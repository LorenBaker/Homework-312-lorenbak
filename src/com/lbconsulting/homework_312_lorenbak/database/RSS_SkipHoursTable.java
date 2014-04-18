package com.lbconsulting.homework_312_lorenbak.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.SparseIntArray;

import com.lbconsulting.homework_312_lorenbak.MyLog;

public class RSS_SkipHoursTable {

	/*skipHours
	 * A hint for aggregators telling them which hours they can skip. 
	 * This element contains up to 24 <hour> sub-elements whose value is a number between 0 and 23, 
	 * representing a time in GMT, when aggregators, if they support the feature, may not read the channel 
	 * on hours listed in the <skipHours> element. The hour beginning at midnight is hour zero.*/

	// Version 1
	public static final String TABLE_SKIP_HOURS = "tblSkipHours";
	public static final String COL_SKIP_HOURS_ID = "_id";
	public static final String COL_CHANNEL_ID = "channelID";
	public static final String COL_HR00 = "HR00"; // col2
	public static final String COL_HR01 = "HR01";
	public static final String COL_HR02 = "HR02";
	public static final String COL_HR03 = "HR03";
	public static final String COL_HR04 = "HR04";
	public static final String COL_HR05 = "HR05";
	public static final String COL_HR06 = "HR06";
	public static final String COL_HR07 = "HR07";
	public static final String COL_HR08 = "HR08";
	public static final String COL_HR09 = "HR09";
	public static final String COL_HR10 = "HR10";
	public static final String COL_HR11 = "HR11";
	public static final String COL_HR12 = "HR12";
	public static final String COL_HR13 = "HR13";
	public static final String COL_HR14 = "HR14";
	public static final String COL_HR15 = "HR15";
	public static final String COL_HR16 = "HR16";
	public static final String COL_HR17 = "HR17";
	public static final String COL_HR18 = "HR18";
	public static final String COL_HR19 = "HR19";
	public static final String COL_HR20 = "HR20";
	public static final String COL_HR21 = "HR21";
	public static final String COL_HR22 = "HR22";
	public static final String COL_HR23 = "HR23"; // col25

	public static final String[] PROJECTION_ALL = { COL_SKIP_HOURS_ID, COL_CHANNEL_ID,
			COL_HR00, COL_HR01, COL_HR02, COL_HR03, COL_HR04, COL_HR05,
			COL_HR06, COL_HR07, COL_HR08, COL_HR09, COL_HR10, COL_HR11,
			COL_HR12, COL_HR13, COL_HR14, COL_HR15, COL_HR16, COL_HR17,
			COL_HR18, COL_HR19, COL_HR20, COL_HR21, COL_HR22, COL_HR23
	};

	public static final String CONTENT_PATH = "skipHours";
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + "vnd.lbconsulting."
			+ CONTENT_PATH;
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + "vnd.lbconsulting."
			+ CONTENT_PATH;
	public static final Uri CONTENT_URI = Uri.parse("content://" + RSS_ContentProvider.AUTHORITY + "/" + CONTENT_PATH);

