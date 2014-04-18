package com.lbconsulting.homework_312_lorenbak.database;

import java.util.Locale;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.SparseArray;

import com.lbconsulting.homework_312_lorenbak.MyLog;

public class RSS_SkipDaysTable {

	/*skipDays
	 * A hint for aggregators telling them which days they can skip. This element contains up to 
	 * seven <day> sub-elements whose value is Monday, Tuesday, Wednesday, Thursday, Friday, Saturday or Sunday. 
	 * Aggregators may not read the channel during days listed in the <skipDays> element.*/

	// Version 1
	public static final String TABLE_SKIP_DAYS = "tblSkipDays";
	public static final String COL_SKIP_DAYS_ID = "_id";
	public static final String COL_CHANNEL_ID = "channelID";
	public static final String COL_SUNDAY = "sunday"; // col2
	public static final String COL_MONDAY = "monday"; // col3
	public static final String COL_TUESDAY = "tuesday"; // col4
	public static final String COL_WEDNESDAY = "wednesday"; // col5
	public static final String COL_THURSDAY = "thursday"; // col6
	public static final String COL_FRIDAY = "friday"; // col7
	public static final String COL_SATURDAY = "saturday"; // col8

	public static final String[] PROJECTION_ALL = { COL_SKIP_DAYS_ID, COL_CHANNEL_ID, COL_SUNDAY, COL_MONDAY,
			COL_TUESDAY,
			COL_WEDNESDAY, COL_THURSDAY, COL_FRIDAY, COL_SATURDAY
	};

	public static final String CONTENT_PATH = "skipDays";
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + "vnd.lbconsulting."
			+ CONTENT_PATH;
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + "vnd.lbconsulting."
			+ CONTENT_PATH;
	public static final Uri CONTENT_URI = Uri.parse("content://" + RSS_ContentProvider.AUTHORITY + "/" + CONTENT_PATH);

