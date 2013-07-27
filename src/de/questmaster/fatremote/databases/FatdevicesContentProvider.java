/*
 ******************************************************************************
 * Parts of this code sample are licensed under Apache License, Version 2.0   *
 * Copyright (c) 2009, Android Open Handset Alliance. All rights reserved.    *
 *																			  *																			*
 * Except as noted, this code sample is offered under a modified BSD license. *
 * Copyright (C) 2010, Motorola Mobility, Inc. All rights reserved.           *
 * 																			  *
 * For more details, see MOTODEV_Studio_for_Android_LicenseNotices.pdf        * 
 * in your installation folder.                                               *
 ******************************************************************************
 */
package de.questmaster.fatremote.databases;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class FatdevicesContentProvider extends ContentProvider {

	private FatDevicesDbHelper dbHelper;
	private static HashMap<String, String> FATDEVICES_PROJECTION_MAP;
	private static final String TABLE_NAME = "fatdevices";
	private static final String AUTHORITY = "de.questmaster.fatremote.databases.fatdevicescontentprovider";

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
	public static final Uri _ID_FIELD_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME.toLowerCase());
	public static final Uri NAME_FIELD_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME.toLowerCase() + "/name");
	public static final Uri IP_FIELD_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME.toLowerCase() + "/ip");
	public static final Uri PORT_FIELD_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME.toLowerCase() + "/port");
	public static final Uri AUTO_FIELD_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME.toLowerCase() + "/auto");
	public static final Uri STORAGE_FIELD_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME.toLowerCase() + "/storage");

	public static final String DEFAULT_SORT_ORDER = "_id ASC";

	private static final UriMatcher URL_MATCHER;

	private static final int FATDEVICES = 1;
	private static final int FATDEVICES__ID = 2;
	private static final int FATDEVICES_NAME = 3;
	private static final int FATDEVICES_IP = 4;
	private static final int FATDEVICES_PORT = 5;
	private static final int FATDEVICES_AUTO = 6;
	private static final int FATDEVICES_STORAGE = 7;

	// Content values keys (using column names)
	public static final String _ID = "_id";
	public static final String NAME = "name";
	public static final String IP = "ip";
	public static final String PORT = "port";
	public static final String AUTO = "auto";
	public static final String STORAGE = "storage";

	public boolean onCreate() {
		dbHelper = new FatDevicesDbHelper(getContext(), true);
		return (dbHelper == null) ? false : true;
	}

	public Cursor query(Uri url, String[] projection, String selection, String[] selectionArgs, String sort) {
		SQLiteDatabase mDB = dbHelper.getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		switch (URL_MATCHER.match(url)) {
		case FATDEVICES:
			qb.setTables(TABLE_NAME);
			qb.setProjectionMap(FATDEVICES_PROJECTION_MAP);
			break;
		case FATDEVICES__ID:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("_id=" + url.getPathSegments().get(1));
			break;
		case FATDEVICES_NAME:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("name='" + url.getPathSegments().get(2) + "'");
			break;
		case FATDEVICES_IP:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("ip='" + url.getPathSegments().get(2) + "'");
			break;
		case FATDEVICES_PORT:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("port='" + url.getPathSegments().get(2) + "'");
			break;
		case FATDEVICES_AUTO:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("auto='" + url.getPathSegments().get(2) + "'");
			break;
		case FATDEVICES_STORAGE:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("storage='" + url.getPathSegments().get(2) + "'");
			break;

		default:
			throw new IllegalArgumentException("Unknown URL " + url);
		}
		String orderBy = "";
		if (TextUtils.isEmpty(sort)) {
			orderBy = DEFAULT_SORT_ORDER;
		} else {
			orderBy = sort;
		}
		Cursor c = qb.query(mDB, projection, selection, selectionArgs, null, null, orderBy);
		c.setNotificationUri(getContext().getContentResolver(), url);
		return c;
	}

	public String getType(Uri url) {
		switch (URL_MATCHER.match(url)) {
		case FATDEVICES:
			return "vnd.android.cursor.dir/vnd.de.questmaster.fatremote.databases.fatdevices";
		case FATDEVICES__ID:
			return "vnd.android.cursor.item/vnd.de.questmaster.fatremote.databases.fatdevices";
		case FATDEVICES_NAME:
			return "vnd.android.cursor.item/vnd.de.questmaster.fatremote.databases.fatdevices";
		case FATDEVICES_IP:
			return "vnd.android.cursor.item/vnd.de.questmaster.fatremote.databases.fatdevices";
		case FATDEVICES_PORT:
			return "vnd.android.cursor.item/vnd.de.questmaster.fatremote.databases.fatdevices";
		case FATDEVICES_AUTO:
			return "vnd.android.cursor.item/vnd.de.questmaster.fatremote.databases.fatdevices";
		case FATDEVICES_STORAGE:
			return "vnd.android.cursor.item/vnd.de.questmaster.fatremote.databases.fatdevices";

		default:
			throw new IllegalArgumentException("Unknown URL " + url);
		}
	}

	public Uri insert(Uri url, ContentValues initialValues) {
		SQLiteDatabase mDB = dbHelper.getWritableDatabase();
		long rowID;
		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}
		if (URL_MATCHER.match(url) != FATDEVICES) {
			throw new IllegalArgumentException("Unknown URL " + url);
		}

		rowID = mDB.insert("fatdevices", "fatdevices", values);
		if (rowID > 0) {
			Uri uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
			getContext().getContentResolver().notifyChange(uri, null);
			return uri;
		}
		throw new SQLException("Failed to insert row into " + url);
	}

	public int delete(Uri url, String where, String[] whereArgs) {
		SQLiteDatabase mDB = dbHelper.getWritableDatabase();
		int count;
		String segment = "";
		switch (URL_MATCHER.match(url)) {
		case FATDEVICES:
			count = mDB.delete(TABLE_NAME, where, whereArgs);
			break;
		case FATDEVICES__ID:
			segment = url.getPathSegments().get(1);
			count = mDB.delete(TABLE_NAME, "_id=" + segment + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
			break;
		case FATDEVICES_NAME:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.delete(TABLE_NAME, "name=" + segment + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
			break;
		case FATDEVICES_IP:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.delete(TABLE_NAME, "ip=" + segment + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
			break;
		case FATDEVICES_PORT:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.delete(TABLE_NAME, "port=" + segment + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
			break;
		case FATDEVICES_AUTO:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.delete(TABLE_NAME, "auto=" + segment + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
			break;
		case FATDEVICES_STORAGE:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.delete(TABLE_NAME, "storage=" + segment + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URL " + url);
		}
		getContext().getContentResolver().notifyChange(url, null);
		return count;
	}

	public int update(Uri url, ContentValues values, String where, String[] whereArgs) {
		SQLiteDatabase mDB = dbHelper.getWritableDatabase();
		int count;
		String segment = "";
		switch (URL_MATCHER.match(url)) {
		case FATDEVICES:
			count = mDB.update(TABLE_NAME, values, where, whereArgs);
			break;
		case FATDEVICES__ID:
			segment = url.getPathSegments().get(1);
			count = mDB.update(TABLE_NAME, values, "_id=" + segment + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
			break;
		case FATDEVICES_NAME:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.update(TABLE_NAME, values, "name=" + segment + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
			break;
		case FATDEVICES_IP:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.update(TABLE_NAME, values, "ip=" + segment + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
			break;
		case FATDEVICES_PORT:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.update(TABLE_NAME, values, "port=" + segment + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
			break;
		case FATDEVICES_AUTO:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.update(TABLE_NAME, values, "auto=" + segment + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
			break;
		case FATDEVICES_STORAGE:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.update(TABLE_NAME, values, "storage=" + segment + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URL " + url);
		}
		getContext().getContentResolver().notifyChange(url, null);
		return count;
	}

	static {
		URL_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase(), FATDEVICES);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/#", FATDEVICES__ID);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/name" + "/*", FATDEVICES_NAME);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/ip" + "/*", FATDEVICES_IP);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/port" + "/*", FATDEVICES_PORT);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/auto" + "/*", FATDEVICES_AUTO);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/storage" + "/*", FATDEVICES_STORAGE);

		FATDEVICES_PROJECTION_MAP = new HashMap<String, String>();
		FATDEVICES_PROJECTION_MAP.put(_ID, "_id");
		FATDEVICES_PROJECTION_MAP.put(NAME, "name");
		FATDEVICES_PROJECTION_MAP.put(IP, "ip");
		FATDEVICES_PROJECTION_MAP.put(PORT, "port");
		FATDEVICES_PROJECTION_MAP.put(AUTO, "auto");
		FATDEVICES_PROJECTION_MAP.put(STORAGE, "storage");

	}
}
