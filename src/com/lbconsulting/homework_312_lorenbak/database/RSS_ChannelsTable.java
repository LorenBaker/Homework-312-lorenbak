package com.lbconsulting.homework_312_lorenbak.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.lbconsulting.homework_312_lorenbak.MyLog;

public class RSS_ChannelsTable {

	// Version 1
	public static final String TABLE_CHANNELS = "tblChannels";
	// Required elements
	public static final String COL_CHANNEL_ID = "_id";
	public static final String COL_NEWS_FEED_URL = "newsFeedURL";
	public static final String COL_TITLE = "title";
	public static final String COL_LINK = "link";
	public static final String COL_DESCRIPTION = "description";
	// optional elements
	public static final String COL_CATEGORY = "category";
	public static final String COL_CATEGORY_DOMAIN = "category_domain";
	public static final String COL_CLOUD = "cloud";
	public static final String COL_COPYRIGHT = "copyright";
	public static final String COL_DOCS = "cocs";
	public static final String COL_GENERATOR = "generator";
	public static final String COL_IMAGE_ID = "imageID";
	public static final String COL_LANGUAGE = "language";
	public static final String COL_LAST_BUILD_DATE = "lastBuildDate";
	public static final String COL_MANAGING_EDITOR = "managingEditor";
	public static final String COL_PUB_DATE = "pubDate";
	public static final String COL_RATING = "rating";
	public static final String COL_SKIP_DAYS_ID = "skipDaysID";
	public static final String COL_SKIP_HOURS_ID = "skipHoursID";
	public static final String COL_TEXT_INPUT_ID = "textInputID";
	public static final String COL_TTL = "TTL";
	public static final String COL_WEBMASTER = "webMaster";
	public static final String COL_LAST_REFRESH_DATE_TIME = "lastDateTimeRefreshed";

	public static final String[] PROJECTION_ALL = { COL_CHANNEL_ID, COL_NEWS_FEED_URL, COL_TITLE, COL_LINK,
			COL_DESCRIPTION, COL_CATEGORY, COL_CATEGORY_DOMAIN, COL_CLOUD, COL_COPYRIGHT, COL_DOCS, COL_GENERATOR,
			COL_IMAGE_ID, COL_LANGUAGE, COL_LAST_BUILD_DATE, COL_MANAGING_EDITOR, COL_PUB_DATE, COL_RATING,
			COL_SKIP_DAYS_ID, COL_SKIP_HOURS_ID, COL_TEXT_INPUT_ID, COL_TTL, COL_WEBMASTER, COL_LAST_REFRESH_DATE_TIME
	};

	public static final String[] PROJECTION_REQUIRED_ELEMENTS = { COL_CHANNEL_ID, COL_TITLE, COL_LINK, COL_DESCRIPTION,
			COL_IMAGE_ID, COL_LAST_REFRESH_DATE_TIME };

	public static final String[] PROJECTION_NEWS_FEED_URLs = { COL_CHANNEL_ID, COL_NEWS_FEED_URL, COL_TITLE };

	public static final String CONTENT_PATH = "channels";
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + "vnd.lbconsulting."
			+ CONTENT_PATH;
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + "vnd.lbconsulting."
			+ CONTENT_PATH;
	public static final Uri CONTENT_URI = Uri.parse("content://" + RSS_ContentProvider.AUTHORITY + "/" + CONTENT_PATH);

	public static final String SORT_ORDER_TITLE = COL_TITLE + " ASC";
	public static final String SORT_ORDER_PUB_DATE = COL_PUB_DATE + " ASC";
	public static final String SORT_ORDER_NEW_FEED_URL = COL_NEWS_FEED_URL + " ASC";

	// Database creation SQL statements
	private static final String DATATABLE_CREATE = "create table " + TABLE_CHANNELS
			+ " ("
			// Required elements
			+ COL_CHANNEL_ID + " integer primary key autoincrement, "
			+ COL_NEWS_FEED_URL + " text collate nocase, "
			+ COL_TITLE + " text collate nocase, "
			+ COL_LINK + " text collate nocase, "
			+ COL_DESCRIPTION + " text collate nocase, "

