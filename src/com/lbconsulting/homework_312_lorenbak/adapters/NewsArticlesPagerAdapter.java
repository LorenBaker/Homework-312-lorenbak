package com.lbconsulting.homework_312_lorenbak.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.lbconsulting.homework_312_lorenbak.MyLog;
import com.lbconsulting.homework_312_lorenbak.database.RSS_ItemsTable;
import com.lbconsulting.homework_312_lorenbak.fragments.NewsArticleFragment;

public class NewsArticlesPagerAdapter extends FragmentStatePagerAdapter {

	private Cursor mAllNewsArticlesCursor;
	private Context mContext;
	private long mActiveChannelID = -1;
	private int mCount = 0;

	public NewsArticlesPagerAdapter(FragmentManager fm, Context context, long activeChannelID) {
		super(fm);
		this.mContext = context;
		mActiveChannelID = activeChannelID;
		setAllNewsArticles();
	}

	@Override
	public Fragment getItem(int position) {
		long newsArticleID = getNewsArticleID(position);
		MyLog.d("NewArticlesPagerAdapter", "getItem() - position=" + position + "; newsArticleID=" + newsArticleID);
		Fragment newNewsArticleFragment = NewsArticleFragment.newInstance(mContext, newsArticleID);
		return newNewsArticleFragment;
	}

	@Override
	public int getCount() {
		MyLog.d("NewArticlesPagerAdapter", "getCount() - count=" + mCount);
		return mCount;
	}

	private Cursor setAllNewsArticles() {
		mAllNewsArticlesCursor = RSS_ItemsTable.getAllItemsIDCursor(mContext, mActiveChannelID,
				RSS_ItemsTable.SORT_ORDER_PUB_DATE);
		if (mAllNewsArticlesCursor != null) {
			mCount = mAllNewsArticlesCursor.getCount();
		} else {
			mCount = 0;
		}
		return mAllNewsArticlesCursor;
	}

	private long getNewsArticleID(int position) {
		long newsArticleID = -1;
		try {
			mAllNewsArticlesCursor.moveToPosition(position);
			newsArticleID = mAllNewsArticlesCursor.getLong(mAllNewsArticlesCursor
					.getColumnIndexOrThrow(RSS_ItemsTable.COL_ITEM_ID));
		} catch (Exception e) {
			MyLog.d("NewArticlesPagerAdapter", "Exception in getNewsArticleID: \n" + e.toString());
		}
		return newsArticleID;
	}
}
