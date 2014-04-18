package com.lbconsulting.homework_312_lorenbak.database;

import java.util.Arrays;
import java.util.HashSet;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.lbconsulting.homework_312_lorenbak.MyLog;

public class RSS_ContentProvider extends ContentProvider {

	// AList database
	private RSS_DatabaseHelper database = null;

	// UriMatcher switch constants
	private static final int CHANNELS_MULTI_ROWS = 10;
	private static final int CHANNELS_SINGLE_ROW = 11;

	private static final int IMAGES_MULTI_ROWS = 20;
	private static final int IMAGES_SINGLE_ROW = 21;

	private static final int ITEMS_MULTI_ROWS = 30;
	private static final int ITEMS_SINGLE_ROW = 31;

	private static final int SKIP_DAYS_MULTI_ROWS = 40;
	private static final int SKIP_DAYS_SINGLE_ROW = 41;

	private static final int SKIP_HOURS_MULTI_ROWS = 50;
	private static final int SKIP_HOURS_SINGLE_ROW = 51;

	private static final int TEXT_INPUTS_MULTI_ROWS = 60;
	private static final int TEXT_INPUTS_SINGLE_ROW = 61;

	public static final String AUTHORITY = "com.lbconsulting.homework_312_lorenbak";

	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, RSS_ChannelsTable.CONTENT_PATH, CHANNELS_MULTI_ROWS);
		sURIMatcher.addURI(AUTHORITY, RSS_ChannelsTable.CONTENT_PATH + "/#", CHANNELS_SINGLE_ROW);

		sURIMatcher.addURI(AUTHORITY, RSS_ImagesTable.CONTENT_PATH, IMAGES_MULTI_ROWS);
		sURIMatcher.addURI(AUTHORITY, RSS_ImagesTable.CONTENT_PATH + "/#", IMAGES_SINGLE_ROW);

		sURIMatcher.addURI(AUTHORITY, RSS_ItemsTable.CONTENT_PATH, ITEMS_MULTI_ROWS);
		sURIMatcher.addURI(AUTHORITY, RSS_ItemsTable.CONTENT_PATH + "/#", ITEMS_SINGLE_ROW);

		sURIMatcher.addURI(AUTHORITY, RSS_SkipDaysTable.CONTENT_PATH, SKIP_DAYS_MULTI_ROWS);
		sURIMatcher.addURI(AUTHORITY, RSS_SkipDaysTable.CONTENT_PATH + "/#", SKIP_DAYS_SINGLE_ROW);

		sURIMatcher.addURI(AUTHORITY, RSS_SkipHoursTable.CONTENT_PATH, SKIP_HOURS_MULTI_ROWS);
		sURIMatcher.addURI(AUTHORITY, RSS_SkipHoursTable.CONTENT_PATH + "/#", SKIP_HOURS_SINGLE_ROW);

		sURIMatcher.addURI(AUTHORITY, RSS_TextInputsTable.CONTENT_PATH, TEXT_INPUTS_MULTI_ROWS);
		sURIMatcher.addURI(AUTHORITY, RSS_TextInputsTable.CONTENT_PATH + "/#", TEXT_INPUTS_SINGLE_ROW);
	}

	@Override
	public boolean onCreate() {
		MyLog.i("RSS_ContentProvider", "onCreate");
		// Construct the underlying database
		// Defer opening the database until you need to perform
		// a query or other transaction.
		database = new RSS_DatabaseHelper(getContext());
		return true;
	}

	/*	A content provider is created when its hosting process is created, and remains around for as long as the process
		does, so there is no need to close the database -- it will get closed as part of the kernel cleaning up the
		process's resources when the process is killed. 
	*/

	@SuppressWarnings("resource")
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		String rowID = null;
		int deleteCount = 0;

		// Open a WritableDatabase database to support the delete transaction
		SQLiteDatabase db = database.getWritableDatabase();

		int uriType = sURIMatcher.match(uri);
		switch (uriType) {

			case CHANNELS_MULTI_ROWS:
				// To return the number of deleted items you must specify a where clause.
				// To delete all rows and return a value pass in "1".
				if (selection == null) {
					selection = "1";
				}
				// Perform the deletion
				deleteCount = db.delete(RSS_ChannelsTable.TABLE_CHANNELS, selection, selectionArgs);
				break;

			case CHANNELS_SINGLE_ROW:
				// Limit deletion to a single row
				rowID = uri.getLastPathSegment();
				selection = RSS_ChannelsTable.COL_CHANNEL_ID + "=" + rowID
						+ (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
				// Perform the deletion
				deleteCount = db.delete(RSS_ChannelsTable.TABLE_CHANNELS, selection, selectionArgs);
				break;

			case IMAGES_MULTI_ROWS:
				if (selection == null) {
					selection = "1";
				}
				deleteCount = db.delete(RSS_ImagesTable.TABLE_IMAGES, selection, selectionArgs);
				break;

			case IMAGES_SINGLE_ROW:
				rowID = uri.getLastPathSegment();
				selection = RSS_ImagesTable.COL_IMAGES_ID + "=" + rowID
						+ (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
				deleteCount = db.delete(RSS_ImagesTable.TABLE_IMAGES, selection, selectionArgs);
				break;

			case ITEMS_MULTI_ROWS:
				if (selection == null) {
					selection = "1";
				}
				deleteCount = db.delete(RSS_ItemsTable.TABLE_ITEMS, selection, selectionArgs);
				break;

			case ITEMS_SINGLE_ROW:
				rowID = uri.getLastPathSegment();
				selection = RSS_ItemsTable.COL_ITEM_ID + "=" + rowID
						+ (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
				deleteCount = db.delete(RSS_ItemsTable.TABLE_ITEMS, selection, selectionArgs);
				break;

			case SKIP_DAYS_MULTI_ROWS:
				if (selection == null) {
					selection = "1";
				}
				deleteCount = db.delete(RSS_SkipDaysTable.TABLE_SKIP_DAYS, selection, selectionArgs);
				break;

			case SKIP_DAYS_SINGLE_ROW:
				rowID = uri.getLastPathSegment();
				selection = RSS_SkipDaysTable.COL_SKIP_DAYS_ID + "=" + rowID
						+ (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
				deleteCount = db.delete(RSS_SkipDaysTable.TABLE_SKIP_DAYS, selection, selectionArgs);
				break;

			case SKIP_HOURS_MULTI_ROWS:
				if (selection == null) {
					selection = "1";
				}
				deleteCount = db.delete(RSS_SkipHoursTable.TABLE_SKIP_HOURS, selection, selectionArgs);
				break;

			case SKIP_HOURS_SINGLE_ROW:
				rowID = uri.getLastPathSegment();
				selection = RSS_SkipHoursTable.COL_SKIP_HOURS_ID + "=" + rowID
						+ (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
				deleteCount = db.delete(RSS_SkipHoursTable.TABLE_SKIP_HOURS, selection, selectionArgs);
				break;

			case TEXT_INPUTS_MULTI_ROWS:
				if (selection == null) {
					selection = "1";
				}
				deleteCount = db.delete(RSS_TextInputsTable.TABLE_TEXT_INPUTS, selection, selectionArgs);
				break;

			case TEXT_INPUTS_SINGLE_ROW:
				rowID = uri.getLastPathSegment();
				selection = RSS_TextInputsTable.COL_TEXT_INPUT_ID + "=" + rowID
						+ (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
				deleteCount = db.delete(RSS_TextInputsTable.TABLE_TEXT_INPUTS, selection, selectionArgs);
				break;

			default:
				throw new IllegalArgumentException("Method delete: Unknown URI: " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return deleteCount;
	}

	@Override
	public String getType(Uri uri) {
		int uriType = sURIMatcher.match(uri);
		switch (uriType) {

			case CHANNELS_MULTI_ROWS:
				return RSS_ChannelsTable.CONTENT_TYPE;
			case CHANNELS_SINGLE_ROW:
				return RSS_ChannelsTable.CONTENT_ITEM_TYPE;

			case IMAGES_MULTI_ROWS:
				return RSS_ImagesTable.CONTENT_TYPE;
			case IMAGES_SINGLE_ROW:
				return RSS_ImagesTable.CONTENT_ITEM_TYPE;

			case ITEMS_MULTI_ROWS:
				return RSS_ItemsTable.CONTENT_TYPE;
			case ITEMS_SINGLE_ROW:
				return RSS_ItemsTable.CONTENT_ITEM_TYPE;

			case SKIP_DAYS_MULTI_ROWS:
				return RSS_SkipDaysTable.CONTENT_TYPE;
			case SKIP_DAYS_SINGLE_ROW:
				return RSS_SkipDaysTable.CONTENT_ITEM_TYPE;

			case SKIP_HOURS_MULTI_ROWS:
				return RSS_SkipHoursTable.CONTENT_TYPE;
			case SKIP_HOURS_SINGLE_ROW:
				return RSS_SkipHoursTable.CONTENT_ITEM_TYPE;

			case TEXT_INPUTS_MULTI_ROWS:
				return RSS_TextInputsTable.CONTENT_TYPE;
			case TEXT_INPUTS_SINGLE_ROW:
				return RSS_TextInputsTable.CONTENT_ITEM_TYPE;

			default:
				throw new IllegalArgumentException("Method getType. Unknown URI: " + uri);
		}
	}

	@SuppressWarnings("resource")
	@Override
	public Uri insert(Uri uri, ContentValues values) {

		SQLiteDatabase db = null;
		long newRowId = 0;
		String nullColumnHack = null;

		// Open a WritableDatabase database to support the insert transaction
		db = database.getWritableDatabase();

		int uriType = sURIMatcher.match(uri);
		switch (uriType) {

			case CHANNELS_MULTI_ROWS:
				newRowId = db.insertOrThrow(RSS_ChannelsTable.TABLE_CHANNELS, nullColumnHack, values);
				if (newRowId > 0) {
					// Construct and return the URI of the newly inserted row.
					Uri newRowUri = ContentUris.withAppendedId(RSS_ChannelsTable.CONTENT_URI, newRowId);
					getContext().getContentResolver().notifyChange(RSS_ChannelsTable.CONTENT_URI, null);
					return newRowUri;
				}
				return null;

			case CHANNELS_SINGLE_ROW:
				throw new IllegalArgumentException(
						"Illegal URI: Cannot insert a new row with a single row URI. " + uri);

			case IMAGES_MULTI_ROWS:
				newRowId = db.insertOrThrow(RSS_ImagesTable.TABLE_IMAGES, nullColumnHack, values);
				if (newRowId > 0) {
					// Construct and return the URI of the newly inserted row.
					Uri newRowUri = ContentUris.withAppendedId(RSS_ImagesTable.CONTENT_URI, newRowId);
					getContext().getContentResolver().notifyChange(RSS_ImagesTable.CONTENT_URI, null);
					return newRowUri;
				}
				return null;

			case IMAGES_SINGLE_ROW:
				throw new IllegalArgumentException(
						"Illegal URI: Cannot insert a new row with a single row URI. " + uri);

			case ITEMS_MULTI_ROWS:
				newRowId = db.insertOrThrow(RSS_ItemsTable.TABLE_ITEMS, nullColumnHack, values);
				if (newRowId > 0) {
					// Construct and return the URI of the newly inserted row.
					Uri newRowUri = ContentUris.withAppendedId(RSS_ItemsTable.CONTENT_URI, newRowId);
					getContext().getContentResolver().notifyChange(RSS_ItemsTable.CONTENT_URI, null);
					return newRowUri;
				}
				return null;

			case ITEMS_SINGLE_ROW:
				throw new IllegalArgumentException(
						"Illegal URI: Cannot insert a new row with a single row URI. " + uri);

			case SKIP_DAYS_MULTI_ROWS:
				newRowId = db.insertOrThrow(RSS_SkipDaysTable.TABLE_SKIP_DAYS, nullColumnHack, values);
				if (newRowId > 0) {
					// Construct and return the URI of the newly inserted row.
					Uri newRowUri = ContentUris.withAppendedId(RSS_SkipDaysTable.CONTENT_URI, newRowId);
					getContext().getContentResolver().notifyChange(RSS_SkipDaysTable.CONTENT_URI, null);
					return newRowUri;
				}
				return null;

			case SKIP_DAYS_SINGLE_ROW:
				throw new IllegalArgumentException(
						"Illegal URI: Cannot insert a new row with a single row URI. " + uri);

			case SKIP_HOURS_MULTI_ROWS:
				newRowId = db.insertOrThrow(RSS_SkipHoursTable.TABLE_SKIP_HOURS, nullColumnHack, values);
				if (newRowId > 0) {
					// Construct and return the URI of the newly inserted row.
					Uri newRowUri = ContentUris.withAppendedId(RSS_SkipHoursTable.CONTENT_URI, newRowId);
					getContext().getContentResolver().notifyChange(RSS_SkipHoursTable.CONTENT_URI, null);
					return newRowUri;
				}
				return null;

			case SKIP_HOURS_SINGLE_ROW:
				throw new IllegalArgumentException(
						"Illegal URI: Cannot insert a new row with a single row URI. " + uri);

			case TEXT_INPUTS_MULTI_ROWS:
				newRowId = db.insertOrThrow(RSS_TextInputsTable.TABLE_TEXT_INPUTS, nullColumnHack, values);
				if (newRowId > 0) {
					// Construct and return the URI of the newly inserted row.
					Uri newRowUri = ContentUris.withAppendedId(RSS_TextInputsTable.CONTENT_URI, newRowId);
					getContext().getContentResolver().notifyChange(RSS_TextInputsTable.CONTENT_URI, null);
					return newRowUri;
				}
				return null;

			case TEXT_INPUTS_SINGLE_ROW:
				throw new IllegalArgumentException(
						"Illegal URI: Cannot insert a new row with a single row URI. " + uri);

			default:
				throw new IllegalArgumentException("Method insert: Unknown URI:" + uri);
		}
	}

	@SuppressWarnings("resource")
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

		// Using SQLiteQueryBuilder
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		/*			case ITEMS_MULTI_ROWS:
						queryBuilder.setTables(RSS_ChannelsTable.TABLE_ARTICLES);
						checkItemsColumnNames(projection);
						break;

					case ITEMS_SINGLE_ROW:
						queryBuilder.setTables(RSS_ChannelsTable.TABLE_ARTICLES);
						checkItemsColumnNames(projection);
						queryBuilder.appendWhere(RSS_ChannelsTable.COL_ARTICLE_ID + "=" + uri.getLastPathSegment());
						break;*/

			case CHANNELS_MULTI_ROWS:
				queryBuilder.setTables(RSS_ChannelsTable.TABLE_CHANNELS);
				checkChannelsColumnNames(projection);
				break;

			case CHANNELS_SINGLE_ROW:
				queryBuilder.setTables(RSS_ChannelsTable.TABLE_CHANNELS);
				checkChannelsColumnNames(projection);
				queryBuilder.appendWhere(RSS_ChannelsTable.COL_CHANNEL_ID + "=" + uri.getLastPathSegment());
				break;

			case IMAGES_MULTI_ROWS:
				queryBuilder.setTables(RSS_ImagesTable.TABLE_IMAGES);
				checkImagesColumnNames(projection);
				break;
			case IMAGES_SINGLE_ROW:
				queryBuilder.setTables(RSS_ImagesTable.TABLE_IMAGES);
				checkImagesColumnNames(projection);
				queryBuilder.appendWhere(RSS_ImagesTable.COL_IMAGES_ID + "=" + uri.getLastPathSegment());
				break;

			case ITEMS_MULTI_ROWS:
				queryBuilder.setTables(RSS_ItemsTable.TABLE_ITEMS);
				checkItemsColumnNames(projection);
				break;
			case ITEMS_SINGLE_ROW:
				queryBuilder.setTables(RSS_ItemsTable.TABLE_ITEMS);
				checkItemsColumnNames(projection);
				queryBuilder.appendWhere(RSS_ItemsTable.COL_ITEM_ID + "=" + uri.getLastPathSegment());
				break;

			case SKIP_DAYS_MULTI_ROWS:
				queryBuilder.setTables(RSS_SkipDaysTable.TABLE_SKIP_DAYS);
				checkSkipDaysColumnNames(projection);
				break;
			case SKIP_DAYS_SINGLE_ROW:
				queryBuilder.setTables(RSS_SkipDaysTable.TABLE_SKIP_DAYS);
				checkSkipDaysColumnNames(projection);
				queryBuilder.appendWhere(RSS_SkipDaysTable.COL_SKIP_DAYS_ID + "=" + uri.getLastPathSegment());
				break;

			case SKIP_HOURS_MULTI_ROWS:
				queryBuilder.setTables(RSS_SkipHoursTable.TABLE_SKIP_HOURS);
				checkSkipHoursColumnNames(projection);
				break;
			case SKIP_HOURS_SINGLE_ROW:
				queryBuilder.setTables(RSS_SkipHoursTable.TABLE_SKIP_HOURS);
				checkSkipHoursColumnNames(projection);
				queryBuilder.appendWhere(RSS_SkipHoursTable.COL_SKIP_HOURS_ID + "=" + uri.getLastPathSegment());
				break;

			case TEXT_INPUTS_MULTI_ROWS:
				queryBuilder.setTables(RSS_TextInputsTable.TABLE_TEXT_INPUTS);
				checkTextInputsColumnNames(projection);
				break;
			case TEXT_INPUTS_SINGLE_ROW:
				queryBuilder.setTables(RSS_TextInputsTable.TABLE_TEXT_INPUTS);
				checkTextInputsColumnNames(projection);
				queryBuilder.appendWhere(RSS_TextInputsTable.COL_TEXT_INPUT_ID + "=" + uri.getLastPathSegment());
				break;

			default:
				throw new IllegalArgumentException("Method query. Unknown URI:" + uri);
		}

		// Execute the query on the database
		SQLiteDatabase db = null;
		try {
			db = database.getWritableDatabase();
		} catch (SQLiteException ex) {
			db = database.getReadableDatabase();
		}

		if (null != db) {
			String groupBy = null;
			String having = null;
			Cursor cursor = null;
			try {
				cursor = queryBuilder.query(db, projection, selection, selectionArgs, groupBy, having, sortOrder);
			} catch (Exception e) {
				MyLog.e("RSS_ContentProvider: Exception error in query.\n", e.toString());
				// e.printStackTrace();
			}

			if (null != cursor) {
				cursor.setNotificationUri(getContext().getContentResolver(), uri);
			}
			return cursor;
		}
		return null;
	}

	@SuppressWarnings("resource")
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		String rowID = null;
		int updateCount = 0;

		// Open a WritableDatabase database to support the update transaction
		SQLiteDatabase db = database.getWritableDatabase();

		int uriType = sURIMatcher.match(uri);
		switch (uriType) {

			case CHANNELS_MULTI_ROWS:
				updateCount = db.update(RSS_ChannelsTable.TABLE_CHANNELS, values, selection, selectionArgs);
				break;

			case CHANNELS_SINGLE_ROW:
				rowID = uri.getLastPathSegment();
				selection = RSS_ChannelsTable.COL_CHANNEL_ID + "=" + rowID
						+ (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
				updateCount = db.update(RSS_ChannelsTable.TABLE_CHANNELS, values, selection, selectionArgs);
				break;

			case IMAGES_MULTI_ROWS:
				updateCount = db.update(RSS_ImagesTable.TABLE_IMAGES, values, selection, selectionArgs);
				break;
			case IMAGES_SINGLE_ROW:
				rowID = uri.getLastPathSegment();
				selection = RSS_ImagesTable.COL_IMAGES_ID + "=" + rowID
						+ (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
				updateCount = db.update(RSS_ImagesTable.TABLE_IMAGES, values, selection, selectionArgs);
				break;

			case ITEMS_MULTI_ROWS:
				updateCount = db.update(RSS_ItemsTable.TABLE_ITEMS, values, selection, selectionArgs);
				break;
			case ITEMS_SINGLE_ROW:
				rowID = uri.getLastPathSegment();
				selection = RSS_ItemsTable.COL_ITEM_ID + "=" + rowID
						+ (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
				updateCount = db.update(RSS_ItemsTable.TABLE_ITEMS, values, selection, selectionArgs);
				break;

			case SKIP_DAYS_MULTI_ROWS:
				updateCount = db.update(RSS_SkipDaysTable.TABLE_SKIP_DAYS, values, selection, selectionArgs);
				break;
			case SKIP_DAYS_SINGLE_ROW:
				rowID = uri.getLastPathSegment();
				selection = RSS_SkipDaysTable.COL_SKIP_DAYS_ID + "=" + rowID
						+ (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
				updateCount = db.update(RSS_SkipDaysTable.TABLE_SKIP_DAYS, values, selection, selectionArgs);
				break;

			case SKIP_HOURS_MULTI_ROWS:
				updateCount = db.update(RSS_SkipHoursTable.TABLE_SKIP_HOURS, values, selection, selectionArgs);
				break;
			case SKIP_HOURS_SINGLE_ROW:
				rowID = uri.getLastPathSegment();
				selection = RSS_SkipHoursTable.COL_SKIP_HOURS_ID + "=" + rowID
						+ (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
				updateCount = db.update(RSS_SkipHoursTable.TABLE_SKIP_HOURS, values, selection, selectionArgs);
				break;

			case TEXT_INPUTS_MULTI_ROWS:
				updateCount = db.update(RSS_TextInputsTable.TABLE_TEXT_INPUTS, values, selection, selectionArgs);
				break;
			case TEXT_INPUTS_SINGLE_ROW:
				rowID = uri.getLastPathSegment();
				selection = RSS_TextInputsTable.COL_TEXT_INPUT_ID + "=" + rowID
						+ (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
				updateCount = db.update(RSS_TextInputsTable.TABLE_TEXT_INPUTS, values, selection, selectionArgs);
				break;

			default:
				throw new IllegalArgumentException("Method update: Unknown URI: " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return updateCount;
	}

	private void checkChannelsColumnNames(String[] projection) {
		// Check if the caller has requested a column that does not exist
		if (projection != null) {
			HashSet<String> requstedColumns = new HashSet<String>(Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(RSS_ChannelsTable.PROJECTION_ALL));
			if (!availableColumns.containsAll(requstedColumns)) {
				throw new IllegalArgumentException(
						"Method checkChannelsColumnNames: Unknown table column name included in the requested projection!");
			}
		}
	}

	private void checkImagesColumnNames(String[] projection) {
		// Check if the caller has requested a column that does not exist
		if (projection != null) {
			HashSet<String> requstedColumns = new HashSet<String>(Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(RSS_ImagesTable.PROJECTION_ALL));
			if (!availableColumns.containsAll(requstedColumns)) {
				throw new IllegalArgumentException(
						"Method checkImagesColumnNames: Unknown table column name included in the requested projection!");
			}
		}
	}

	private void checkItemsColumnNames(String[] projection) {
		// Check if the caller has requested a column that does not exist
		if (projection != null) {
			HashSet<String> requstedColumns = new HashSet<String>(Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(RSS_ItemsTable.PROJECTION_ALL));
			if (!availableColumns.containsAll(requstedColumns)) {
				throw new IllegalArgumentException(
						"Method checkItemsColumnNames: Unknown table column name included in the requested projection!");
			}
		}
	}

	private void checkSkipDaysColumnNames(String[] projection) {
		// Check if the caller has requested a column that does not exist
		if (projection != null) {
			HashSet<String> requstedColumns = new HashSet<String>(Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(RSS_SkipDaysTable.PROJECTION_ALL));
			if (!availableColumns.containsAll(requstedColumns)) {
				throw new IllegalArgumentException(
						"Method checkSkipDaysColumnNames: Unknown table column name included in the requested projection!");
			}
		}
	}

	private void checkSkipHoursColumnNames(String[] projection) {
		// Check if the caller has requested a column that does not exist
		if (projection != null) {
			HashSet<String> requstedColumns = new HashSet<String>(Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(RSS_SkipHoursTable.PROJECTION_ALL));
			if (!availableColumns.containsAll(requstedColumns)) {
				throw new IllegalArgumentException(
						"Method checkSkipHoursColumnNames: Unknown table column name included in the requested projection!");
			}
		}
	}

	private void checkTextInputsColumnNames(String[] projection) {
		// Check if the caller has requested a column that does not exist
		if (projection != null) {
			HashSet<String> requstedColumns = new HashSet<String>(Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(RSS_TextInputsTable.PROJECTION_ALL));
			if (!availableColumns.containsAll(requstedColumns)) {
				throw new IllegalArgumentException(
						"Method checkTextInputsColumnNames: Unknown table column name included in the requested projection!");
			}
		}
	}

	/**
	 * A test package can call this to get a handle to the database underlying HW311ContentProvider, so it can insert
	 * test data into the database. The test case class is responsible for instantiating the provider in a test context;
	 * {@link android.test.ProviderTestCase2} does this during the call to setUp()
	 * 
	 * @return a handle to the database helper object for the provider's data.
	 */
	public RSS_DatabaseHelper getOpenHelperForTest() {
		return database;
	}
}
