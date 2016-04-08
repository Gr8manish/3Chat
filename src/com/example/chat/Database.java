package com.example.chat;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "Mchat";
	private static final String TABLE_USERS = "chat";
	private static final String KEY_TO = "to_";
	private static final String KEY_FROM = "from_";
	private static final String KEY_MSG = "message";

	public Database(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USERS + " ( "
				+ KEY_TO + " TEXT, " + KEY_FROM + " TEXT, " + KEY_MSG
				+ " TEXT " + ")";
		db.execSQL(CREATE_LOGIN_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
		onCreate(db);
	}

	public void addMsg(String to, String from, String msg) {
		SQLiteDatabase db = this.getWritableDatabase();
		System.out.println("Inside addMsg="+to+" "+from+" "+msg);
		ContentValues values = new ContentValues();
		values.put(KEY_TO, to);
		values.put(KEY_FROM, from);
		values.put(KEY_MSG, msg);
		db.insert(TABLE_USERS, null, values);
		db.close();
	}

	public ArrayList<User_object> getSpecificUserMsgs(String toChatting) {
		ArrayList<User_object> arrList = new ArrayList<User_object>();
		String selectQuery = "SELECT  * FROM " + TABLE_USERS + " WHERE "
				+ KEY_TO + "='" + toChatting + "' OR " + KEY_FROM + "='"
				+ toChatting + "'";
		System.out.println("selectQuery=" + selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		//System.out.println("counttttt=" + cursor.getCount());
		cursor.moveToFirst();
		int count;
		if ((count = cursor.getCount()) > 0) {
			while (count-- != 0) {
				String to_ = cursor.getString(0);
				String from_ = cursor.getString(1);
				String msg = cursor.getString(2);
				if (cursor.getString(0).equals("You")) {
					arrList.add(new User_object(from_, msg));
				} else {
					arrList.add(new User_object("You", msg));
				}
				// System.out.println(to_ + " " + from_ + " " + msg);
				cursor.moveToNext();
			}
		}
		cursor.close();
		db.close();

		/*
		 * for(User_object ob:arrList){
		 * System.out.println(ob.getUsername()+" "+ob.getmessage()); }
		 */

		return arrList;
	}

	public int getRowCount() {
		String countQuery = "SELECT  * FROM " + TABLE_USERS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int rowCount = cursor.getCount();
		db.close();
		cursor.close();
		return rowCount;
	}
}
