package com.lbconsulting.homework_312_lorenbak.database;

import java.util.Calendar;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import com.lbconsulting.homework_312_lorenbak.MyLog;

public class RSS_ItemsTable {

	/* A channel may contain any number of <item>s. 
	 * An item may represent a "story" -- much like a story in a newspaper or magazine; 
	 * if so its description is a synopsis of the story, and the link points to the full story. 
	 * An item may also be complete in itself, if so, the description contains the text 
	 * (entity-encoded HTML is allowed; see examples), and the link and title may be omitted. 
	 * 
	 * All elements of an item are optional, however at least one of title or description must be present.
	 * 	title - The title of the item.	
	 * 		Example: Venice Film Festival Tries to Quit Sinking
	 * 
	 *  link - The URL of the item.	
	 *  	Example: http://nytimes.com/2004/12/07FEST.html
	 *  
	 *  description - The item synopsis.	
	 *  	Example: <description>Some of the most heated chatter at the Venice Film Festival this week was about 
	 *  			 the way that the arrival of the stars at the Palazzo del Cinema was being staged.</description>
	 *  
	 *  author - Email address of the author of the item.
	 *  	It's the email address of the author of the item. For newspapers and magazines syndicating via RSS, 
	 *  	the author is the person who wrote the article that the <item> describes. For collaborative weblogs, 
	 *  	the author of the item might be different from the managing editor or webmaster. For a weblog authored 
	 *  	by a single individual it would make sense to omit the <author> element.
	 *  
	 *  category - Includes the item in one or more categories.
	 *  	Category has one optional attribute, domain, a string that identifies a categorization taxonomy.
	 *  	The value of the element is a forward-slash-separated string that identifies a hierarchic location 
	 *  	in the indicated taxonomy. Processors may establish conventions for the interpretation of categories. 
	 *  	Two examples are provided below:
	 *  		<category>Grateful Dead</category>
	 *  		<category domain="http://www.fool.com/cusips">MSFT</category>
	 * 		You may include as many category elements as you need to, for different domains, and to have an item 
	 * 		cross-referenced in different parts of the same domain.
	 *  
	 *  comments - URL of a page for comments relating to the item.
	 *  	If present, it is the url of the comments page for the item.
	 *  	Example: <comments>http://ekzemplo.com/entry/4403/comments</comments>
	 *  	More about comments here: http://www.rssboard.org/rss-weblog-comments-use-case
	 *  
	 *  enclosure - Describes a media object that is attached to the item.
	 *  	It has three required attributes. 
	 *  		url says where the enclosure is located, 
	 *  		length says how big it is in bytes, and 
	 *  		type says what its type is, a standard MIME type.
	 *  	The url must be an http url.
	 *  	Example: <enclosure url="http://www.scripting.com/mp3s/weatherReportSuite.mp3" length="12216320" type="audio/mpeg" />
	 *  	A use-case narrative for this element is here: http://www.rssboard.org/rss-enclosures-use-case
	 *  
	 *  guid - A string that uniquely identifies the item.
	 *  	guid stands for globally unique identifier. It's a string that uniquely identifies the item. When present, 
	 *  	an aggregator may choose to use this string to determine if an item is new.
	 *  	Example: <guid>http://some.server.com/weblogItem3207</guid>
	 *  
	 *  	There are no rules for the syntax of a guid. Aggregators must view them as a string. It's up to the source of the feed 
	 *  	to establish the uniqueness of the string. 
	 *  
	 *  	If the guid element has an attribute named isPermaLink with a value of true, 
	 *  	the reader may assume that it is a permalink to the item, that is, a url that can be opened in a Web browser, that points 
	 *  	to the full item described by the <item> element. 
	 *  	An example: <guid isPermaLink="true">http://inessential.com/2002/09/01.php#a2</guid>
	 *  	isPermaLink is optional, its default value is true. If its value is false, the guid may not be assumed to be a url, 
	 *  	or a url to anything in particular.

	 *  
	 *  pubDate - Indicates when the item was published.
	 *  	Its value is a date, indicating when the item was published. If it's a date in the future, aggregators may choose to 
	 *  	not display the item until that date.
	 *  	Example: <pubDate>Sun, 19 May 2002 15:21:36 GMT</pubDate>
	 *  
	 *  source - The RSS channel that the item came from.
	 *  	Its value is the name of the RSS channel that the item came from, derived from its <title>. 
	 *  	It has one required attribute, url, which links to the XMLization of the source.
	 *  	Example: <source url="http://www.tomalak.org/links2.xml">Tomalak's Realm</source>
	 *  
	 *  	The purpose of this element is to propagate credit for links, to publicize the sources of news items. 
	 *  	It can be used in the Post command of an aggregator. It should be generated automatically when forwarding an item 
	 *  	from an aggregator to a weblog authoring tool.
	 *  
	 *  */

