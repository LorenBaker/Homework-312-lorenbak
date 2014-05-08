package com.lbconsulting.homework_312_lorenbak.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.lbconsulting.homework_312_lorenbak.MainActivity;
import com.lbconsulting.homework_312_lorenbak.MyLog;
import com.lbconsulting.homework_312_lorenbak.R;
import com.lbconsulting.homework_312_lorenbak.adapters.TitlesCursorAdaptor;
import com.lbconsulting.homework_312_lorenbak.database.RSS_ItemsTable;

public class TitlesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

	OnArticleSelected mOnArticleSelectedCallback;

	// Container Activity must implement this interface
	public interface OnArticleSelected {

		public void onArticleSelected(int position, long articleID);
	}

	private TitlesCursorAdaptor mItemsCursorAdaptor;
	private ListView mTitlesListView;
	/*private TextProgressBar pbLoadingIndicator;
	private TextView tvEmptyFragTitles;*/

	private int ITEMS_LOADER_ID = 1;
	private LoaderManager mLoaderManager = null;
	private LoaderManager.LoaderCallbacks<Cursor> mTitlesFragmentCallbacks;

	private long mSelectedChannelID = 1;
	private int mSelectedArticlePosition = 0;
	private int mListViewFirstVisiblePosition = 0;
	private int mListViewTop = 0;

	public TitlesFragment() {
		// Empty constructor
	}

	public static TitlesFragment newInstance(long activeChannelID, int selectedArticlePosition) {
		TitlesFragment f = new TitlesFragment();
		// Supply activeChannelID input as an argument.
		Bundle args = new Bundle();
		args.putLong(MainActivity.STATE_SELECTED_CHANNEL_ID, activeChannelID);
		args.putInt(MainActivity.STATE_SELECTED_ARTICLE_POSITION, selectedArticlePosition);
		f.setArguments(args);
		return f;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		MyLog.i("TitlesFragment", "onActivityCreated()");
		mLoaderManager = getLoaderManager();
		mLoaderManager.initLoader(ITEMS_LOADER_ID, null, mTitlesFragmentCallbacks);
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		MyLog.i("TitlesFragment", "onAttach()");
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mOnArticleSelectedCallback = (OnArticleSelected) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnItemSelectedCallback");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		MyLog.i("TitlesFragment", "onCreateView()");

		if (savedInstanceState != null && savedInstanceState.containsKey(MainActivity.STATE_SELECTED_CHANNEL_ID)) {
			mSelectedChannelID = savedInstanceState.getLong(MainActivity.STATE_SELECTED_CHANNEL_ID, 1);
			mSelectedArticlePosition = savedInstanceState.getInt(MainActivity.STATE_SELECTED_ARTICLE_POSITION, 0);
			mListViewFirstVisiblePosition = savedInstanceState.getInt(
					MainActivity.STATE_TITLES_LV_FIRST_VISIBLE_POSITION, 0);
			mListViewTop = savedInstanceState.getInt(MainActivity.STATE_TITLES_LV_TOP, 0);

		} else {
			Bundle bundle = getArguments();
			if (bundle != null) {
				mSelectedChannelID = bundle.getLong(MainActivity.STATE_SELECTED_CHANNEL_ID, 1);
				mSelectedArticlePosition = bundle.getInt(MainActivity.STATE_SELECTED_ARTICLE_POSITION, 0);
				mListViewFirstVisiblePosition = bundle.getInt(MainActivity.STATE_TITLES_LV_FIRST_VISIBLE_POSITION, 0);
				mListViewTop = bundle.getInt(MainActivity.STATE_TITLES_LV_TOP, 0);

			}
		}

		View view = inflater.inflate(R.layout.frag_titles_list, container, false);

		mTitlesListView = (ListView) view.findViewById(R.id.itemsListView);
		if (mTitlesListView != null) {
			mItemsCursorAdaptor = new TitlesCursorAdaptor(getActivity(), null, 0);
			mTitlesListView.setAdapter(mItemsCursorAdaptor);

			// set the list view's contextual mode
			mTitlesListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

			mTitlesListView.setMultiChoiceModeListener(new MultiChoiceModeListener() {

				private int nr = 0;

				@Override
				public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
					// Do nothing
					return false;
				}

				@Override
				public void onDestroyActionMode(ActionMode mode) {
					RSS_ItemsTable.DeselectAllSelectedItems(getActivity());
				}

				@Override
				public boolean onCreateActionMode(ActionMode mode, Menu menu) {
					nr = 0;
					MenuInflater contextMenueInflater = getActivity().getMenuInflater();
					contextMenueInflater.inflate(R.menu.contextual_menu, menu);
					return true;
				}

				@Override
				public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
					switch (item.getItemId()) {

						case R.id.item_delete:
							RSS_ItemsTable.DeleteAllSelectedItems(getActivity(), mSelectedChannelID);
							mode.finish();
							break;

						case R.id.select_all_read_articles:
							RSS_ItemsTable.SelectAllReadItems(getActivity(), mSelectedChannelID);
							break;

						case R.id.select_all_articles:
							RSS_ItemsTable.SelectAllItems(getActivity(), mSelectedChannelID);
							break;

						case R.id.deselect_all_articles:
							RSS_ItemsTable.DeselectAllSelectedItems(getActivity());
							break;

						case R.id.select_articles_4hours:
							RSS_ItemsTable.SelectItemsOlderThan(getActivity(), mSelectedChannelID, 4);
							break;

						case R.id.select_articles_1day:
							RSS_ItemsTable.SelectItemsOlderThan(getActivity(), mSelectedChannelID, 24);
							break;

						case R.id.select_articles_2days:
							RSS_ItemsTable.SelectItemsOlderThan(getActivity(), mSelectedChannelID, 48);
							break;

						case R.id.select_articles_1week:
							RSS_ItemsTable.SelectItemsOlderThan(getActivity(), mSelectedChannelID, 7 * 24);
							break;

						default:
							break;
					}
					nr = RSS_ItemsTable.getNumberOfSelectedItems(getActivity(), mSelectedChannelID);
					mode.setTitle(nr + " selected");
					return false;
				}

				@Override
				public void onItemCheckedStateChanged(ActionMode mode, int position, long itemID, boolean checked) {
					// toggle the item's selected state
					boolean isSelected = !RSS_ItemsTable.isItemSelected(getActivity(), itemID);
					if (isSelected) {
						nr++;
					} else {
						nr--;
					}
					RSS_ItemsTable.setItemSelection(getActivity(), itemID, isSelected);
					mode.setTitle(nr + " selected");

				}

			});

			mTitlesListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View v, int position, long articleID) {
					mOnArticleSelectedCallback.onArticleSelected(position, articleID);
				}
			});

			mTitlesListView.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long articleID) {
					mTitlesListView.setItemChecked(position, !RSS_ItemsTable.isItemSelected(getActivity(), articleID));
					return false;
				}
			});

		}

		/*		tvEmptyFragTitles = (TextView) view.findViewById(R.id.tvEmptyFragTitles);
				pbLoadingIndicator = (TextProgressBar) view.findViewById(R.id.pbLoadingIndicator);
				if (pbLoadingIndicator != null) {
					pbLoadingIndicator.setText("Loading Articles");
				}*/

		mTitlesFragmentCallbacks = this;

		return view;
	}

	@Override
	public void onDestroy() {
		MyLog.i("TitlesFragment", "onDestroy()");
		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
		MyLog.i("TitlesFragment", "onDestroyView()");
		super.onDestroyView();
	}

	@Override
	public void onDetach() {
		MyLog.i("TitlesFragment", "onDetach()");
		super.onDetach();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		MyLog.i("TitlesFragment", "onSaveInstanceState");
		View v = mTitlesListView.getChildAt(0);
		int listViewFirstChildTop = (v == null) ? 0 : v.getTop();

		outState.putInt(MainActivity.STATE_SELECTED_ARTICLE_POSITION, mSelectedArticlePosition);
		outState.putLong(MainActivity.STATE_SELECTED_CHANNEL_ID, mSelectedChannelID);
		outState.putInt(MainActivity.STATE_TITLES_LV_FIRST_VISIBLE_POSITION, mTitlesListView.getFirstVisiblePosition());
		outState.putInt(MainActivity.STATE_TITLES_LV_TOP, listViewFirstChildTop);

		super.onSaveInstanceState(outState);
	}

	@Override
	public void onPause() {
		MyLog.i("TitlesFragment", "onPause()");
		View v = mTitlesListView.getChildAt(0);
		int listViewFirstChildTop = (v == null) ? 0 : v.getTop();

		SharedPreferences settings = this.getActivity().getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME,
				Context.MODE_PRIVATE);

		SharedPreferences.Editor storedStates = settings.edit();
		storedStates.putInt(MainActivity.STATE_SELECTED_ARTICLE_POSITION, mSelectedArticlePosition);
		storedStates.putInt(MainActivity.STATE_TITLES_LV_FIRST_VISIBLE_POSITION,
				mTitlesListView.getFirstVisiblePosition());
		storedStates.putInt(MainActivity.STATE_TITLES_LV_TOP, listViewFirstChildTop);

		storedStates.commit();
		super.onPause();
	}

	@Override
	public void onResume() {
		MyLog.i("TitlesFragment", "onResume()");
		SharedPreferences storedStates = this.getActivity().getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		mSelectedChannelID = storedStates.getLong(MainActivity.STATE_SELECTED_CHANNEL_ID, 1);
		mSelectedArticlePosition = storedStates.getInt(MainActivity.STATE_SELECTED_ARTICLE_POSITION, 0);
		mListViewFirstVisiblePosition = storedStates.getInt(MainActivity.STATE_TITLES_LV_FIRST_VISIBLE_POSITION, 0);
		mListViewTop = storedStates.getInt(MainActivity.STATE_TITLES_LV_TOP, 0);

		super.onResume();
	}

	@Override
	public void onStart() {
		MyLog.i("TitlesFragment", "onStart()");
		super.onStart();
	}

	@Override
	public void onStop() {
		MyLog.i("TitlesFragment", "onStop()");
		super.onStop();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		MyLog.i("TitlesFragment", "onCreateLoader(): LoaderId = " + id);
		CursorLoader cursorLoader = RSS_ItemsTable.getAllItems(getActivity(), mSelectedChannelID,
				RSS_ItemsTable.SORT_ORDER_PUB_DATE);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor newCursor) {
		int id = loader.getId();
		MyLog.i("TitlesFragment", "onLoadFinished: LoaderID = " + id);
		mItemsCursorAdaptor.swapCursor(newCursor);

		mTitlesListView.setSelectionFromTop(mListViewFirstVisiblePosition, mListViewTop);

		int childCount = mTitlesListView.getChildCount();
		int lastPositionInView = mListViewFirstVisiblePosition + childCount;
		if (mSelectedArticlePosition > lastPositionInView) {
			mTitlesListView.smoothScrollToPosition(mSelectedArticlePosition + 1);
		} else if (mSelectedArticlePosition < mListViewFirstVisiblePosition) {
			mTitlesListView.smoothScrollToPosition(mSelectedArticlePosition);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		int id = loader.getId();
		MyLog.i("TitlesFragment", "onLoaderReset: LoaderID = " + id);
		mItemsCursorAdaptor.swapCursor(null);
	}

}
