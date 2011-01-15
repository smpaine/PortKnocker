package com.smpaine.portknocker;

/**
 * Port Knocker A port knocking application for android Based off of the
 * original PortKnocking application by Alexis Robert Under GPL 3 License
 * http://www.gnu.org/licenses/gpl.txt
 * 
 * Copyright Stephen Paine 2009,2010
 */

import java.util.StringTokenizer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter {
	private Context ctx;
	private DBOpenHelper helper;
	private SQLiteDatabase db;

	// Key used by both tables
	public static final String KEY_ID = "_id";
	public static final String KEY_PORT = "port";

	// Keys from hosts table
	public static final String KEY_DB = "hosts";
	public static final String KEY_LABEL = "label";
	public static final String KEY_HOST = "host";
	public static final String KEY_TIMEOUT = "timeout";
	public static final String KEY_NICKNAME = "nickname";
	public static final String KEY_USERNAME = "username";

	// Keys from ports table
	public static final String KEY_DB_PORTS = "ports";
	public static final String KEY_HOST_ID = "hostid";
	public static final String KEY_PACKETTYPE = "packetType";

	private static class DBOpenHelper extends SQLiteOpenHelper {
		public DBOpenHelper (Context context) {
			super(context, "hostlist.db", null, 7);
		}

		@Override
		public void onCreate (SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + KEY_DB + " (" + KEY_ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_LABEL
					+ " STRING NOT NULL DEFAULT '', " + KEY_HOST + " STRING, "
					+ KEY_TIMEOUT + " STRING NOT NULL DEFAULT \"1000\", "
					+ KEY_NICKNAME + " STRING NOT NULL DEFAULT \"\", "
					+ KEY_USERNAME + " STRING NOT NULL DEFAULT \"\", "
					+ KEY_PORT + " STRING NOT NULL DEFAULT \"22\");");
			db.execSQL("CREATE TABLE " + KEY_DB_PORTS + " (" + KEY_ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_HOST_ID
					+ " STRING, " + KEY_PORT + " STRING, " + KEY_PACKETTYPE
					+ " STRING NOT NULL DEFAULT \"TCP\");");
		}

		@Override
		public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
			Cursor host;
			StringTokenizer stok;
			String timeout;
			long hostID;
			ContentValues values;

			if (oldVersion <= 3) {
				db.execSQL("ALTER TABLE " + KEY_DB + " ADD COLUMN "
						+ KEY_USERNAME + " STRING NOT NULL DEFAULT \"\"");
				db.execSQL("ALTER TABLE " + KEY_DB + " ADD COLUMN "
						+ KEY_NICKNAME + " STRING NOT NULL DEFAULT \"\"");
				db.execSQL("ALTER TABLE " + KEY_DB + " ADD COLUMN " + KEY_PORT
						+ " STRING NOT NULL DEFAULT \"22\"");
				// Log.v("dbUpgrade", "Upgraded to version 3");
			}

			if (oldVersion <= 4) {
				db.execSQL("ALTER TABLE " + KEY_DB + " ADD COLUMN "
						+ KEY_PACKETTYPE + " STRING NOT NULL DEFAULT \"TCP\"");
				// Log.v("dbUpgrade", "Upgraded to version 4");
			}

			if (oldVersion <= 5) {
				// Log.v("dbUpgrade", "Upgrading to version 5");
				db.execSQL("ALTER TABLE " + KEY_DB + " RENAME TO old");
				// Log.v("dbUpgrade", "Altered table to old");
				db.execSQL("CREATE TABLE " + KEY_DB + " (" + KEY_ID
						+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_HOST
						+ " STRING, " + KEY_TIMEOUT
						+ " STRING NOT NULL DEFAULT \"1000\", " + KEY_NICKNAME
						+ " STRING NOT NULL DEFAULT \"\", " + KEY_USERNAME
						+ " STRING NOT NULL DEFAULT \"\", " + KEY_PORT
						+ " STRING NOT NULL DEFAULT \"22\");");
				// Log.v("dbUpgrade", "Created new table hosts");
				db.execSQL("CREATE TABLE " + KEY_DB_PORTS + " (" + KEY_ID
						+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_HOST_ID
						+ " STRING, " + KEY_PORT + " STRING, " + KEY_PACKETTYPE
						+ " STRING NOT NULL DEFAULT \"TCP\");");
				// Log.v("dbUpgrade", "Created new table ports");
				host = db.query("old", null, null, null, null, null, null);
				if (host != null) {
					// Log.v("dbUpgrade", "host not null!");
					host.moveToFirst();
					if (host.isFirst()) {
						// Log.v("dbUpgrade", "at first host");
						do {
							// Log.v("dbUpgrade", "cycling through...");
							values = new ContentValues();
							values.put(KEY_HOST, host.getString(host
									.getColumnIndex(KEY_HOST)));
							timeout = host.getString(host
									.getColumnIndex(KEY_TIMEOUT));
							if (timeout.length() > 0) {
								values.put(KEY_TIMEOUT, timeout);
							} else {
								values.put(KEY_TIMEOUT, "1000");
							}
							values.put(KEY_NICKNAME, host.getString(host
									.getColumnIndex(KEY_NICKNAME)));
							values.put(KEY_USERNAME, host.getString(host
									.getColumnIndex(KEY_USERNAME)));
							values.put(KEY_PORT, host.getString(host
									.getColumnIndex(KEY_PORT)));
							hostID = db.insert(KEY_DB, null, values);
							// Log.v("dbUpgrade", "Added host: "+
							// host.getString(host.getColumnIndex(KEY_HOST)));
							stok = new StringTokenizer(host.getString(host
									.getColumnIndex("ports")), ",");
							// Check ports list for actual port number(s)
							while (stok.hasMoreTokens()) {
								values = new ContentValues();
								values.put(KEY_HOST_ID, hostID);
								values.put(KEY_PORT, stok.nextToken());
								values.put(KEY_PACKETTYPE, host.getString(host
										.getColumnIndex(KEY_PACKETTYPE)));
								db.insert(KEY_DB_PORTS, null, values);
							}
						} while (host.moveToNext());
					}
				}
				host.close();
				// Log.v("dbUpgrade", "Finished adding host(s)");
				db.execSQL("DROP TABLE old");
				// Log.v("dbUpgrade", "Upgrade to version 5 done.");
			}

			if (oldVersion <= 6) {
				// Log.v("dbUpgrade", "Upgrading to version 6");

				db.execSQL("ALTER TABLE " + KEY_DB + " RENAME TO old");
				// Log.v("dbUpgrade", "Altered table to old");
				db.execSQL("CREATE TABLE " + KEY_DB + " (" + KEY_ID
						+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_LABEL
						+ " STRING NOT NULL DEFAULT '', " + KEY_HOST
						+ " STRING, " + KEY_TIMEOUT
						+ " STRING NOT NULL DEFAULT \"1000\", " + KEY_NICKNAME
						+ " STRING NOT NULL DEFAULT \"\", " + KEY_USERNAME
						+ " STRING NOT NULL DEFAULT \"\", " + KEY_PORT
						+ " STRING NOT NULL DEFAULT \"22\");");
				// Log.v("dbUpgrade", "Created new table hosts");;
				host = db.query("old", null, null, null, null, null, null);
				if (host != null) {
					// Log.v("dbUpgrade", "host not null!");
					host.moveToFirst();
					if (host.isFirst()) {
						// Log.v("dbUpgrade", "at first host");
						do {
							// Log.v("dbUpgrade", "cycling through...");
							values = new ContentValues();
							values.put(KEY_HOST, host.getString(host
									.getColumnIndex(KEY_HOST)));
							timeout = host.getString(host
									.getColumnIndex(KEY_TIMEOUT));
							if (timeout.length() > 0) {
								values.put(KEY_TIMEOUT, timeout);
							} else {
								values.put(KEY_TIMEOUT, "1000");
							}
							values.put(KEY_NICKNAME, host.getString(host
									.getColumnIndex(KEY_NICKNAME)));
							values.put(KEY_USERNAME, host.getString(host
									.getColumnIndex(KEY_USERNAME)));
							values.put(KEY_PORT, host.getString(host
									.getColumnIndex(KEY_PORT)));
							values.put(KEY_LABEL, values
									.getAsString(KEY_USERNAME)
									+ "@" + values.getAsString(KEY_HOST));
							hostID = db.insert(KEY_DB, null, values);
							// Log.v("dbUpgrade", "Added host: "+
						} while (host.moveToNext());
					}
					// Log.v("dbUpgrade", "Done upgrading to version 6");
				}
				host.close();
				// Log.v("dbUpgrade", "Finished adding host(s)");
				db.execSQL("DROP TABLE old");
				// Log.v("dbUpgrade", "Upgrade to version 5 done.");
			}
			// The easy way out...not my way
			// db.execSQL("DROP TABLE IF EXISTS "+KEY_DB);
			// onCreate(db);
		}
	}

	public DBAdapter (Context ctx) {
		this.ctx = ctx;
	}

	public void open () {
		helper = new DBOpenHelper(this.ctx);
		db = helper.getWritableDatabase();
	}

	public void close () {
		db.close();
	}

	public long[] getHosts () {
		Cursor c = db.query(KEY_DB, new String[] { KEY_ID }, null, null, null,
				null, null);

		long[] data = new long[c.getCount()];
		c.moveToFirst();

		for (int i = 0; i < c.getCount(); i++) {
			data[i] = c.getInt(0);
			c.moveToNext();
		}

		c.close();

		return data;
	}
	
	public String getLabel (long id) {
		Cursor c = db.query(KEY_DB, new String[] { KEY_LABEL }, KEY_ID + " = "
				+ id, null, null, null, null);

		c.moveToFirst();
		String label = c.getString(0);
		c.close();

		return label;
	}

	public String getHost (long id) {
		Cursor c = db.query(KEY_DB, new String[] { KEY_HOST }, KEY_ID + " = "
				+ id, null, null, null, null);

		c.moveToFirst();
		String host = c.getString(0);
		c.close();

		return host;
	}

	public String getTimeout (long id) {
		Cursor c = db.query(KEY_DB, new String[] { KEY_TIMEOUT }, KEY_ID
				+ " = " + id, null, null, null, null);
		c.moveToFirst();
		String timeout = c.getString(0);
		c.close();

		return timeout;
	}

	public String getNickname (long id) {
		Cursor c = db.query(KEY_DB, new String[] { KEY_NICKNAME }, KEY_ID
				+ " = " + id, null, null, null, null);
		c.moveToFirst();
		String timeout = c.getString(0);
		c.close();

		return timeout;
	}

	public String getUsername (long id) {
		Cursor c = db.query(KEY_DB, new String[] { KEY_USERNAME }, KEY_ID
				+ " = " + id, null, null, null, null);
		c.moveToFirst();
		String timeout = c.getString(0);
		c.close();

		return timeout;
	}

	public String getPort (long id) {
		Cursor c = db.query(KEY_DB, new String[] { KEY_PORT }, KEY_ID + " = "
				+ id, null, null, null, null);
		c.moveToFirst();
		String timeout = c.getString(0);
		c.close();

		return timeout;
	}

	public String getPacketType (long id) {
		Cursor c = db.query(KEY_DB, new String[] { KEY_PACKETTYPE }, KEY_ID
				+ " = " + id, null, null, null, null);
		c.moveToFirst();
		String packetType = c.getString(0);
		c.close();

		return packetType;
	}

	public Cursor getRawHosts () {
		return db.query(KEY_DB, null, null, null, null, null, KEY_ID + " ASC");
	}

	public Cursor getRawHost (long id) {
		Cursor c = db.query(KEY_DB, null, KEY_ID + " = " + String.valueOf(id),
				null, null, null, null);
		if (c != null)
			c.moveToFirst();
		return c;
	}

	public Cursor getRawPorts (long id) {
		Cursor c = db.query(KEY_DB_PORTS, null, KEY_HOST_ID + " = "
				+ String.valueOf(id), null, null, null, KEY_ID + " ASC");
		if (c != null)
			c.moveToFirst();
		return c;
	}

	public Cursor getRawPort (long id) {
		Cursor c = db.query(KEY_DB_PORTS, null, KEY_ID + " = "
				+ String.valueOf(id), null, null, null, null);
		if (c != null)
			c.moveToFirst();
		return c;
	}

	public long createHost (String label, String host, String timeout, String type,
			String username, String port) {
		ContentValues values = new ContentValues();
		values.put(KEY_LABEL, label);
		values.put(KEY_HOST, host);
		values.put(KEY_TIMEOUT, timeout);
		values.put(KEY_NICKNAME, type);
		values.put(KEY_USERNAME, username);
		values.put(KEY_PORT, port);
		return db.insert(KEY_DB, null, values);
	}

	public void updateHost (long id, String label, String host, String timeout, String type,
			String username, String port) {
		ContentValues values = new ContentValues();
		values.put(KEY_LABEL, label);
		values.put(KEY_HOST, host);
		values.put(KEY_TIMEOUT, timeout);
		values.put(KEY_NICKNAME, type);
		values.put(KEY_USERNAME, username);
		values.put(KEY_PORT, port);
		db.update(KEY_DB, values, KEY_ID + "=" + id, null);
	}

	public void addPort (long hostID, String port, String packetType) {
		ContentValues values = new ContentValues();
		values.put(KEY_HOST_ID, hostID);
		values.put(KEY_PORT, port);
		values.put(KEY_PACKETTYPE, packetType);
		db.insert(KEY_DB_PORTS, null, values);
	}

	public void updatePort (long id, long hostID, String port, String packetType) {
		ContentValues values = new ContentValues();
		values.put(KEY_HOST_ID, hostID);
		values.put(KEY_PORT, port);
		values.put(KEY_PACKETTYPE, packetType);
		db.update(KEY_DB_PORTS, values, KEY_ID + "=" + id, null);
	}

	public void deleteHost (long id) {
		db.delete(KEY_DB, KEY_ID + "=" + id, null);
		db.delete(KEY_DB_PORTS, KEY_HOST_ID + "=" + id, null);
	}

	public void deletePort (long id) {
		db.delete(KEY_DB_PORTS, KEY_ID + "=" + id, null);
	}
}
