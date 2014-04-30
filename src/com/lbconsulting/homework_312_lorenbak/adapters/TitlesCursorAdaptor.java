package com.lbconsulting.homework_312_lorenbak.adapters;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lbconsulting.homework_312_lorenbak.MainActivity;
import com.lbconsulting.homework_312_lorenbak.MyLog;
import com.lbconsulting.homework_312_lorenbak.R;
import com.lbconsulting.homework_312_lorenbak.database.RSS_ImagesTable;
import com.lbconsulting.homework_312_lorenbak.database.RSS_ItemsTable;

public class TitlesCursorAdaptor extends CursorAdapter {

	public TitlesCursorAdaptor(Context context, Cursor c, int flags) {
		super(context, c, flags);
		MyLog.i("TitlesCursorAdaptor", "TitlesCursorAdaptor constructor.");
	}

	@Override
	public void bindView(View view, final Context context, final Cursor cursor) {
		if (cursor != null && view != null) {

			TextView tvPubDate = (TextView) view.findViewById(R.id.tvPubDate);
			if (tvPubDate != null) {
				long pubDateValue = cursor.getLong(cursor.getColumnIndexOrThrow(RSS_ItemsTable.COL_PUB_DATE));
				int isUpdated = cursor.getInt(cursor.getColumnIndexOrThrow(RSS_ItemsTable.COL_ITEM_UPDATED));
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(pubDateValue);

				SimpleDateFormat format = new SimpleDateFormat("cccc, MMMM d, yyyy,  h:mm a",
						java.util.Locale.getDefault());
				String pubDate = format.format(cal.getTime());
				if (isUpdated == 1) {
					pubDate = "Updated: " + pubDate;
				}
				tvPubDate.setText(pubDate);
			}

			TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
			if (tvTitle != null) {
				String title = cursor.getString(cursor.getColumnIndexOrThrow(RSS_ItemsTable.COL_TITLE));
				tvTitle.setText(title);
			}

			ImageView ivTitleIcon = (ImageView) view.findViewById(R.id.ivTitleIcon);
			if (ivTitleIcon != null) {
				long channelID = cursor.getLong(cursor.getColumnIndexOrThrow(RSS_ItemsTable.COL_CHANNEL_ID));
				String imageKey = "";
				if (channelID > 1) {
					Cursor imageCursor = RSS_ImagesTable.getImage(context, channelID);

					if (imageCursor != null && imageCursor.getCount() > 0) {
						imageCursor.moveToFirst();
						imageKey = String.valueOf(imageCursor.getLong(imageCursor
								.getColumnIndexOrThrow(RSS_ImagesTable.COL_IMAGES_ID)));
						imageCursor.close();
						Bitmap imageBitmap = MainActivity.getMemoryCache().get(imageKey);
						if (imageBitmap != null) {
							ivTitleIcon.setImageBitmap(imageBitmap);
							MyLog.i("TitlesCursorAdaptor", "bindView()  channelID:" + channelID + "; imageKey:"
									+ imageKey);
						} else {
							MyLog.d("TitlesCursorAdaptor", "bindView(): imageBitmap is null!  channelID:" + channelID
									+ "; imageKey:"
									+ imageKey);
						}
					}
				}
			}

			int isArticleSelected = cursor.getInt(cursor.getColumnIndexOrThrow(RSS_ItemsTable.COL_ITEM_SELECTED));
			int isArticleRead = cursor.getInt(cursor.getColumnIndexOrThrow(RSS_ItemsTable.COL_ITEM_READ));

			if (isArticleRead == 1) {
				view.setBackgroundColor(context.getResources().getColor(R.color.greyLight2));

			} else {
				view.setBackgroundColor(context.getResources().getColor(android.R.color.background_light));
			}

			if (isArticleSelected == 1) {
				view.setBackgroundColor(context.getResources().getColor(R.color.blueLight));
			}
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// MyLog.i("TitlesCursorAdaptor", "newView()");
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.row_title, parent, false);
		return view;
	}

}
