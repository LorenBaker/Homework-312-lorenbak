package com.lbconsulting.homework_312_lorenbak;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.xmlpull.v1.XmlPullParserException;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.lbconsulting.homework_312_lorenbak.RSSreader.RSS_Parser;
import com.lbconsulting.homework_312_lorenbak.adapters.NewsFeedsSpinnerCursorAdapter;
import com.lbconsulting.homework_312_lorenbak.database.RSS_ChannelsTable;
import com.lbconsulting.homework_312_lorenbak.database.RSS_ImagesTable;
import com.lbconsulting.homework_312_lorenbak.fragments.TitlesFragment;
import com.lbconsulting.homework_312_lorenbak.fragments.TitlesFragment.OnArticleSelected;

public class MainActivity extends ActionBarActivity implements ActionBar.OnNavigationListener, OnArticleSelected,
		SensorEventListener, LoaderManager.LoaderCallbacks<Cursor> {

	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	private long mActiveChannelID = 1;
	private long mActiveArticleID = -1;
	private int mActivePosition = -1;
	// private int mChannelSpinnerPosition = 0;

	private LruCache<String, Bitmap> mMemoryCache;
	private TextProgressBar pbLoadingIndicator;

	/*public static ImageLoader imageLoader = ImageLoader.getInstance();
	public static DisplayImageOptions options;*/

	public LruCache<String, Bitmap> getMemoryCache() {
		return mMemoryCache;
	}

	private LoaderManager mLoaderManager = null;
	private LoaderManager.LoaderCallbacks<Cursor> mNewsFeedsCallbacks;
	private NewsFeedsSpinnerCursorAdapter mNewsFeedsCursorAdapter;
	private static final int NEWS_FEEDS_LOADER_ID = 2;

	// private String DATA_FILENAME2 = "sample-rss-2.xml";
	private String DATA_FILENAME1 = "GoogleNews.download.xml";
	private String DATA_FILENAME2 = "Yahoo.download.xml";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyLog.i("Main_ACTIVITY", "onCreate()");
		setContentView(R.layout.activity_main);

		// verify that news feeds exist ... if not create some.
		Cursor newsFeedURLs = RSS_ChannelsTable.getAllNewsFeedsCursor(this);
		if (newsFeedURLs == null || newsFeedURLs.getCount() == 0) {
			// No news feeds exist ... so create some
			String[] urls = this.getResources().getStringArray(R.array.newsFeedURLs);
			String[] titles = this.getResources().getStringArray(R.array.newsFeedTitles);
			int i = 0;
			String title = null;
			for (String url : urls) {
				title = titles[i];
				RSS_ChannelsTable.CreateChannel(this, url, title);
				i++;
			}
		}
		if (newsFeedURLs != null) {
			newsFeedURLs.close();
		}

		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		// set up the universal image loader
		/*options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error)
				.cacheInMemory(true)
				.cacheOnDisc(true)
				.considerExifParams(true)
				// .displayer(new RoundedBitmapDisplayer(20))
				.build();*/

		// setup mMemoryCache

		// Get max available VM memory, exceeding this amount will throw an
		// OutOfMemory exception. Stored in kilobytes as LruCache takes an
		// int in its constructor.
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

		// Use 1/8th of the available memory for this memory cache.
		final int cacheSize = maxMemory / 8;

		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {

			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				// The cache size will be measured in kilobytes rather than
				// number of items.
				return bitmap.getByteCount() / 1024;
			}
		};

		// Set up the adapter for the ActionBar dropdown list
		mNewsFeedsCursorAdapter = new NewsFeedsSpinnerCursorAdapter(this, null, 0);
		mNewsFeedsCallbacks = this;
		mLoaderManager = getLoaderManager();
		mLoaderManager.initLoader(NEWS_FEEDS_LOADER_ID, null, mNewsFeedsCallbacks);

		// Set up the dropdown list navigation in the action bar.
		actionBar.setListNavigationCallbacks(
				// Specify a Adapter to populate the dropdown list.
				mNewsFeedsCursorAdapter, this);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		MyLog.i("Main_ACTIVITY", "onRestoreInstanceState()");
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getSupportActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}

		if (savedInstanceState.containsKey("ActiveArticleID")) {
			mActiveArticleID = savedInstanceState.getLong("ActiveArticleID");
		}

		if (savedInstanceState.containsKey("ActiveChannelID")) {
			mActiveChannelID = savedInstanceState.getLong("ActiveChannelID");
		}

		if (savedInstanceState.containsKey("ActivePosition")) {
			mActivePosition = savedInstanceState.getInt("ActivePosition");
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		MyLog.i("Main_ACTIVITY", "onSaveInstanceState()");
		// Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getSupportActionBar().getSelectedNavigationIndex());
		outState.putLong("ActiveArticleID", mActiveArticleID);
		outState.putLong("ActiveChannelID", mActiveChannelID);
		outState.putInt("ActivePosition", mActivePosition);
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
				RefreshItems();
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onPause() {
		MyLog.i("Main_ACTIVITY", "onPause()");
		super.onPause();
	}

	@Override
	protected void onResume() {
		MyLog.i("Main_ACTIVITY", "onResume()");
		/*		SharedPreferences storedStates = getSharedPreferences("HW312", MODE_PRIVATE);
				mActiveArticleID = storedStates.getLong("ActiveArticleID", -1);
				mActiveChannelID = storedStates.getLong("ActiveChannelID", -1);
				mActivePosition = storedStates.getInt("ActivePosition", -1);

				if (mActivePosition > -1) {
					mPager.setCurrentItem(mActivePosition);
				}*/
		super.onResume();
	}

	public void ShowLoadingIndicator() {

		if (pbLoadingIndicator != null) {
			pbLoadingIndicator.setVisibility(View.VISIBLE);
		}
		/*if (mTitlesListView != null) {
			mTitlesListView.setVisibility(View.GONE);
		}
		if (tvEmptyFragTitles != null) {
			tvEmptyFragTitles.setVisibility(View.GONE);
		}*/
	}

	public void DismissLoadingIndicator() {

		if (pbLoadingIndicator != null) {
			pbLoadingIndicator.setVisibility(View.GONE);
		}
		/*if (mTitlesListView != null) {
			mTitlesListView.setVisibility(View.VISIBLE);
		}
		if (tvEmptyFragTitles != null) {
			tvEmptyFragTitles.setVisibility(View.GONE);
		}*/
	}

	public void LoadArticles(String dataFilename) {
		new LoadArticlesTask().execute(dataFilename);
	}

	private class LoadArticlesTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			ShowLoadingIndicator();
		}

		@Override
		protected Void doInBackground(Void... dataFilename) {

			// Suppress content provider notifying loaders of changes
			// until after all article data has been updated in the database
			// HW311ContentProvider.setSupressUpdates(true);
			RefreshArticles();

			// Simulate an Internet download to allow the loading indicator
			// to be seen for a reasonable period of time
			try {
				Thread.sleep(2500);
			} catch (InterruptedException e) {
				MyLog.e("Titles_ACTIVITY",
						"doInBackground(): InterruptedException " + dataFilename + "\n" + e.toString());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// Allow normal notification of updates
			// HW311ContentProvider.setSupressUpdates(false);
			// Restart the loader to show database update changes
			// mLoaderManager.restartLoader(ITEMS_LOADER_ID, null, mTitlesFragmentCallbacks);
			// Hide the loading indicator and show the article list
			DismissLoadingIndicator();

			super.onPostExecute(result);
		}

	}

	private void RefreshArticles() {

		AssetManager assetManager = getAssets();
		InputStream input1 = null;
		InputStream input2 = null;

		try {
			input1 = assetManager.open(DATA_FILENAME1);
			RSS_Parser.parse(this, input1);
			if (input1 != null) {
				input1.close();
			}

		} catch (IOException e) {
			MyLog.e("Main_ACTIVITY", "RefreshItems(): IOException opening " + DATA_FILENAME1);
			e.printStackTrace();

		} catch (XmlPullParserException e) {
			MyLog.e("Main_ACTIVITY", "RefreshItems(): XmlPullParserException parsing " + DATA_FILENAME1);
			e.printStackTrace();
		} finally {

		}

		try {
			input2 = assetManager.open(DATA_FILENAME2);
			RSS_Parser.parse(this, input2);
			if (input2 != null) {
				input2.close();
			}
			mActivePosition = -1;

		} catch (IOException e) {
			MyLog.e("Main_ACTIVITY", "RefreshItems(): IOException opening " + DATA_FILENAME2);
			e.printStackTrace();

		} catch (XmlPullParserException e) {
			MyLog.e("Main_ACTIVITY", "RefreshItems(): XmlPullParserException parsing " + DATA_FILENAME2);
			e.printStackTrace();
		} finally {

		}
		
		Bitmap bitmap;
		BitmapFactory.Options options = new BitmapFactory.Options();
		/*options.inJustDecodeBounds= true;
		BitmapFactory.decodeFile(path, options); // Not really allocating pixels
*/		options.inJustDecodeBounds= false;
		
		// fill the mMemoryCache with images
		Cursor imagesCursor = RSS_ImagesTable.getAllImages();
		if(imagesCursor!=null) {
			imagesCursor.moveToPosition(-1);
			while (imagesCursor.moveToNext()){
				String imageURL = imagesCursor.getString(imagesCursor.getColumnIndexOrThrow(RSS_ImagesTable.COL_URL));
				URL(imageURL).
			}
			
		}
	}

	private String getRSSxml(String xmlURL) throws MalformedURLException {
		URL url = new URL(xmlURL);
		HttpURLConnection urlConnection = null;
		String xmlData = "";
		try {
			urlConnection = (HttpURLConnection) url.openConnection();
			InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			byte buffer[] = new byte[4096];
			int count;

			while ((count = in.read(buffer)) != -1) {
				xmlData += new String(buffer, 0, count);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
		}

		return xmlData;
	}

	@Override
	public boolean onNavigationItemSelected(int position, long channelID) {
		MyLog.i("Main_ACTIVITY", "onNavigationItemSelected(); channelID = " + channelID);
		// When the given dropdown item is selected, show its contents in the
		// container view.
		mActiveChannelID = channelID;
		// mChannelSpinnerPosition = position;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, TitlesFragment.newInstance(mActiveChannelID, mActivePosition))
				.commit();
		return true;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onArticleSelected(int position, long articleID) {
		// start the NewsArticleActivity
		mActivePosition = position;
		Intent newsArticleActivityIntent = new Intent(this, NewsArticleActivity.class);
		newsArticleActivityIntent.putExtra("ActiveArticleID", articleID);
		newsArticleActivityIntent.putExtra("ActiveChannelID", mActiveChannelID);
		newsArticleActivityIntent.putExtra("ActivePosition", mActivePosition);
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

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		int id = loader.getId();
		MyLog.i("Main_ACTIVITY", "onLoaderReset(); id = " + id);
		mNewsFeedsCursorAdapter.swapCursor(null);

	}

}
