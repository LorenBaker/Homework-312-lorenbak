package com.lbconsulting.homework_312_lorenbak;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.lbconsulting.homework_312_lorenbak.RSSreader.RSS_Parser;
import com.lbconsulting.homework_312_lorenbak.adapters.NewsFeedsSpinnerCursorAdapter;
import com.lbconsulting.homework_312_lorenbak.database.RSS_ChannelsTable;
import com.lbconsulting.homework_312_lorenbak.database.RSS_ImagesTable;
import com.lbconsulting.homework_312_lorenbak.fragments.TitlesFragment;
import com.lbconsulting.homework_312_lorenbak.fragments.TitlesFragment.OnArticleSelected;
import com.lbconsulting.homework_312_lorenbak.image_management.DiskLruImageCache;

public class MainActivity extends ActionBarActivity implements ActionBar.OnNavigationListener, OnArticleSelected,
		SensorEventListener, LoaderManager.LoaderCallbacks<Cursor> {

	public static final String SHARED_PREFERENCES_NAME = "HW312_shared_preferences";

	public static final String STATE_SELECTED_CHANNEL_ID = "selected_channel_id";
	public static final String STATE_SELECTED_CHANNEL_POSITION = "selected_channel_position";
	private long mSelectedChannelID = 1;
	private int mSelectedChannelPosition = 0;

	public static final String STATE_SELECTED_ARTICLE_ID = "selected_article_id";
	public static final String STATE_SELECTED_ARTICLE_POSITION = "selected_article_position";
	private long mSelectedArticleID = -1;
	private int mSelectedArticlePosition = -1;

	public static final String STATE_TITLES_LV_TOP = "lv_top";
	public static final String STATE_TITLES_LV_FIRST_VISIBLE_POSITION = "lv_first_visible_position";
	private int mListViewFirstVisiblePosition = 0;
	private int mListViewTop = 0;

	public static final String STATE_PAGER_NOTIFICATION = "pager_notification";

	private static final String FRAGMENT_TITLES = "frag_titles";
	private TitlesFragment mTitlesFragment;

	private static LruCache<String, Bitmap> mMemoryCache;
	private static DiskLruImageCache mDiskCache;
	private static int DISK_CACHE_SIZE = 1024 * 1024 * 16; // 16mb in bytes
	public static String DISK_CACH_DIRECTORY = "HW312_Images";

	public static LruCache<String, Bitmap> getMemoryCache() {
		return mMemoryCache;
	}

	public static DiskLruImageCache getDiskCache() {
		return mDiskCache;
	}

	private SensorManager mSensorManager;
	private Sensor mAccelerometerSensor;

	private final float SHAKE_EVENT_TRIGGER = (float) 150;
	private final float ALPAH = (float) 0.8;
	private final float ONE_MINUS_ALPHA = 1 - ALPAH;
	private float gravity[] = { 0, 0, 0 };
	private float linear_acceleration[] = { 0, 0, 0 };
	private float previous_linear_acceleration[] = { 0, 0, 0 };
	private long mPrevious_time = System.currentTimeMillis();

	private TextView tvEmptyFragTitles;
	private TextProgressBar pbLoadingIndicator;

	private LoaderManager mLoaderManager = null;
	private LoaderManager.LoaderCallbacks<Cursor> mNewsFeedsCallbacks;
	private NewsFeedsSpinnerCursorAdapter mNewsFeedsCursorAdapter;
	private static final int NEWS_FEEDS_LOADER_ID = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyLog.i("Main_ACTIVITY", "onCreate()");
		setContentView(R.layout.activity_main);

		tvEmptyFragTitles = (TextView) findViewById(R.id.tvEmptyFragTitles);
		pbLoadingIndicator = (TextProgressBar) findViewById(R.id.pbLoadingIndicator);
		if (pbLoadingIndicator != null) {
			pbLoadingIndicator.setText("Loading Articles");
		}

		// setup mMemoryCache AND mDiskCache
		// Get max available VM memory
		// Exceeding this amount will throw an OutOfMemory exception.
		// Stored in kilobytes as LruCache takes an int in its constructor.
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		// Use 1/8th of the available memory for this memory cache.
		final int cacheSize = maxMemory / 32;
		MyLog.i("Main_ACTIVITY", "onCreate(); mMemoryCache cacheSize:" + cacheSize);

		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {

			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				// The cache size will be measured in kilobytes
				// rather than the number of items.
				return bitmap.getByteCount() / 1024;
			}
		};
		mDiskCache = new DiskLruImageCache(this, DISK_CACH_DIRECTORY, DISK_CACHE_SIZE, CompressFormat.PNG, 80);

		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		// Set up the adapter for the ActionBar dropdown list
		mNewsFeedsCursorAdapter = new NewsFeedsSpinnerCursorAdapter(this, null, 0);
		mNewsFeedsCallbacks = this;
		mLoaderManager = getLoaderManager();

		// Set up the dropdown list navigation in the action bar.
		actionBar.setListNavigationCallbacks(
				// Specify a Adapter to populate the dropdown list.
				mNewsFeedsCursorAdapter, this);

		// verify that news feeds exist ... if not create some.
		Cursor newsFeedURLs = RSS_ChannelsTable.getAllNewsFeedsCursor(this);
		if (newsFeedURLs == null || newsFeedURLs.getCount() == 0) {
			// No news feeds exist in the database ... so create some
			String[] urls = this.getResources().getStringArray(R.array.newsFeedURLs);
			String[] titles = this.getResources().getStringArray(R.array.newsFeedTitles);
			int i = 0;
			String title = null;
			for (String url : urls) {
				title = titles[i];
				RSS_ChannelsTable.CreateChannel(this, url, title);
				i++;
			}
			if (tvEmptyFragTitles != null) {
				tvEmptyFragTitles.setVisibility(View.VISIBLE);
			}
			initNewsFeedsLoader();

		} else {
			// News feeds exist in the database ...
			// get stored images
			Cursor cursor = RSS_ImagesTable.getAllImages(this);
			if (cursor != null && cursor.getCount() > 0) {
				// load images from the disk cache into the memory cache
				// get all of the image IDs
				ArrayList<String> imageIDs = new ArrayList<String>();
				long imageID = -1;
				while (cursor.moveToNext()) {
					imageID = cursor.getLong(cursor.getColumnIndexOrThrow(RSS_ImagesTable.COL_IMAGES_ID));
					imageIDs.add(String.valueOf(imageID));
				}
				// start an AsyncTask to load images from the persisted disk storage
				new LoadImagesTask().execute(imageIDs);
				cursor.close();
			}

		}
		if (newsFeedURLs != null) {
			newsFeedURLs.close();
		}

	}

	private void initNewsFeedsLoader() {
		mLoaderManager.initLoader(NEWS_FEEDS_LOADER_ID, null, mNewsFeedsCallbacks);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		switch (item.getItemId()) {
			case R.id.action_refresh:
				LoadArticles();
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onPause() {
		MyLog.i("Main_ACTIVITY", "onPause()");
		mSensorManager.unregisterListener(this);

		SharedPreferences settings = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
		SharedPreferences.Editor storedStates = settings.edit();
		storedStates.putLong(STATE_SELECTED_ARTICLE_ID, mSelectedArticleID);
		if (mSelectedArticlePosition < 0) {
			mSelectedArticlePosition = 0;
		}
		storedStates.putInt(STATE_SELECTED_ARTICLE_POSITION, mSelectedArticlePosition);
		storedStates.putLong(STATE_SELECTED_CHANNEL_ID, mSelectedChannelID);
		storedStates.putInt(STATE_SELECTED_CHANNEL_POSITION, getSupportActionBar().getSelectedNavigationIndex());

		storedStates.putInt(MainActivity.STATE_TITLES_LV_FIRST_VISIBLE_POSITION, mListViewFirstVisiblePosition);
		storedStates.putInt(MainActivity.STATE_TITLES_LV_TOP, mListViewTop);

		storedStates.commit();
		super.onPause();
	}

	@Override
	protected void onResume() {
		MyLog.i("Main_ACTIVITY", "onResume()");
		mSensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);

		SharedPreferences storedStates = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
		mSelectedArticleID = storedStates.getLong(STATE_SELECTED_ARTICLE_ID, -1);
		mSelectedArticlePosition = storedStates.getInt(STATE_SELECTED_ARTICLE_POSITION, 0);

		mSelectedChannelID = storedStates.getLong(STATE_SELECTED_CHANNEL_ID, 1);
		mSelectedChannelPosition = storedStates.getInt(STATE_SELECTED_CHANNEL_POSITION, 0);

		mListViewFirstVisiblePosition = storedStates.getInt(MainActivity.STATE_TITLES_LV_FIRST_VISIBLE_POSITION, 0);
		mListViewTop = storedStates.getInt(MainActivity.STATE_TITLES_LV_TOP, 0);

		super.onResume();
	}

	public void ShowLoadingIndicator() {

		if (pbLoadingIndicator != null) {
			pbLoadingIndicator.setVisibility(View.VISIBLE);
		}

		android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
		if (fm.findFragmentByTag(FRAGMENT_TITLES) != null) {
			fm.beginTransaction()
					.remove(mTitlesFragment)
					.commit();
		}
		if (tvEmptyFragTitles != null) {
			tvEmptyFragTitles.setVisibility(View.GONE);
		}
	}

	public void DismissLoadingIndicator() {

		if (pbLoadingIndicator != null) {
			pbLoadingIndicator.setVisibility(View.GONE);
		}

		mTitlesFragment = TitlesFragment.newInstance(mSelectedChannelID, mSelectedArticlePosition);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, mTitlesFragment, FRAGMENT_TITLES)
				.commit();

		if (tvEmptyFragTitles != null) {
			tvEmptyFragTitles.setVisibility(View.GONE);
		}
	}

	public void LoadArticles() {
		new LoadArticlesTask().execute();
	}

	private class LoadArticlesTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			ShowLoadingIndicator();
		}

		@Override
		protected Void doInBackground(Void... dataFilename) {
			RefreshArticles();
			LoadChannelIcons();
			LoadArticleImages();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			DismissLoadingIndicator();
			super.onPostExecute(result);
		}

	}

	private void RefreshArticles() {

		Cursor channelsCursor = RSS_ChannelsTable.getNewsFeedsCursor(this, mSelectedChannelID);
		URL channelNewsFeedURL = null;
		String channelNewsFeed = "";
		long channelID = -1;
		URLConnection connection = null;
		HttpURLConnection httpURLConnection = null;
		int responseCode = -1;
		InputStream channelNewsFeedInputStream = null;

		if (channelsCursor != null && channelsCursor.getCount() > 0) {
			while (channelsCursor.moveToNext()) {
				channelID = channelsCursor.getLong(channelsCursor
						.getColumnIndexOrThrow(RSS_ChannelsTable.COL_CHANNEL_ID));
				channelNewsFeed = channelsCursor.getString(channelsCursor
						.getColumnIndexOrThrow(RSS_ChannelsTable.COL_NEWS_FEED_URL));
				if (channelID > 1) {
					try {
						// Create news feed URL
						channelNewsFeedURL = new URL(channelNewsFeed);
						// Create new HTTP URL connection
						connection = channelNewsFeedURL.openConnection();
						httpURLConnection = (HttpURLConnection) connection;

						responseCode = httpURLConnection.getResponseCode();
						if (responseCode == HttpURLConnection.HTTP_OK) {
							// Get the news feed input stream
							channelNewsFeedInputStream = httpURLConnection.getInputStream();
							// Parse the input steam and save data to the database
							RSS_Parser.parse(this, channelID, channelNewsFeedInputStream);
							channelNewsFeedInputStream.close();
						}

					} catch (MalformedURLException e) {
						MyLog.e("Main_ACTIVITY", "RefreshArticles(): MalformedURLException opening news feed: "
								+ channelNewsFeed + ". " + e.getMessage());
					} catch (IOException e) {
						MyLog.e("Main_ACTIVITY", "RefreshArticles(): IOException opening news feed: " + channelNewsFeed
								+ ". " + e.getMessage());
					} catch (XmlPullParserException e) {
						MyLog.e("Main_ACTIVITY", "RefreshArticles(): XmlPullParserException opening news feed: "
								+ channelNewsFeed
								+ ". " + e.getMessage());
					}
				}
			}
		}

		if (channelsCursor != null) {
			channelsCursor.close();
		}
	}

	private void LoadArticleImages() {
		Cursor cursor = RSS_ImagesTable.getItemImageURLs(this);
		String imageUrl = "";
		long imageID = -1;
		Bitmap image = null;
		String key = "";
		if (cursor != null) {
			while (cursor.moveToNext()) {
				imageID = cursor.getLong(cursor.getColumnIndexOrThrow(RSS_ImagesTable.COL_IMAGES_ID));
				key = String.valueOf(imageID);
				imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(RSS_ImagesTable.COL_URL));
				if (imageUrl != null && !imageUrl.isEmpty() && imageID > 0) {
					if (!getDiskCache().containsKey(key)) {
						// load the image from the web
						image = getImageFromWeb(imageUrl);
						if (image != null) {
							getDiskCache().put(key, image);
						}
					}
				}
			}
			cursor.close();
		}
	}

	private void LoadChannelIcons() {
		Cursor cursor = RSS_ImagesTable.getChannelImageURLs(this);
		String imageUrl = "";
		long imageID = -1;
		Bitmap image = null;
		String key = "";
		if (cursor != null) {
			while (cursor.moveToNext()) {
				imageID = cursor.getLong(cursor.getColumnIndexOrThrow(RSS_ImagesTable.COL_IMAGES_ID));
				key = String.valueOf(imageID);
				imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(RSS_ImagesTable.COL_URL));
				if (imageUrl != null && !imageUrl.isEmpty() && imageID > 0) {
					if (!getDiskCache().containsKey(key)) {
						// load the image from the web
						image = getImageFromWeb(imageUrl);
						if (image != null) {
							getMemoryCache().put(key, image);
							getDiskCache().put(key, image);
						}
					}
				}
			}
			cursor.close();
		}
	}

	private static Bitmap getImageFromWeb(String url) {
		Bitmap bitmapImage = null;
		if (url != null && !url.isEmpty()) {
			try {
				InputStream is = (InputStream) new URL(url).getContent();
				bitmapImage = BitmapFactory.decodeStream(is);
				if (is != null) {
					is.close();
				}

			} catch (Exception e) {
				MyLog.e("Main_ACTIVITY", "ERROR in getImageFromWeb(): " + e.getMessage());
			}
		}
		return bitmapImage;
	}

	private class LoadImagesTask extends AsyncTask<ArrayList<String>, Void, Void> {

		@Override
		protected void onPreExecute() {
			// Do nothing
		}

		@Override
		protected Void doInBackground(ArrayList<String>... passing) {
			ArrayList<String> imageIDs = passing[0]; // get passed arraylist
			for (String id : imageIDs) {
				if (getDiskCache().containsKey(id)) {
					getMemoryCache().put(id, getDiskCache().getBitmap(id));
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			initNewsFeedsLoader();
			super.onPostExecute(result);
		}

	}

	@Override
	public boolean onNavigationItemSelected(int position, long channelID) {
		MyLog.i("Main_ACTIVITY", "onNavigationItemSelected(); channelID = " + channelID);
		mSelectedChannelPosition = position;
		mSelectedChannelID = channelID;
		mListViewFirstVisiblePosition = 0;
		mListViewTop = 0;
		mTitlesFragment = TitlesFragment.newInstance(mSelectedChannelID, mSelectedArticlePosition);

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, mTitlesFragment, FRAGMENT_TITLES)
				.commit();

		return true;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// do nothing

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor == mAccelerometerSensor) {

			long curTime = System.currentTimeMillis();
			long diffTime = (curTime - mPrevious_time);
			if (diffTime > 100) {

				// alpha is calculated as t / (t + dT)
				// with t, the low-pass filter's time-constant
				// and dT, the event delivery rate

				gravity[0] = ALPAH * gravity[0] + (ONE_MINUS_ALPHA) * event.values[0];
				gravity[1] = ALPAH * gravity[1] + (ONE_MINUS_ALPHA) * event.values[1];
				gravity[2] = ALPAH * gravity[2] + (ONE_MINUS_ALPHA) * event.values[2];

				linear_acceleration[0] = event.values[0] - gravity[0];
				linear_acceleration[1] = event.values[1] - gravity[1];
				linear_acceleration[2] = event.values[2] - gravity[2];

				float shakeEventStrength =
						(
								Math.abs(linear_acceleration[0] - previous_linear_acceleration[0])
										+ Math.abs(linear_acceleration[1] - previous_linear_acceleration[1])
										+ Math.abs(linear_acceleration[2] - previous_linear_acceleration[2])
								) / diffTime * 1000;

				// MyLog.i("Main_ACTIVITY", "onSensorChanged(): shakeEventStrength:" + shakeEventStrength);

				if (shakeEventStrength > SHAKE_EVENT_TRIGGER) {
					// MyLog.d("Main_ACTIVITY", "onSensorChanged(): shakeEventStrength >" + SHAKE_EVENT_TRIGGER);
					LoadArticles();
				}

				previous_linear_acceleration[0] = linear_acceleration[0];
				previous_linear_acceleration[1] = linear_acceleration[1];
				previous_linear_acceleration[2] = linear_acceleration[2];
				mPrevious_time = curTime;
			}
		}

	}

	@Override
	public void onArticleSelected(int position, long articleID) {
		// start the NewsArticleActivity
		mSelectedArticlePosition = position;
		Intent newsArticleActivityIntent = new Intent(this, NewsArticleActivity.class);
		newsArticleActivityIntent.putExtra(STATE_SELECTED_ARTICLE_ID, articleID);
		newsArticleActivityIntent.putExtra(STATE_SELECTED_CHANNEL_ID, mSelectedChannelID);
		newsArticleActivityIntent.putExtra(STATE_SELECTED_ARTICLE_POSITION, mSelectedArticlePosition);
		startActivity(newsArticleActivityIntent);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		MyLog.i("Main_ACTIVITY", "onCreateLoader(); id = " + id);
		CursorLoader cursorLoader = RSS_ChannelsTable.getAllNewsFeeds(this);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor newCursor) {
		int id = loader.getId();
		MyLog.i("Main_ACTIVITY", "onLoadFinished(); id = " + id);
		mNewsFeedsCursorAdapter.swapCursor(newCursor);
		getSupportActionBar().setSelectedNavigationItem(mSelectedChannelPosition);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		int id = loader.getId();
		MyLog.i("Main_ACTIVITY", "onLoaderReset(); id = " + id);
		mNewsFeedsCursorAdapter.swapCursor(null);

	}

}
