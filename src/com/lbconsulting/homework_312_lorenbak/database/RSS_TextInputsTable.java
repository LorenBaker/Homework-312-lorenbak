package com.lbconsulting.homework_312_lorenbak.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.lbconsulting.homework_312_lorenbak.MyLog;

public class RSS_TextInputsTable {

	/*A channel may optionally contain a <textInput> sub-element, which contains four required sub-elements.
	 * <title> -- The label of the Submit button in the text input area.
	 * <description> -- Explains the text input area.
	 * <name> -- The name of the text object in the text input area.
	 * <link> -- The URL of the CGI script that processes text input requests.
	 * 
	 * The purpose of the <textInput> element is something of a mystery. 
	 * You can use it to specify a search engine box. Or to allow a reader to provide feedback. 
	 * Most aggregators ignore it.*/

	// Version 1
	public static final String TABLE_TEXT_INPUTS = "tblTextInputs";
	public static final String COL_TEXT_INPUT_ID = "_id";
	public static final String COL_CHANNEL_ID = "channelID";
	public static final String COL_TITLE = "title";
	public static final String COL_DESCRIPTION = "description";
	public static final String COL_NAME = "name";
	public static final String COL_LINK = "link";

	public static final String[] PROJECTION_ALL = { COL_TEXT_INPUT_ID, COL_CHANNEL_ID,
			COL_TITLE, COL_DESCRIPTION, COL_NAME, COL_LINK };

	public static final String CONTENT_PATH = "textInputs";
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + "vnd.lbconsulting."
			+ CONTENT_PATH;
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + "vnd.lbconsulting."
			+ CONTENT_PATH;
	public static final Uri CONTENT_URI = Uri.parse("content://" + RSS_ContentProvider.AUTHORITY + "/" + CONTENT_PATH);

	// Database creation SQL statements
	private static final String DATATABLE_CREATE = "create table " + TABLE_TEXT_INPUTS
			+ " ("
			+ COL_TEXT_INPUT_ID + " integer primary key autoincrement, "
			+ COL_CHANNEL_ID + " integer default -1, "
			+ COL_TITLE + " text collate nocase, "
			+ COL_DESCRIPTION + " text collate nocase, "
			+ COL_NAME + " text collate nocase, "
			+ COL_LINK + " text collate nocase "
			+ ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATATABLE_CREATE);
		MyLog.i("RSS_TextInputsTable", "onCreate: " + TABLE_TEXT_INPUTS + " created.");

	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		MyLog.w(TABLE_TEXT_INPUTS, "Upgrading database from version " + oldVersion + " to version " + newVersion);
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_TEXT_INPUTS);
		onCreate(database);
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Create Methods
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static long CreateTextInputs(Context context, long channelID, ContentValues textInputContextValues) {
		long newItemID = -1;
		Cursor TextInputsCursor = null;

		if (channelID > 0) {
			// determine if the TextInputs for the provided channel is already in the database
			TextInputsCursor = getTextInputs(context, channelID);
			if (TextInputsCursor != null && TextInputsCursor.getCount() > 0) {
				// the TextInputs for the provided channel exist in the database
				// so delete it
				DeleteAllTextInputsInChannel(context, channelID);
			}

			// the TextInputs for the provided channel does not exist in the database ... so create it
			textInputContextValues.put(COL_CHANNEL_ID, channelID);
			Uri uri = CONTENT_URI;
			ContentResolver cr = context.getContentResolver();
			Uri newItemUri = cr.insert(uri, textInputContextValues);
			if (newItemUri != null) {
				newItemID = Long.parseLong(newItemUri.getLastPathSegment());
			}
		}

		if (TextInputsCursor != null) {
			TextInputsCursor.close();
		}
		return newItemID;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Read Methods
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static Cursor getTextInputs(Context context, long channelID) {
		Uri uri = CONTENT_URI;
		String[] projection = PROJECTION_ALL;
		String selection = COL_CHANNEL_ID + " = ?";
		String selectionArgs[] = new String[] { String.valueOf(channelID) };
		String sortOrder = null;

		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
		} catch (Exception e) {
			MyLog.e("RSS_TextInputsTable", "Exception error in getTextInputs:");
			e.printStackTrace();
		}
		return cursor;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Update Methods
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static int UpdateTextInputslFieldValues(Context context, long channelID, ContentValues newFieldValues) {
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

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Delete Methods
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static int DeleteAllTextInputsInChannel(Context context, long channelID) {
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

}
