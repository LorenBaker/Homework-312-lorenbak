package com.lbconsulting.homework_312_lorenbak.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lbconsulting.homework_312_lorenbak.MyLog;
import com.lbconsulting.homework_312_lorenbak.R;
import com.lbconsulting.homework_312_lorenbak.database.RSS_ChannelsTable;

public class NewsFeedsSpinnerCursorAdapter extends CursorAdapter {

	public NewsFeedsSpinnerCursorAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		MyLog.i("NewsFeedsSpinnerCursorAdapter", "NewsFeedsSpinnerCursorAdapter constructor.");
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		if (cursor != null) {
			switch (view.getId()) {
				case R.id.llNewsFeedRow:
					TextView tvNewsFeedRow = (TextView) view.findViewById(R.id.tvNewsFeedRow);
					if (tvNewsFeedRow != null) {
						tvNewsFeedRow
								.setText(cursor.getString(cursor.getColumnIndexOrThrow(RSS_ChannelsTable.COL_TITLE)));
					}
					break;

				case R.id.llNewsFeedRowDropdown:
					TextView tvNewsFeedRowDropdown = (TextView) view.findViewById(R.id.tvNewsFeedRowDropdown);
					if (tvNewsFeedRowDropdown != null) {
						tvNewsFeedRowDropdown.setText(cursor.getString(cursor
								.getColumnIndexOrThrow(RSS_ChannelsTable.COL_TITLE)));
					}
					break;
				default:
					break;
			}
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View v = null;
		LayoutInflater inflater = LayoutInflater.from(context);
		v = inflater.inflate(R.layout.row_news_feeds_spinner, parent, false);
		bindView(v, context, cursor);
		return v;
	}

	@Override
	public View newDropDownView(Context context, Cursor cursor, ViewGroup parent) {
		View v = null;
		LayoutInflater inflater = LayoutInflater.from(context);
		v = inflater.inflate(R.layout.row_news_feeds_spinner_dropdown, parent, false);
		bindView(v, context, cursor);
		return v;
	}

}
