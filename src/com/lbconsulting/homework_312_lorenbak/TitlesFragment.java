package com.lbconsulting.homework_312_lorenbak;

import android.app.Activity;
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
import android.widget.TextView;

import com.lbconsulting.homework_312_lorenbak.database.RSS_ItemsTable;

public class TitlesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

	OnTitleSelected mOnTitleSelectedCallback;

	// Container Activity must implement this interface
	public interface OnTitleSelected {

		public void OnItemSelected(long itemID);
	}

	private TitlesCursorAdaptor mItemsCursorAdaptor;
	private ListView mTitlesListView;
	private TextProgressBar pbLoadingIndicator;
	private TextView tvEmptyFragTitles;

	private int ITEMS_LOADER_ID = 1;
	private LoaderManager mLoaderManager = null;
	private LoaderManager.LoaderCallbacks<Cursor> mTitlesFragmentCallbacks;

	private long mActiveChannelID = 1;

	public TitlesFragment() {
		// Empty constructor
	}

	public static TitlesFragment newInstance(long activeChannelID) {
		TitlesFragment f = new TitlesFragment();
		// Supply activeChannelID input as an argument.
		Bundle args = new Bundle();
		args.putLong("activeChannelID", activeChannelID);
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
			mOnTitleSelectedCallback = (OnTitleSelected) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnItemSelectedCallback");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		MyLog.i("TitlesFragment", "onCreateView()");

		if (savedInstanceState != null && savedInstanceState.containsKey("activeChannelID")) {
			mActiveChannelID = savedInstanceState.getLong("activeChannelID", 1);
		} else {
			Bundle bundle = getArguments();
			if (bundle != null)
				mActiveChannelID = bundle.getLong("activeChannelID", 1);
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
							nr = 0;
							mOnTitleSelectedCallback.OnItemSelected(0);
							RSS_ItemsTable.DeleteAllSelectedItems(getActivity());
							mode.finish();
							break;

						default:
							break;
					}
					return false;
				}

				@Override
				public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
					if (checked) {
						nr++;
					} else {
						nr--;
					}
					RSS_ItemsTable.setItemSelection(getActivity(), id, checked);
					mode.setTitle(nr + " selected");

				}

			});

			mTitlesListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View v, int position, long itemID) {
					mOnTitleSelectedCallback.OnItemSelected(itemID);
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

		tvEmptyFragTitles = (TextView) view.findViewById(R.id.tvEmptyFragTitles);
		pbLoadingIndicator = (TextProgressBar) view.findViewById(R.id.pbLoadingIndicator);
		if (pbLoadingIndicator != null) {
			pbLoadingIndicator.setText("Loading Articles");
		}

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
		outState.putLong("activeChannelID", this.mActiveChannelID);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onPause() {
		MyLog.i("TitlesFragment", "onPause()");
		super.onPause();
	}

	@Override
	public void onResume() {
		MyLog.i("TitlesFragment", "onResume()");
		Bundle bundle = this.getArguments();
		if (bundle != null) {
			mActiveChannelID = bundle.getLong("activeChannelID", 1);
		}
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
		CursorLoader cursorLoader = RSS_ItemsTable.getAllItems(getActivity(), mActiveChannelID,
				RSS_ItemsTable.SORT_ORDER_PUB_DATE);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor newCursor) {
		int id = loader.getId();
		MyLog.i("TitlesFragment", "onLoadFinished: LoaderID = " + id);
		mItemsCursorAdaptor.swapCursor(newCursor);
		if (newCursor != null && newCursor.getCount() > 0) {
			mTitlesListView.setVisibility(View.VISIBLE);
			tvEmptyFragTitles.setVisibility(View.GONE);
		} else {
			mTitlesListView.setVisibility(View.GONE);
			tvEmptyFragTitles.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		int id = loader.getId();
		MyLog.i("TitlesFragment", "onLoaderReset: LoaderID = " + id);
		mItemsCursorAdaptor.swapCursor(null);
	}

	public void ShowLoadingIndicator() {

		if (pbLoadingIndicator != null) {
			pbLoadingIndicator.setVisibility(View.VISIBLE);
		}
		if (mTitlesListView != null) {
			mTitlesListView.setVisibility(View.GONE);
		}
		if (tvEmptyFragTitles != null) {
			tvEmptyFragTitles.setVisibility(View.GONE);
		}
	}

	public void DismissLoadingIndicator() {

		if (pbLoadingIndicator != null) {
			pbLoadingIndicator.setVisibility(View.GONE);
		}
		if (mTitlesListView != null) {
			mTitlesListView.setVisibility(View.VISIBLE);
		}
		if (tvEmptyFragTitles != null) {
			tvEmptyFragTitles.setVisibility(View.GONE);
		}
	}

	/*	public void LoadArticles(String dataFilename) {
			new LoadArticlesTask().execute(dataFilename);
		}*/

	/*	private void RefreshArticles(String dataFilename) {
			AssetManager assetManager = getActivity().getAssets();
			InputStream input = null;
			try {
				input = assetManager.open(dataFilename);
				ArticlesParser.parse(getActivity(), input);
				input.close();

			} catch (IOException e) {
				MyLog.e("Titles_ACTIVITY", "RefreshArticles(): IOException opening " + dataFilename);
				e.printStackTrace();

			} catch (XmlPullParserException e) {
				MyLog.e("Titles_ACTIVITY", "RefreshArticles(): XmlPullParserException parsing " + dataFilename);
				e.printStackTrace();
			}
		}*/

	/*	private class LoadArticlesTask extends AsyncTask<String, Void, Void> {

			@Override
			protected void onPreExecute() {
				ShowLoadingIndicator();
			}

			@Override
			protected Void doInBackground(String... dataFilename) {
				// simulate an Internet download
				try {
					Thread.sleep(3500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				RefreshArticles(dataFilename[0]);
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				DismissLoadingIndicator();
				super.onPostExecute(result);
			}

		}*/

}
