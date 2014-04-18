package com.lbconsulting.homework_312_lorenbak;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParserException;

import android.content.res.AssetManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.lbconsulting.homework_312_lorenbak.TitlesFragment.OnTitleSelected;
import com.lbconsulting.homework_312_lorenbak.RSSreader.RSS_Parser;

public class MainActivity extends ActionBarActivity implements ActionBar.OnNavigationListener, OnTitleSelected,
		SensorEventListener {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	private long mActiveChannelID = 1;

	// private String DATA_FILENAME = "sample-rss-2.xml";

	// private String DATA_FILENAME = "GoogleNews.download.xml";

	private String DATA_FILENAME = "Yahoo.download.xml";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		// Set up the dropdown list navigation in the action bar.
		actionBar.setListNavigationCallbacks(
				// Specify a SpinnerAdapter to populate the dropdown list.
				new ArrayAdapter<String>(
						actionBar.getThemedContext(),
						android.R.layout.simple_list_item_1,
						android.R.id.text1,
						new String[] {
								getString(R.string.title_section1),
								getString(R.string.title_section2),
								getString(R.string.title_section3),
						}),
				this);
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
			/*RSS_Channel channelParseResult = RSS_Parser.parse(input);*/
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
	public boolean onNavigationItemSelected(int position, long id) {
		// When the given dropdown item is selected, show its contents in the
		// container view.
		// TODO replace mActiveChannelID with id
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, TitlesFragment.newInstance(mActiveChannelID))
				.commit();
		return true;
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	/*	public static class PlaceholderFragment extends Fragment {

			*//**
		* The fragment argument representing the section number for this
		* fragment.
		*/
	/*
	private static final String ARG_SECTION_NUMBER = "section_number";

	*//**
		* Returns a new instance of this fragment for the given section
		* number.
		*/
	/*
	public static PlaceholderFragment newInstance(int sectionNumber) {
	PlaceholderFragment fragment = new PlaceholderFragment();
	Bundle args = new Bundle();
	args.putInt(ARG_SECTION_NUMBER, sectionNumber);
	fragment.setArguments(args);
	return fragment;
	}

	public PlaceholderFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
	View rootView = inflater.inflate(R.layout.fragment_main, container, false);
	TextView textView = (TextView) rootView.findViewById(R.id.section_label);
	textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
	return rootView;
	}
	}*/

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnItemSelected(long itemID) {
		// TODO Auto-generated method stub

	}

}
