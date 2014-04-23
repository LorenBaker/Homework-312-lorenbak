package com.lbconsulting.homework_312_lorenbak;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.xmlpull.v1.XmlPullParserException;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.lbconsulting.homework_312_lorenbak.RSSreader.RSS_Parser;
import com.lbconsulting.homework_312_lorenbak.adapters.NewsFeedsSpinnerCursorAdapter;
import com.lbconsulting.homework_312_lorenbak.database.RSS_ChannelsTable;
import com.lbconsulting.homework_312_lorenbak.fragments.TitlesFragment;
import com.lbconsulting.homework_312_lorenbak.fragments.TitlesFragment.OnArticleSelected;

public class MainActivity extends ActionBarActivity implements ActionBar.OnNavigationListener, OnArticleSelected,
		SensorEventListener, LoaderManager.LoaderCallbacks<Cursor> {

	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	private long mActiveChannelID = 1;
	private long mActiveArticleID = -1;
	private int mActivePosition = -1;
	// private int mChannelSpinnerPosition = 0;

	private LoaderManager mLoaderManager = null;
	private LoaderManager.LoaderCallbacks<Cursor> mNewsFeedsCallbacks;
	private NewsFeedsSpinnerCursorAdapter mNewsFeedsCursorAdapter;
	private static final int NEWS_FEEDS_LOADER_ID = 2;

	// private String DATA_FILENAME = "sample-rss-2.xml";
	// private String DATA_FILENAME = "GoogleNews.download.xml";
	private String DATA_FILENAME = "Yahoo.download.xml";

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

	private void RefreshItems() {
		String xmlURL = "http://news.yahoo.com/rss/world/";
		String rssFeed = null;
		try {
			rssFeed = getRSSxml(xmlURL);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			InputStream input = new ByteArrayInputStream(rssFeed.getBytes("UTF-8"));

			RSS_Parser.parse(this, input);
			if (input != null) {
				input.close();
			}
			mActivePosition = -1;

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*		AssetManager assetManager = getAssets();
				InputStream input = null;

				try {
					input = assetManager.open(DATA_FILENAME);
					RSS_Parser.parse(this, input);
					if (input != null) {
						input.close();
					}
					mActivePosition = -1;

				} catch (IOException e) {
					MyLog.e("Main_ACTIVITY", "RefreshItems(): IOException opening " + DATA_FILENAME);
					e.printStackTrace();

				} catch (XmlPullParserException e) {
					MyLog.e("Main_ACTIVITY", "RefreshItems(): XmlPullParserException parsing " + DATA_FILENAME);
					e.printStackTrace();
				} finally {

				}*/
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
