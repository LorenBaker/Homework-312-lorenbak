package com.lbconsulting.homework_312_lorenbak.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lbconsulting.homework_312_lorenbak.MyLog;

public class RSS_DatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "HW312.db";
	private static final int DATABASE_VERSION = 1;

	private static SQLiteDatabase dBase;

	public RSS_DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		RSS_DatabaseHelper.dBase = database;

		MyLog.i("RSS_DatabaseHelper", "onCreate");
		RSS_ChannelsTable.onCreate(database);
		RSS_ImagesTable.onCreate(database);
		RSS_ItemsTable.onCreate(database);
		RSS_SkipDaysTable.onCreate(database);
		RSS_SkipHoursTable.onCreate(database);
		RSS_TextInputsTable.onCreate(database);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		MyLog.i("RSS_DatabaseHelper", "onUpgrade");
		RSS_ChannelsTable.onUpgrade(database, oldVersion, newVersion);
		RSS_ImagesTable.onUpgrade(database, oldVersion, newVersion);
		RSS_ItemsTable.onUpgrade(database, oldVersion, newVersion);
		RSS_SkipDaysTable.onUpgrade(database, oldVersion, newVersion);
		RSS_SkipHoursTable.onUpgrade(database, oldVersion, newVersion);
		RSS_TextInputsTable.onUpgrade(database, oldVersion, newVersion);
	}

	public static SQLiteDatabase getDatabase() {
		return dBase;
	}

}