			// optional elements
			+ COL_CATEGORY + " text collate nocase, "
			+ COL_CATEGORY_DOMAIN + " text collate nocase, "
			+ COL_CLOUD + " text collate nocase, "
			+ COL_COPYRIGHT + " text collate nocase, "
			+ COL_DOCS + " text collate nocase, "
			+ COL_GENERATOR + " text collate nocase, "
			+ COL_IMAGE_ID + " integer default -1, "
			+ COL_LANGUAGE + " text collate nocase, "
			+ COL_LAST_BUILD_DATE + " integer default -1, "
			+ COL_MANAGING_EDITOR + " text collate nocase, "
			+ COL_PUB_DATE + " integer default -1, "
			+ COL_RATING + " text collate nocase, "
			+ COL_SKIP_DAYS_ID + " integer default -1, "
			+ COL_SKIP_HOURS_ID + " integer default -1, "
			+ COL_TEXT_INPUT_ID + " integer default -1, "
			+ COL_TTL + " text collate nocase, "
			+ COL_WEBMASTER + " text collate nocase, "
			+ COL_LAST_REFRESH_DATE_TIME + " integer default -1"
			+ ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATATABLE_CREATE);
		MyLog.i("RSS_ChannelsTable", "onCreate: " + TABLE_CHANNELS + " created.");

	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		MyLog.w(TABLE_CHANNELS, "Upgrading database from version " + oldVersion + " to version " + newVersion);
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_CHANNELS);
		onCreate(database);
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Create Methods
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static long CreateChannel(Context context, String newsFeedURL, String title) {
		long newChannelID = -1;
		Cursor channelCursor = null;
		if (newsFeedURL != null && !newsFeedURL.isEmpty()) {
			// check to see if the news feed is already in the database
			channelCursor = getNewsFeedCursor(context, newsFeedURL);
			if (channelCursor != null && channelCursor.getCount() > 0) {
				// the news feed already exists in the database
				newChannelID = channelCursor.getLong(channelCursor.getColumnIndexOrThrow(COL_CHANNEL_ID));
				channelCursor.close();
				return newChannelID;
			}

			// the news feed is NOT in the database ... so create it.
			Uri uri = CONTENT_URI;
			ContentResolver cr = context.getContentResolver();
			ContentValues cv = new ContentValues();
			cv.put(COL_NEWS_FEED_URL, newsFeedURL);
			cv.put(COL_TITLE, title);
			Uri newChannelUri = cr.insert(uri, cv);
			if (newChannelUri != null) {
				newChannelID = Long.parseLong(newChannelUri.getLastPathSegment());
			}
		}
		if (channelCursor != null) {
			channelCursor.close();
		}
		return newChannelID;
	}

	public static long CreateChannel(Context context, ContentValues channelRequiredContentValues) {

		String channelTitle = channelRequiredContentValues.getAsString("title");
		String channelDescription = channelRequiredContentValues.getAsString("description");
		String channelLink = channelRequiredContentValues.getAsString("link"); // newsFeedURL

		long newChannelID = -1;
		Cursor channelCursor = null;

		if (channelLink != null && !channelLink.isEmpty()) {
			// determine if the channel is already in the database
			channelCursor = getNewsFeedCursor(context, channelLink);
			if (channelCursor != null && channelCursor.getCount() > 0) {
				// the channel exists in the database
				// replace the channel's content and update the refresh date/time
				channelCursor.moveToFirst();
				newChannelID = channelCursor.getLong(channelCursor.getColumnIndexOrThrow(COL_CHANNEL_ID));
				ContentValues newFieldValues = new ContentValues();
				newFieldValues.put(COL_DESCRIPTION, channelDescription);
				newFieldValues.put(COL_TITLE, channelTitle);
				UpdateChannelFieldValues(context, newChannelID, newFieldValues);
				channelCursor.close();
				return newChannelID;
			}
			// the channel does not exist in the database
			// so create it.
			Uri uri = CONTENT_URI;
			ContentResolver cr = context.getContentResolver();
			Uri newItemUri = cr.insert(uri, channelRequiredContentValues);
			if (newItemUri != null) {
				newChannelID = Long.parseLong(newItemUri.getLastPathSegment());
			}
		}

		if (channelCursor != null) {
			channelCursor.close();
		}
		return newChannelID;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Read Methods
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////

	private static Cursor getNewsFeedCursor(Context context, String newsFeedURL) {
		Uri uri = CONTENT_URI;
		String[] projection = PROJECTION_NEWS_FEED_URLs;
		String selection = COL_NEWS_FEED_URL + " = ?";
		String selectionArgs[] = new String[] { newsFeedURL };
		String sortOrder = SORT_ORDER_NEW_FEED_URL;

		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
		} catch (Exception e) {
			MyLog.e("RSS_ChannelsTable", "Exception error in getAllNewsFeedsCursor:");
			e.printStackTrace();
		}
		return cursor;
	}

	public static Cursor getAllNewsFeedsCursor(Context context) {
		Uri uri = CONTENT_URI;
		String[] projection = PROJECTION_NEWS_FEED_URLs;
		String selection = null;
		String selectionArgs[] = null;
		String sortOrder = SORT_ORDER_NEW_FEED_URL;

		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
		} catch (Exception e) {
			MyLog.e("RSS_ChannelsTable", "Exception error in getAllNewsFeedsCursor:");
			e.printStackTrace();
		}
		return cursor;
	}

	public static Cursor getNewsFeedsCursor(Context context, long channelID) {
		Uri uri = CONTENT_URI;
		String[] projection = PROJECTION_NEWS_FEED_URLs;
		String selection = COL_CHANNEL_ID + " != ?";
		String selectionArgs[] = new String[] { String.valueOf(1) };
		if (channelID > 1) {
			selection = COL_CHANNEL_ID + " = ?";
			selectionArgs = new String[] { String.valueOf(channelID) };
		}

		String sortOrder = SORT_ORDER_NEW_FEED_URL;

		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
		} catch (Exception e) {
			MyLog.e("RSS_ChannelsTable", "Exception error in getNewsFeedsCursor:");
			e.printStackTrace();
		}
		return cursor;
	}

	public static CursorLoader getAllNewsFeeds(Context context) {
		Uri uri = CONTENT_URI;
		String[] projection = PROJECTION_NEWS_FEED_URLs;
		String selection = null;
		String selectionArgs[] = null;
		String sortOrder = SORT_ORDER_NEW_FEED_URL;
		CursorLoader cursorLoader = null;
		try {
			cursorLoader = new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);
		} catch (Exception e) {
			MyLog.e("RSS_ChannelsTable", "Exception error in getAllNewsFeeds:");
			e.printStackTrace();
		}
		return cursorLoader;
	}

	public static Cursor getChannelAllElements(Context context, long channelID) {
		Uri uri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(channelID));
		String[] projection = PROJECTION_ALL;
		String selection = null;
		String selectionArgs[] = null;
		String sortOrder = null;

		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
		} catch (Exception e) {
			MyLog.e("RSS_ChannelsTable", "Exception error in getChannelAllElements:");
			e.printStackTrace();
		}
		return cursor;
	}

	public static Cursor getChannelRequiredElements(Context context, long channelID) {
		Uri uri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(channelID));
		String[] projection = PROJECTION_REQUIRED_ELEMENTS;
		String selection = null;
		String selectionArgs[] = null;
		String sortOrder = null;

		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
		} catch (Exception e) {
			MyLog.e("RSS_ChannelsTable", "Exception error in getChannelRequiredElements:");
			e.printStackTrace();
		}
		return cursor;
	}

	/*	public static Cursor getChannelRequiredElements(Context context, String newsFeedURL) {
			Uri uri = CONTENT_URI;
			String[] projection = PROJECTION_REQUIRED_ELEMENTS;
			String selection = COL_NEWS_FEED_URL + " = ?";
			String selectionArgs[] = new String[] { newsFeedURL };
			String sortOrder = SORT_ORDER_TITLE;

			ContentResolver cr = context.getContentResolver();
			Cursor cursor = null;
			try {
				cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
			} catch (Exception e) {
				MyLog.e("RSS_ChannelsTable", "Exception error in getChannelRequiredElements:");
				e.printStackTrace();
			}
			return cursor;
		}*/

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Update Methods
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static int UpdateChannelFieldValues(Context context, long channelID, ContentValues newFieldValues) {
		int numberOfUpdatedRecords = -1;
		if (channelID > 0) {
			ContentResolver cr = context.getContentResolver();
			Uri channelUri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(channelID));
			String selection = null;
			String[] selectionArgs = null;
			numberOfUpdatedRecords = cr.update(channelUri, newFieldValues, selection, selectionArgs);
		}
		return numberOfUpdatedRecords;
	}

	public static void setImageID(Context context, long channelID, long imageID) {
		ContentValues newFieldValues = new ContentValues();
		newFieldValues.put(COL_IMAGE_ID, imageID);
		UpdateChannelFieldValues(context, channelID, newFieldValues);
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Delete Methods
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static int DeleteChannel(Context context, long channelID) {
		int numberOfDeletedRecords = -1;
		if (channelID > 0) {
			ContentResolver cr = context.getContentResolver();
			Uri channelUri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(channelID));
			String where = null;
			String[] selectionArgs = null;
			numberOfDeletedRecords = cr.delete(channelUri, where, selectionArgs);

			RSS_ImagesTable.DeleteAllImagesInChannel(context, channelID);
			RSS_ItemsTable.DeleteAllItemsInChannel(context, channelID);
			RSS_SkipDaysTable.DeleteAllSkipDaysInChannel(context, channelID);
			RSS_SkipHoursTable.DeleteAllSkipHoursInChannel(context, channelID);
			RSS_TextInputsTable.DeleteAllTextInputsInChannel(context, channelID);
		}
		return numberOfDeletedRecords;
	}

}
