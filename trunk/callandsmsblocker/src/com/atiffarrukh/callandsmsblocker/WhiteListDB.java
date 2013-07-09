/*	Copyright 2012 Atif Farrukh

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.*/
package com.atiffarrukh.callandsmsblocker;

import java.util.Currency;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;


/**
 * This class contains Database for the whitelist of the app
 * The number Stored in this Database will be only answered.
 * all other calls will be rejected.
 */

public class WhiteListDB {
	
	SharedPreferences prefs;
	boolean number;
	
	//Define database name and version
	private static final String DATABASE_NAME = "WhiteList";
	private static final String DATABASE_TABLE = "WhiteListTable";

	private static final int DATABASE_VERSION = 2;
	
	//Define Columns
	public static final String KEY_ROWID = "_id";
	public static final int INDEX_ROWID = 0;
	public static final String KEY_NAME = "name";
	public static final String KEY_NUMBER = "phone_number";
	
	public static final String[] KEY_ALL = {WhiteListDB.KEY_ROWID,WhiteListDB.KEY_NUMBER};
	public static final String[] KEY_ENTER_NUMBER = {WhiteListDB.KEY_NUMBER};
	
	public static final String[] KEY_ALL_NAME = {WhiteListDB.KEY_ROWID,WhiteListDB.KEY_NAME};
	public static final String[] KEY_ENTER_NAME= {WhiteListDB.KEY_NAME};
	
	//Define database variables
	private DbHelper ourHelper;
	private final Context ourContext;
	private SQLiteDatabase ourDatabase;
	
	/** Construct DB for this activity context. */
	public WhiteListDB(Context c){
		ourContext = c;
		prefs = PreferenceManager.getDefaultSharedPreferences(c);
		number = prefs.getBoolean("number", true);
	}
	
	/** Opens up a connection to the database. Do this before any operations. 
	 * @return */
	public void Open() throws SQLException{
		ourHelper = new DbHelper(ourContext);
		ourDatabase = ourHelper.getWritableDatabase(); 
	}
	
	/** Closes the database connection. Operations are not valid after this. */
	public void Close(){
		ourHelper.close();
		ourHelper = null;
		ourDatabase = null;
	}
	
	/**
	  Creates and inserts a new row using the given values.
	  Returns the rowid of the new row, or -1 on error.
	  todo: values should not include a rowid I assume.
	 */
	
	public long createRow(ContentValues values){
		return ourDatabase.insert(DATABASE_TABLE, null, values);
	}
	
	/**
	 Updates the given rowid with the given values.
	 Returns true if there was a change (i.e. the rowid was valid).
	 * @return 
	 */
	public boolean updateRow(long rowId, ContentValues values){
		return ourDatabase.update(DATABASE_TABLE, values,
				BlockListDB.KEY_ROWID + "=" + rowId, null) > 0;
	}
	
	/**
	 Deletes the given rowid.
	 Returns true if any rows were deleted (i.e. the id was valid).
	*/
	public boolean deleteRow(long rowId) {
		return ourDatabase.delete(DATABASE_TABLE,
				WhiteListDB.KEY_ROWID + "=" + rowId, null) > 0;
	}
	
	/** Returns a cursor for all the rows. Caller should close or manage the cursor. */
	public Cursor queryAll() {
		if (number) {
			return ourDatabase.query(DATABASE_TABLE,
					KEY_ALL,  // i.e. return all columns; 2 columns in this case
					null, null, null, null,
					WhiteListDB.KEY_NUMBER+ " ASC"  // order-by, "DESC" for descending
				);
		}
		else{
			return ourDatabase.query(DATABASE_TABLE,
					KEY_ALL_NAME,  // i.e. return all columns; 2 columns in this case
					null, null, null, null,
					WhiteListDB.KEY_NAME+ " ASC"  // order-by, "DESC" for descending
				);
		}
		
	}
	
	/** Creates a ContentValues hash for our data. Pass in to create/update. */
	public ContentValues createContentValues(String number,String name) {
		ContentValues values = new ContentValues();
		values.put(BlockListDB.KEY_NUMBER, number);
		values.put(KEY_NAME, name);
		return values;
	}
	
	public Cursor getOneContact(long id){
		return ourDatabase.query(DATABASE_TABLE, null, "_id=" + id, null, null, null, null);
	}
	
	public Cursor searchNumber(String number){
		return ourDatabase.rawQuery("SELECT * FROM " + DATABASE_TABLE + " WHERE " + KEY_NUMBER + " = '" + number + "'", null);//query(DATABASE_TABLE, KEY_INPUT_NUMBER, number, null, null, null, null);//("SELECT " + KEY_NUMBER + " FROM " + DATABASE_TABLE + " WHERE " + KEY_NUMBER + " = " + number, null);
	}
	
	public Cursor searchNumberSMS(String number){
		return ourDatabase.rawQuery("SELECT " + KEY_NUMBER + "FROM " + DATABASE_TABLE + " WHERE " + KEY_NUMBER + " = " + number , null);//query(DATABASE_TABLE, KEY_INPUT_NUMBER, number, null, null, null, null);//("SELECT " + KEY_NUMBER + " FROM " + DATABASE_TABLE + " WHERE " + KEY_NUMBER + " = " + number, null);
	}
	
	
	// Helper for database open, create, upgrade.
	// Here written as a private inner class to TodoDB.
	private static class DbHelper extends SQLiteOpenHelper{
		
		public DbHelper(Context context){
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) { 
			// TODO Auto-generated method stub
			db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" + KEY_ROWID + 
					" INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_NUMBER  + ", " + KEY_NAME + ");");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			if (oldVersion < 2) {
				final String ALTER_TBL = 
		                "ALTER TABLE " + DATABASE_TABLE +
		                " ADD COLUMN " + KEY_NAME + ";";
				db.execSQL(ALTER_TBL);
			}
			/*db.execSQL("DROP TABLE IF EXIST " + DATABASE_NAME);
			onCreate(db);*/
		}
	}
}
