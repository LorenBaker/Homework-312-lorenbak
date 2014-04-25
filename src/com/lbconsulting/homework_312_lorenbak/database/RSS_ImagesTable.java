package com.lbconsulting.homework_312_lorenbak.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.lbconsulting.homework_312_lorenbak.MyLog;

public class RSS_ImagesTable {

	/*<image> is an optional sub-element of <channel>
	 * It contains three required and three optional sub-elements.
	 * The required elements are:
	 * <url> is the URL of a GIF, JPEG or PNG image that represents the channel.
	 * <title> describes the image, it's used in the ALT attribute of the HTML <img> tag when the channel is rendered in HTML.
	 * <link> is the URL of the site, when the channel is rendered, the image is a link to the site. 
	 * (Note, in practice the image <title> and <link> should have the same value as the channel's <title> and <link>.
	 * 
	 * Optional elements include 
	 * <width> and <height>, numbers, indicating the width and height of the image in pixels. 
	 * 		Maximum value for width is 144, default value is 88.
	 * 		Maximum value for height is 400, default value is 31.
	 * <description> contains text that is included in the TITLE attribute of the link formed around the image in the HTML rendering.*/

	// Version 1
	public static final String TABLE_IMAGES = "tlbImages";
	public static final String COL_IMAGES_ID = "_id";
	public static final String COL_CHANNEL_ID = "channelID";
	public static final String COL_ITEM_ID = "itemID";

	// required elements
	public static final String COL_URL = "url";
	public static final String COL_TITLE = "title";
	public static final String COL_LINK = "link";

	// optional elements
	public static final String COL_WIDTH = "width";
	public static final String COL_HEIGHT = "height";
	public static final String COL_DESCRIPTION = "description";

	public static final String[] PROJECTION_ALL = { COL_IMAGES_ID, COL_CHANNEL_ID, COL_ITEM_ID,
			COL_URL, COL_TITLE, COL_LINK, COL_WIDTH, COL_HEIGHT, COL_DESCRIPTION
	};

	public static final String[] PROJECTION_IMAGE_URL = { COL_IMAGES_ID, COL_URL };

	public static final String CONTENT_PATH = "images";
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + "vnd.lbconsulting."
			+ CONTENT_PATH;
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + "vnd.lbconsulting."
			+ CONTENT_PATH;
	public static final Uri CONTENT_URI = Uri.parse("content://" + RSS_ContentProvider.AUTHORITY + "/" + CONTENT_PATH);

	// Database creation SQL statements
	private static final String DATATABLE_CREATE = "create table " + TABLE_IMAGES
			+ " ("
			+ COL_IMAGES_ID + " integer primary key autoincrement, "
			+ COL_CHANNEL_ID + " integer default -1, "
			+ COL_ITEM_ID + " integer default -1, "

			+ COL_URL + " text collate nocase, "
			+ COL_TITLE + " text collate nocase, "
			+ COL_LINK + " text collate nocase, "