	// Version 1
	public static final String TABLE_ITEMS = "tblItems";
	public static final String COL_ITEM_ID = "_id";
	public static final String COL_CHANNEL_ID = "channelID";
	public static final String COL_TITLE = "title";
	public static final String COL_LINK = "link";
	public static final String COL_DESCRIPTION = "description";
	public static final String COL_AUTHOR = "author";
	public static final String COL_CATEGORY = "category";
	public static final String COL_CATEGORY_DOMAIN = "category_domain";
	public static final String COL_COMMENTS = "comments";
	public static final String COL_ENCLOSURE_URL = "enclosure_url";
	public static final String COL_ENCLOSURE_LENGTH = "enclosure_length";
	public static final String COL_ENCLOSURE_TYPE = "enclosure_type";
	public static final String COL_GUID = "guid";
	public static final String COL_GUID_PERMALINK = "guid_permalink";
	public static final String COL_PUB_DATE = "pubDate";
	public static final String COL_SOURCE = "source";
	public static final String COL_SOURCE_URL = "source_url";
	public static final String COL_IMAGE_ID = "imageID";
	public static final String COL_ITEM_UPDATED = "itemUpdated";
	public static final String COL_ITEM_SELECTED = "itemSelected";
	public static final String COL_ITEM_READ = "itemRead";

	private static final int TITLE_MAX_LENGTH = 75;

	public static final String[] PROJECTION_ALL = { COL_ITEM_ID, COL_CHANNEL_ID, COL_TITLE, COL_LINK,
			COL_DESCRIPTION, COL_AUTHOR, COL_CATEGORY, COL_CATEGORY_DOMAIN, COL_COMMENTS,
			COL_ENCLOSURE_URL, COL_ENCLOSURE_LENGTH, COL_ENCLOSURE_TYPE, COL_GUID, COL_GUID_PERMALINK,
			COL_PUB_DATE, COL_SOURCE, COL_SOURCE_URL, COL_IMAGE_ID, COL_ITEM_UPDATED, COL_ITEM_SELECTED, COL_ITEM_READ
	};

	public static final String[] PROJECTION_ITEM_ID = { COL_ITEM_ID };

	public static final String CONTENT_PATH = "items";
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + "vnd.lbconsulting."
			+ CONTENT_PATH;
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + "vnd.lbconsulting."
			+ CONTENT_PATH;
	public static final Uri CONTENT_URI = Uri.parse("content://" + RSS_ContentProvider.AUTHORITY + "/" + CONTENT_PATH);

	public static final String SORT_ORDER_PUB_DATE = COL_PUB_DATE + " DESC";
	public static final String SORT_ORDER_TITLE = COL_TITLE + " ASC";

