package com.atiffarrukh.callandsmsblocker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

	public DatabaseOpenHelper(Context context, String dbName, CursorFactory factory,
			int version) {
		// TODO Auto-generated constructor stub
		super(context,dbName,factory,version);
	}

	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		 String createQuery = "CREATE TABLE contact (_id integer primary key autoincrement,name, number);";                 
	        db.execSQL(createQuery);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	
	}

	
}