	// Database creation SQL statements
	private static final String DATATABLE_CREATE = "create table " + TABLE_SKIP_DAYS
			+ " ("
			+ COL_SKIP_DAYS_ID + " integer primary key autoincrement, "
			+ COL_CHANNEL_ID + " integer default -1, "
			+ COL_SUNDAY + " integer default 0 ,"
			+ COL_MONDAY + " integer default 0, "
			+ COL_TUESDAY + " integer default 0, "
			+ COL_WEDNESDAY + " integer default 0, "
			+ COL_THURSDAY + " integer default 0, "
			+ COL_FRIDAY + " integer default 0, "
			+ COL_SATURDAY + " integer default 0 "
			+ ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATATABLE_CREATE);
		MyLog.i("RSS_SkipDaysTable", "onCreate: " + TABLE_SKIP_DAYS + " created.");

	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		MyLog.w(TABLE_SKIP_DAYS, "Upgrading database from version " + oldVersion + " to version " + newVersion);
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_SKIP_DAYS);
		onCreate(database);
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Create Methods
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static long CreateSkipDays(Context context, long channelID, ContentValues skipDaysContentValues) {
		long newSkipDaysID = -1;
		Cursor skipDaysRowCursor = null;

		if (channelID > 0) {
			// determine if the skipDays for the provided channel is already in the database
			skipDaysRowCursor = getSkipDaysRow(context, channelID);
			if (skipDaysRowCursor != null && skipDaysRowCursor.getCount() > 0) {
				// the skipDays for the provided channel exist in the database
				skipDaysRowCursor.moveToFirst();
				newSkipDaysID = skipDaysRowCursor.getLong(skipDaysRowCursor.getColumnIndexOrThrow(COL_SKIP_DAYS_ID));
				// set all skip days to their default (0) values
				ContentValues newFieldValues = new ContentValues();
				newFieldValues.put(COL_SUNDAY, 0);
				newFieldValues.put(COL_MONDAY, 0);
				newFieldValues.put(COL_TUESDAY, 0);
				newFieldValues.put(COL_WEDNESDAY, 0);
				newFieldValues.put(COL_THURSDAY, 0);
				newFieldValues.put(COL_FRIDAY, 0);
				newFieldValues.put(COL_SATURDAY, 0);
				UpdateSkipDayslFieldValues(context, channelID, newFieldValues);
				skipDaysRowCursor.close();
				// update the skip days fields with the new content
				UpdateSkipDayslFieldValues(context, channelID, skipDaysContentValues);
				return newSkipDaysID;
			}

			// the skipDays for the provided channel does not exist in the database ... so create it
			// ContentValues newFieldValues = new ContentValues();
			skipDaysContentValues.put(COL_CHANNEL_ID, channelID);
			Uri uri = CONTENT_URI;
			ContentResolver cr = context.getContentResolver();
			Uri newItemUri = cr.insert(uri, skipDaysContentValues);
			if (newItemUri != null) {
				newSkipDaysID = Long.parseLong(newItemUri.getLastPathSegment());
			}
		}

		if (skipDaysRowCursor != null) {
			skipDaysRowCursor.close();
		}
		return newSkipDaysID;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Read Methods
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static Cursor getSkipDaysRow(Context context, long channelID) {
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
			MyLog.e("RSS_SkipDaysTable", "Exception error in getSkipDays:");
			e.printStackTrace();
		}
		return cursor;
	}

	public static SparseArray<String> getSkipDays(Context context, long channelID) {
		SparseArray<String> skipDays = new SparseArray<String>();
		if (channelID > 0) {
			Cursor skipDaysRowCursor = getSkipDaysRow(context, channelID);
			if (skipDaysRowCursor != null && skipDaysRowCursor.getCount() > 0) {
				skipDaysRowCursor.moveToFirst();

				for (int col = 2; col < 9; col++) {
					int dayOfWeek = skipDaysRowCursor.getInt(col);
					if (dayOfWeek == 1) {
						switch (col) {
							case 2:
								skipDays.put(col - 2, "Sunday");
								break;
							case 3:
								skipDays.put(col - 2, "Monday");
								break;
							case 4:
								skipDays.put(col - 2, "Tuesday");
								break;
							case 5:
								skipDays.put(col - 2, "Wednesday");
								break;
							case 6:
								skipDays.put(col - 2, "Thursday");
								break;
							case 7:
								skipDays.put(col - 2, "Friday");
								break;
							case 8:
								skipDays.put(col - 2, "Saturday");
								break;

							default:
								break;
						}
					}
				}
			}

			if (skipDaysRowCursor != null) {
				skipDaysRowCursor.close();
			}
		}
		return skipDays;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Update Methods
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static int UpdateSkipDayslFieldValues(Context context, long channelID, ContentValues newFieldValues) {
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

	public static void setSkipDay(Context context, long channelID, String day, boolean skip) {
		int skipValue = 0;
		if (skip) {
			skipValue = 1;
		}
		day = day.toLowerCase(Locale.US);
		ContentValues newFieldValues = new ContentValues();
		if (day.equals("sunday")) {
			newFieldValues.put(COL_SUNDAY, skipValue);
		} else if (day.equals("monday")) {
			newFieldValues.put(COL_MONDAY, skipValue);
		} else if (day.equals("tuesday")) {
			newFieldValues.put(COL_TUESDAY, skipValue);
		} else if (day.equals("wednesday")) {
			newFieldValues.put(COL_WEDNESDAY, skipValue);
		} else if (day.equals("thursday")) {
			newFieldValues.put(COL_THURSDAY, skipValue);
		} else if (day.equals("friday")) {
			newFieldValues.put(COL_FRIDAY, skipValue);
		} else if (day.equals("saturday")) {
			newFieldValues.put(COL_SATURDAY, skipValue);
		}
		UpdateSkipDayslFieldValues(context, channelID, newFieldValues);
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Delete Methods
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static int DeleteAllSkipDaysInChannel(Context context, long channelID) {
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