	// Database creation SQL statements
	private static final String DATATABLE_CREATE = "create table " + TABLE_SKIP_HOURS
			+ " ("
			+ COL_SKIP_HOURS_ID + " integer primary key autoincrement, "
			+ COL_CHANNEL_ID + " integer default -1, "
			+ COL_HR00 + " integer default 0 ,"
			+ COL_HR01 + " integer default 0 ,"
			+ COL_HR02 + " integer default 0 ,"
			+ COL_HR03 + " integer default 0 ,"
			+ COL_HR04 + " integer default 0 ,"
			+ COL_HR05 + " integer default 0 ,"
			+ COL_HR06 + " integer default 0 ,"
			+ COL_HR07 + " integer default 0 ,"
			+ COL_HR08 + " integer default 0 ,"
			+ COL_HR09 + " integer default 0 ,"
			+ COL_HR10 + " integer default 0 ,"
			+ COL_HR11 + " integer default 0 ,"
			+ COL_HR12 + " integer default 0 ,"
			+ COL_HR13 + " integer default 0 ,"
			+ COL_HR14 + " integer default 0 ,"
			+ COL_HR15 + " integer default 0 ,"
			+ COL_HR16 + " integer default 0 ,"
			+ COL_HR17 + " integer default 0 ,"
			+ COL_HR18 + " integer default 0 ,"
			+ COL_HR19 + " integer default 0 ,"
			+ COL_HR20 + " integer default 0 ,"
			+ COL_HR21 + " integer default 0 ,"
			+ COL_HR22 + " integer default 0 ,"
			+ COL_HR23 + " integer default 0"
			+ ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATATABLE_CREATE);
		MyLog.i("RSS_SkipHoursTable", "onCreate: " + TABLE_SKIP_HOURS + " created.");

	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		MyLog.w(TABLE_SKIP_HOURS, "Upgrading database from version " + oldVersion + " to version " + newVersion);
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_SKIP_HOURS);
		onCreate(database);
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Create Methods
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static long CreateSkipHours(Context context, long channelID, ContentValues skipHoursContentValues) {
		long newSkipHoursID = -1;
		Cursor skipHoursRowCursor = null;

		if (channelID > 0) {
			// determine if the skipHours for the provided channel is already in the database
			skipHoursRowCursor = getskipHoursRow(context, channelID);
			if (skipHoursRowCursor != null && skipHoursRowCursor.getCount() > 0) {
				// the skipHours for the provided channel exist in the database
				skipHoursRowCursor.moveToFirst();
				newSkipHoursID = skipHoursRowCursor
						.getLong(skipHoursRowCursor.getColumnIndexOrThrow(COL_SKIP_HOURS_ID));
				// set all skip hours to their default (0) values
				ContentValues newFieldValues = new ContentValues();
				String col = "";
				for (int hourOfDay = 0; hourOfDay < 24; hourOfDay++) {
					col = "HR" + String.format("%0d2", hourOfDay);
					newFieldValues.put(col, 0);
				}
				UpdateSkipHourslFieldValues(context, channelID, newFieldValues);
				skipHoursRowCursor.close();
				// update the skip hour fields with the new content
				UpdateSkipHourslFieldValues(context, channelID, skipHoursContentValues);
				return newSkipHoursID;

			}

			// the skipHours for the provided channel does not exist in the database ... so create it
			skipHoursContentValues.put(COL_CHANNEL_ID, channelID);
			Uri uri = CONTENT_URI;
			ContentResolver cr = context.getContentResolver();
			Uri newItemUri = cr.insert(uri, skipHoursContentValues);
			if (newItemUri != null) {
				newSkipHoursID = Long.parseLong(newItemUri.getLastPathSegment());
			}
		}

		if (skipHoursRowCursor != null) {
			skipHoursRowCursor.close();
		}
		return newSkipHoursID;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Read Methods
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static Cursor getskipHoursRow(Context context, long channelID) {
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
			MyLog.e("RSS_skipHoursTable", "Exception error in getskipHours:");
			e.printStackTrace();
		}
		return cursor;
	}

	public static SparseIntArray getskipHours(Context context, long channelID) {
		SparseIntArray skipHours = new SparseIntArray();
		if (channelID > 0) {
			Cursor skipHoursRowCursor = getskipHoursRow(context, channelID);
			if (skipHoursRowCursor != null && skipHoursRowCursor.getCount() > 0) {
				skipHoursRowCursor.moveToFirst();

				for (int col = 2; col < 26; col++) {
					int hourOfDay = skipHoursRowCursor.getInt(col);
					if (hourOfDay == 1) {
						skipHours.put(col - 2, col - 2);
					}
				}
			}

			if (skipHoursRowCursor != null) {
				skipHoursRowCursor.close();
			}
		}
		return skipHours;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Update Methods
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static int UpdateSkipHourslFieldValues(Context context, long channelID, ContentValues newFieldValues) {
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

	public static void setSkipHour(Context context, long channelID, int hourOfDay, boolean skip) {
		int skipValue = 0;
		if (skip) {
			skipValue = 1;
		}

		String col = "HR" + String.format("%0d2", hourOfDay);
		ContentValues newFieldValues = new ContentValues();
		newFieldValues.put(col, skipValue);
		UpdateSkipHourslFieldValues(context, channelID, newFieldValues);
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Delete Methods
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static int DeleteAllSkipHoursInChannel(Context context, long channelID) {
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
