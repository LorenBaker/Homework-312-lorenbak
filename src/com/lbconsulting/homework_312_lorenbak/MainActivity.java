package com.lbconsulting.homework_312_lorenbak;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParserException;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.lbconsulting.homework_312_lorenbak.RSSreader.RSS_Parser;
import com.lbconsulting.homework_312_lorenbak.adapters.NewsFeedsSpinnerCursorAdapter;
import com.lbconsulting.homework_312_lorenbak.database.RSS_ChannelsTable;
import com.lbconsulting.homework_312_lorenbak.fragments.TitlesFragment;
import com.lbconsulting.homework_312_lorenbak.fragments.TitlesFragment.OnArticleSelected;

public class MainActivity extends ActionBarActivity implements ActionBar.OnNavigationListener, OnArticleSelected,
		SensorEventListener, LoaderManager.LoaderCallbacks<Cursor> {

	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	private long mActiveChannelID = 1;
	private int mActivePosition = -1;
	private int mChannelSpinnerPosition = 0;

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
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getSupportActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getSupportActionBar().getSelectedNavigationIndex());
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

			case R.id.action_discardItems:
				Toast.makeText(this, "\"" + item.getTitle() + "\"" + " is under construction.",
						Toast.LENGTH_SHORT).show();
				DiscardItems();
				return true;

			case R.id.action_acceptItems:
				Toast.makeText(this, "\"" + item.getTitle() + "\"" + " is under construction.",
						Toast.LENGTH_SHORT).show();
				AcceptItems();
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void RefreshItems() {
		AssetManager assetManager = getAssets();
		InputStream input = null;

		try {
			input = assetManager.open(DATA_FILENAME);
			RSS_Parser.parse(this, input);
			if (input != null) {
				input.close();
			}

		} catch (IOException e) {
			MyLog.e("Main_ACTIVITY", "RefreshItems(): IOException opening " + DATA_FILENAME);
			e.printStackTrace();

		} catch (XmlPullParserException e) {
			MyLog.e("Main_ACTIVITY", "RefreshItems(): XmlPullParserException parsing " + DATA_FILENAME);
			e.printStackTrace();
		} finally {

		}
	}

	private void DiscardItems() {
		// TODO Auto-generated method stub

	}

	private void AcceptItems() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onNavigationItemSelected(int position, long channelID) {
		// When the given dropdown item is selected, show its contents in the
		// container view.
		mActiveChannelID = channelID;
		mChannelSpinnerPosition = position;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, TitlesFragment.newInstance(mActiveChannelID))
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