	// Database creation SQL statements
	private static final String DATATABLE_CREATE = "create table " + TABLE_ITEMS
			+ " ("
			+ COL_ITEM_ID + " integer primary key autoincrement, "
			+ COL_CHANNEL_ID + " integer default -1, "
			+ COL_TITLE + " text collate nocase, "
			+ COL_LINK + " text collate nocase, "
			+ COL_DESCRIPTION + " text collate nocase, "
			+ COL_AUTHOR + " text collate nocase, "
			+ COL_CATEGORY + " text collate nocase, "
			+ COL_CATEGORY_DOMAIN + " text collate nocase, "
			+ COL_COMMENTS + " text collate nocase, "
			+ COL_ENCLOSURE_URL + " text collate nocase, "
			+ COL_ENCLOSURE_LENGTH + " integer default -1, "
			+ COL_ENCLOSURE_TYPE + " text collate nocase, "
			+ COL_GUID + " text collate nocase, "
			+ COL_GUID_PERMALINK + " integer default 1, "
			+ COL_PUB_DATE + " integer default -1, "
			+ COL_SOURCE + " text collate nocase, "
			+ COL_SOURCE_URL + " text collate nocase, "
			+ COL_IMAGE_ID + " integer default -1, "
			+ COL_ITEM_UPDATED + " integer default 0, "
			+ COL_ITEM_SELECTED + " integer default 0, "
			+ COL_ITEM_READ + " integer default 0 "
			+ ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATATABLE_CREATE);
		MyLog.i("RSS_ItemsTable", "onCreate: " + TABLE_ITEMS + " created.");

	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		MyLog.w(TABLE_ITEMS, "Upgrading database from version " + oldVersion + " to version " + newVersion);
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
		onCreate(database);
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Create Methods
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static long CreateItem(Context context, long channelID, ContentValues itemContentValues) {
		long newItemID = -1;
		Cursor ItemCursor = null;

		String guid = null;
		if (itemContentValues.containsKey(COL_GUID)) {
			guid = itemContentValues.getAsString(COL_GUID);
		} else {
			guid = String.valueOf(channelID);
		}

		if (channelID > 0) {
			// determine if the Item for the provided channel is already in the database
			ItemCursor = getItem(context, channelID, guid);
			if (ItemCursor != null && ItemCursor.getCount() > 0) {
				// the Item for the provided channel exist in the database
				ItemCursor.moveToFirst();
				newItemID = ItemCursor.getLong(ItemCursor.getColumnIndexOrThrow(COL_ITEM_ID));
				long existingPubDate = ItemCursor.getLong(ItemCursor.getColumnIndexOrThrow(COL_PUB_DATE));
				// check the published dates to see if the item has been updated.
				if (itemContentValues.containsKey(COL_PUB_DATE) && existingPubDate > 0) {
					long proposedPubDate = itemContentValues.getAsLong(COL_PUB_DATE);
					if (proposedPubDate > existingPubDate) {
						itemContentValues.put(COL_ITEM_UPDATED, 1);
						itemContentValues.put(COL_ITEM_READ, 0);
						UpdateItemlFieldValues(context, newItemID, itemContentValues);
					}
				} else {
					// the item exists in the database, but we don't know pub date ... so assume that it's an update
					itemContentValues.put(COL_ITEM_UPDATED, 1);
					itemContentValues.put(COL_ITEM_READ, 0);
					UpdateItemlFieldValues(context, newItemID, itemContentValues);
				}
				ItemCursor.close();
				return newItemID;

			} else {
				// the Item for the provided channel does not exist in the database ... so create it
				itemContentValues.put(COL_CHANNEL_ID, channelID);
				Uri uri = CONTENT_URI;
				ContentResolver cr = context.getContentResolver();
				if (itemContentValues.containsKey(COL_TITLE)) {
					String title = itemContentValues.getAsString(COL_TITLE);
					if (title.length() > TITLE_MAX_LENGTH) {
						title = title.substring(0, TITLE_MAX_LENGTH) + " ...";
						itemContentValues.put(COL_TITLE, title);
					}
				} else {
					String description = itemContentValues.getAsString(COL_DESCRIPTION);
					String title = description.substring(0, TITLE_MAX_LENGTH) + " ...";
					itemContentValues.put(COL_TITLE, title);
				}
				Uri newItemUri = cr.insert(uri, itemContentValues);
				if (newItemUri != null) {
					newItemID = Long.parseLong(newItemUri.getLastPathSegment());
				}
				// if itemContentValues does not contain a guid
				// then set the channelID as the guid and set the permaLink value to false
				if (newItemID > 0) {
					if (!itemContentValues.containsKey(COL_GUID)) {
						ContentValues newFieldValues = new ContentValues();
						newFieldValues = new ContentValues();
						newFieldValues.put(COL_GUID, newItemID);
						newFieldValues.put(COL_GUID_PERMALINK, 0);
						UpdateItemlFieldValues(context, newItemID, newFieldValues);
					}
				}
			}

			if (ItemCursor != null) {
				ItemCursor.close();
			}
		}
		return newItemID;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Read Methods
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static Cursor getItem(Context context, long itemID) {
		Uri uri = CONTENT_URI;
		String[] projection = PROJECTION_ALL;
		String selection = COL_ITEM_ID + " = ?";
		String selectionArgs[] = new String[] { String.valueOf(itemID) };
		String sortOrder = null;

		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
		} catch (Exception e) {
			MyLog.e("TABLE_ITEMS", "Exception error in getItem:");
			e.printStackTrace();
		}
		return cursor;
	}

	public static Cursor getItem(Context context, long channelID, String guid) {
		Uri uri = CONTENT_URI;
		String[] projection = PROJECTION_ALL;
		String selection = COL_CHANNEL_ID + " = ? AND " + COL_GUID + " = ?";
		String selectionArgs[] = new String[] { String.valueOf(channelID), guid };
		String sortOrder = null;

		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
		} catch (Exception e) {
			MyLog.e("TABLE_ITEMS", "Exception error in getItem:");
			e.printStackTrace();
		}
		return cursor;
	}

	public static boolean isItemUpdated(Context context, long itemID) {
		boolean result = false;
		Cursor cursor = getItem(context, itemID);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			int updatedValue = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ITEM_UPDATED));
			if (updatedValue == 1) {
				result = true;
			}
		}
		if (cursor != null) {
			cursor.close();
		}
		return result;
	}

	public static boolean isItemSelected(Context context, long itemID) {
		boolean result = false;
		Cursor cursor = getItem(context, itemID);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			int selectedValue = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ITEM_SELECTED));
			if (selectedValue == 1) {
				result = true;
			}
		}
		if (cursor != null) {
			cursor.close();
		}
		return result;
	}

	public static boolean isItemRead(Context context, long itemID) {
		boolean result = false;
		Cursor cursor = getItem(context, itemID);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			int selectedValue = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ITEM_READ));
			if (selectedValue == 1) {
				result = true;
			}
		}
		if (cursor != null) {
			cursor.close();
		}
		return result;
	}

	public static CursorLoader getAllItems(Context context, long channelID, String sortOrder) {
		Uri uri = CONTENT_URI;
		String[] projection = PROJECTION_ALL;
		String selection = null;
		String selectionArgs[] = null;
		if (channelID > 1) {
			selection = COL_CHANNEL_ID + " = ?";
			selectionArgs = new String[] { String.valueOf(channelID) };
		}

		CursorLoader cursorLoader = null;
		try {
			cursorLoader = new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);
		} catch (Exception e) {
			MyLog.e("RSS_ItemsTable", "Exception error in getAllItems:");
			e.printStackTrace();
		}
		return cursorLoader;
	}

	public static Cursor getAllItemsIDCursor(Context context, long channelID, String sortOrder) {
		Uri uri = CONTENT_URI;
		String[] projection = PROJECTION_ITEM_ID;
		String selection = null;
		String selectionArgs[] = null;
		if (channelID > 1) {
			selection = COL_CHANNEL_ID + " = ?";
			selectionArgs = new String[] { String.valueOf(channelID) };
		}

		Cursor cursor = null;
		ContentResolver cr = context.getContentResolver();
		try {
			cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
		} catch (Exception e) {
			MyLog.e("TABLE_ITEMS", "Exception error in getAllItemsCursor:");
			e.printStackTrace();
		}

		return cursor;
	}

	public static long getChannelID(Context context, long itemID) {
		long channelID = -1;
		Cursor cursor = getItem(context, itemID);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			channelID = cursor.getLong(cursor.getColumnIndexOrThrow(COL_CHANNEL_ID));
		}
		if (cursor != null) {
			cursor.close();
		}
		return channelID;
	}

	public static Cursor getAllSelectedItems(Context context, long channelID) {

		Uri uri = CONTENT_URI;
		String[] projection = PROJECTION_ITEM_ID;
		String selection = null;
		String selectionArgs[] = null;
		String sortOrder = null;
		if (channelID > 1) {
			selection = COL_CHANNEL_ID + " = ? AND " + COL_ITEM_SELECTED + " = ?";
			selectionArgs = new String[] { String.valueOf(channelID), String.valueOf(1) };
		} else {
			selection = COL_ITEM_SELECTED + " = ?";
			selectionArgs = new String[] { String.valueOf(1) };
		}

		Cursor cursor = null;
		ContentResolver cr = context.getContentResolver();
		try {
			cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
		} catch (Exception e) {
			MyLog.e("TABLE_ITEMS", "Exception error in getAllItemsCursor:");
			e.printStackTrace();
		}

		return cursor;
	}

	public static int getNumberOfSelectedItems(Context context, long channelID) {
		int numberOfSelectedItems = 0;

		Cursor cursor = getAllSelectedItems(context, channelID);

		if (cursor != null) {
			numberOfSelectedItems = cursor.getCount();
			cursor.close();
		}

		return numberOfSelectedItems;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Update Methods
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static int UpdateItemlFieldValues(Context context, long itemID, ContentValues newFieldValues) {
		int numberOfUpdatedRecords = -1;
		if (itemID > 0) {
			ContentResolver cr = context.getContentResolver();
			Uri channelUri = CONTENT_URI;
			String selection = COL_ITEM_ID + " = ?";
			String selectionArgs[] = new String[] { String.valueOf(itemID) };
			numberOfUpdatedRecords = cr.update(channelUri, newFieldValues, selection, selectionArgs);
		}
		return numberOfUpdatedRecords;
	}

	public static int setItemAsRead(Context context, long itemID) {
		int numberOfUpdatedRecords = -1;
		Uri uri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(itemID));
		String selection = null;
		String[] selectionArgs = null;

		ContentValues newFieldValues = new ContentValues();
		newFieldValues.put(COL_ITEM_READ, 1);

		ContentResolver cr = context.getContentResolver();
		numberOfUpdatedRecords = cr.update(uri, newFieldValues, selection, selectionArgs);

		return numberOfUpdatedRecords;
	}

	public static void setItemSelection(Context context, long itemID, boolean itemSelected) {

		int selectedValue = 0; // not selected
		if (itemSelected) {
			selectedValue = 1; // selected
		}

		ContentValues newFieldValues = new ContentValues();
		newFieldValues.put(COL_ITEM_SELECTED, selectedValue);
		UpdateItemlFieldValues(context, itemID, newFieldValues);

	}

	public static int SelectItemsOlderThan(Context context, long channelID, long numberOfHours) {
		int numberOfUpdatedRecords = -1;

		Calendar rightNow = Calendar.getInstance();
		long age = numberOfHours * 60 * 60 * 1000;
		long selectionTimeMills = rightNow.getTimeInMillis() - age;

		Uri uri = CONTENT_URI;
		String selection = null;
		String selectionArgs[] = null;
		if (channelID > 1) {
			selection = COL_CHANNEL_ID + " = ? AND " + COL_PUB_DATE + " < ?";
			selectionArgs = new String[] { String.valueOf(channelID), String.valueOf(selectionTimeMills) };
		} else {
			selection = COL_PUB_DATE + " < ?";
			selectionArgs = new String[] { String.valueOf(selectionTimeMills) };
		}
		ContentValues newFieldValues = new ContentValues();
		newFieldValues.put(COL_ITEM_SELECTED, 1);

		ContentResolver cr = context.getContentResolver();
		numberOfUpdatedRecords = cr.update(uri, newFieldValues, selection, selectionArgs);

		return numberOfUpdatedRecords;
	}

	public static int SelectAllItems(Context context, long channelID) {
		int numberOfUpdatedRecords = -1;
		Uri uri = CONTENT_URI;
		String selection = null;
		String selectionArgs[] = null;
		if (channelID > 1) {
			selection = COL_CHANNEL_ID + " = ?";
			selectionArgs = new String[] { String.valueOf(channelID) };
		}

		ContentValues newFieldValues = new ContentValues();
		newFieldValues.put(COL_ITEM_SELECTED, 1);

		ContentResolver cr = context.getContentResolver();
		numberOfUpdatedRecords = cr.update(uri, newFieldValues, selection, selectionArgs);
		return numberOfUpdatedRecords;
	}

	public static int SelectAllReadItems(Context context, long channelID) {
		int numberOfUpdatedRecords = -1;
		Uri uri = CONTENT_URI;
		String selection = null;
		String selectionArgs[] = null;
		if (channelID > 1) {
			selection = COL_CHANNEL_ID + " = ? AND " + COL_ITEM_READ + " = ?";
			selectionArgs = new String[] { String.valueOf(channelID), String.valueOf(1) };
		} else {
			selection = COL_ITEM_READ + " = ?";
			selectionArgs = new String[] { String.valueOf(1) };
		}

		ContentValues newFieldValues = new ContentValues();
		newFieldValues.put(COL_ITEM_SELECTED, 1);

		ContentResolver cr = context.getContentResolver();
		numberOfUpdatedRecords = cr.update(uri, newFieldValues, selection, selectionArgs);
		return numberOfUpdatedRecords;
	}

	public static int DeselectAllSelectedItems(Context context) {
		int numberOfUpdatedRecords = -1;

		Uri uri = CONTENT_URI;
		String selection = null;
		String selectionArgs[] = null;
		selection = COL_ITEM_SELECTED + " = ?";
		selectionArgs = new String[] { String.valueOf(1) };

		ContentValues newFieldValues = new ContentValues();
		newFieldValues.put(COL_ITEM_SELECTED, 0);

		ContentResolver cr = context.getContentResolver();
		numberOfUpdatedRecords = cr.update(uri, newFieldValues, selection, selectionArgs);

		return numberOfUpdatedRecords;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Delete Methods
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static int DeleteAllItemsInChannel(Context context, long channelID) {
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

	public static int DeleteItem(Context context, long itemID) {
		int numberOfDeletedRecords = -1;
		if (itemID > 0) {
			ContentResolver cr = context.getContentResolver();
			Uri uri = CONTENT_URI;
			String selection = COL_ITEM_ID + " = ?";
			String selectionArgs[] = new String[] { String.valueOf(itemID) };
			numberOfDeletedRecords = cr.delete(uri, selection, selectionArgs);
			RSS_ImagesTable.DeleteAllImagesInItem(context, itemID);
		}
		return numberOfDeletedRecords;
	}

	public static int DeleteAllSelectedItems(Context context, long channelID) {
		int numberOfDeletedRecords = 0;

		Cursor cursor = getAllSelectedItems(context, channelID);
		if (cursor != null) {
			long itemID = -1;
			while (cursor.moveToNext()) {
				itemID = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ITEM_ID));
				numberOfDeletedRecords += DeleteItem(context, itemID);
			}
			cursor.close();
		}

		return numberOfDeletedRecords;
	}

}