			+ COL_WIDTH + " integer default 88, "
			+ COL_HEIGHT + " integer default 31, "
			+ COL_DESCRIPTION + " text collate nocase "
			+ ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATATABLE_CREATE);
		MyLog.i("RSS_ImagesTable", "onCreate: " + TABLE_IMAGES + " created.");

	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		MyLog.w(TABLE_IMAGES, "Upgrading database from version " + oldVersion + " to version " + newVersion);
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGES);
		onCreate(database);
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Create Methods
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static long CreateImage(Context context, long channelID, ContentValues imageContentValues) {
		long newItemID = -1;
		Cursor ImageCursor = null;

		if (channelID > 0) {
			// determine if the Image for the provided channel is already in the database
			ImageCursor = getImage(context, channelID);
			if (ImageCursor != null && ImageCursor.getCount() > 0) {
				// the Images for the provided channel exist in the database
				// so delete it
				DeleteImage(context, channelID);
			}

			// the Image for the provided channel does not exist in the database ... so create it
			// ContentValues newFieldValues = new ContentValues();
			imageContentValues.put(COL_CHANNEL_ID, channelID);
			imageContentValues.put(COL_ITEM_ID, -1);
			Uri uri = CONTENT_URI;
			ContentResolver cr = context.getContentResolver();
			Uri newItemUri = cr.insert(uri, imageContentValues);
			if (newItemUri != null) {
				newItemID = Long.parseLong(newItemUri.getLastPathSegment());
			}
		}

		if (ImageCursor != null) {
			ImageCursor.close();
		}
		return newItemID;
	}

	public static long CreateImage(Context context, long channelID, long itemID, ContentValues imageContentValues) {
		long newItemID = -1;
		Cursor ImageCursor = null;

		if (channelID > 0 && itemID > 0) {
			// determine if the Image for the provided channel is already in the database
			ImageCursor = getImage(context, channelID, itemID);
			if (ImageCursor != null && ImageCursor.getCount() > 0) {
				// the Images for the provided channel exist in the database
				// so delete it
				DeleteImage(context, channelID, itemID);
			}

			// the Image for the provided channel does not exist in the database ... so create it
			// ContentValues newFieldValues = new ContentValues();
			imageContentValues.put(COL_CHANNEL_ID, channelID);
			imageContentValues.put(COL_ITEM_ID, itemID);
			Uri uri = CONTENT_URI;
			ContentResolver cr = context.getContentResolver();
			Uri newItemUri = cr.insert(uri, imageContentValues);
			if (newItemUri != null) {
				newItemID = Long.parseLong(newItemUri.getLastPathSegment());
			}
		}

		if (ImageCursor != null) {
			ImageCursor.close();
		}
		return newItemID;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Read Methods
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static Cursor getImage(Context context, long channelID) {
		Cursor cursor = null;
		if (channelID > 0) {
			Uri uri = CONTENT_URI;
			String[] projection = PROJECTION_ALL;
			String selection = COL_CHANNEL_ID + " = ? AND " + COL_ITEM_ID + " = ?";
			String selectionArgs[] = new String[] { String.valueOf(channelID), String.valueOf(-1) };
			String sortOrder = null;

			ContentResolver cr = context.getContentResolver();

			try {
				cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
			} catch (Exception e) {
				MyLog.e("RSS_ImagesTable", "Exception error in getImage:");
				e.printStackTrace();
			}
		}
		return cursor;
	}

	public static Cursor getImage(Context context, long channelID, long itemID) {
		Cursor cursor = null;
		if (channelID > 0 && itemID > 0) {
			Uri uri = CONTENT_URI;
			String[] projection = PROJECTION_ALL;
			String selection = COL_CHANNEL_ID + " = ? AND " + COL_ITEM_ID + " = ?";
			String selectionArgs[] = new String[] { String.valueOf(channelID), String.valueOf(itemID) };
			String sortOrder = null;

			ContentResolver cr = context.getContentResolver();

			try {
				cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
			} catch (Exception e) {
				MyLog.e("RSS_ImagesTable", "Exception error in getImage:");
				e.printStackTrace();
			}
		}
		return cursor;
	}

	public static Cursor getChannelImageURLs(Context context) {
		Cursor cursor = null;
		Uri uri = CONTENT_URI;
		String[] projection = PROJECTION_IMAGE_URL;
		String selection = COL_ITEM_ID + " = ?";
		String selectionArgs[] = new String[] { String.valueOf(-1) };
		String sortOrder = null;

		ContentResolver cr = context.getContentResolver();

		try {
			cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
		} catch (Exception e) {
			MyLog.e("RSS_ImagesTable", "Exception error in getChannelImageURLs: " + e.getMessage());
		}
		return cursor;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Update Methods
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static int UpdateImageslFieldValues(Context context, long channelID, ContentValues newFieldValues) {
		int numberOfUpdatedRecords = -1;
		if (channelID > 0) {
			ContentResolver cr = context.getContentResolver();
			Uri channelUri = CONTENT_URI;
			String selection = COL_CHANNEL_ID + " = ?";
			String selectionArgs[] = new String[] { String.valueOf(channelID) };
			numberOfUpdatedRecords = cr.update(channelUri, newFieldValues, selection, selectionArgs);
		}
		return numberOfUpdatedRecords;
	}

	public static int UpdateImageslFieldValues(Context context, long channelID, long itemID,
			ContentValues newFieldValues) {
		int numberOfUpdatedRecords = -1;
		if (channelID > 0 && itemID > 0) {
			ContentResolver cr = context.getContentResolver();
			Uri channelUri = CONTENT_URI;
			String selection = COL_CHANNEL_ID + " = ? AND " + COL_ITEM_ID + " = ?";
			String selectionArgs[] = new String[] { String.valueOf(channelID), String.valueOf(itemID) };
			numberOfUpdatedRecords = cr.update(channelUri, newFieldValues, selection, selectionArgs);
		}
		return numberOfUpdatedRecords;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Delete Methods
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static int DeleteAllImagesInChannel(Context context, long channelID) {
		int numberOfDeletedRecords = -1;
		if (channelID > 0) {
			ContentResolver cr = context.getContentResolver();
			Uri uri = CONTENT_URI;
			String selection = COL_CHANNEL_ID + " = ?";
			String selectionArgs[] = new String[] { String.valueOf(channelID) };
			numberOfDeletedRecords = cr.delete(uri, selection, selectionArgs);
		}
		return numberOfDeletedRecords;
	}

	private static int DeleteImage(Context context, long channelID) {
		int numberOfDeletedRecords = -1;
		if (channelID > 0) {
			ContentResolver cr = context.getContentResolver();
			Uri uri = CONTENT_URI;
			String selection = COL_CHANNEL_ID + " = ? AND " + COL_ITEM_ID + " = ?";
			String selectionArgs[] = new String[] { String.valueOf(channelID), String.valueOf(-1) };
			numberOfDeletedRecords = cr.delete(uri, selection, selectionArgs);
		}
		return numberOfDeletedRecords;
	}

	private static int DeleteImage(Context context, long channelID, long itemID) {
		int numberOfDeletedRecords = -1;
		if (channelID > 0 && itemID > 0) {
			ContentResolver cr = context.getContentResolver();
			Uri uri = CONTENT_URI;
			String selection = COL_CHANNEL_ID + " = ? AND " + COL_ITEM_ID + " = ?";
			String selectionArgs[] = new String[] { String.valueOf(channelID), String.valueOf(itemID) };
			numberOfDeletedRecords = cr.delete(uri, selection, selectionArgs);
		}
		return numberOfDeletedRecords;
	}

}
