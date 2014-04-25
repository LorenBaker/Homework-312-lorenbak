package com.lbconsulting.homework_312_lorenbak.adapters;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lbconsulting.homework_312_lorenbak.MyLog;
import com.lbconsulting.homework_312_lorenbak.R;
import com.lbconsulting.homework_312_lorenbak.database.RSS_ImagesTable;
import com.lbconsulting.homework_312_lorenbak.database.RSS_ItemsTable;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class TitlesCursorAdaptor extends CursorAdapter {

	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

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
				// long imageID = cursor.getLong(cursor.getColumnIndexOrThrow(RSS_ItemsTable.COL_IMAGE_ID));
				String channelImageURL = "";
				if (channelID > 1) {
					Cursor imageCursor = RSS_ImagesTable.getImage(context, channelID);

					if (imageCursor != null) {
						imageCursor.moveToFirst();
						channelImageURL = imageCursor.getString(
								imageCursor.getColumnIndexOrThrow(RSS_ImagesTable.COL_URL));
						imageCursor.close();
					}
				}
				MyLog.i("TitlesCursorAdaptor", "bindView()  channelID:" + channelID + "; url:" + channelImageURL);

				if (!channelImageURL.isEmpty()) {
					/*MainActivity.imageLoader.displayImage(channelImageURL, ivTitleIcon, MainActivity.options,
							animateFirstListener);*/
				}

				// TODO display image -- may need use a joined table query.
			}

			// long itemID = cursor.getLong(cursor.getColumnIndexOrThrow(RSS_ItemsTable.COL_ITEM_ID));
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

	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}

}
