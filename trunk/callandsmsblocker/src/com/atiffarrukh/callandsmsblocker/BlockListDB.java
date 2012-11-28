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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This class manages a connection to the database, providing
 * convenience methods to create/update/delete, and centralizing the
 * constants used in the database.
 * 
 * It should be possible to adapt this class for common android/db applications
 * by changing the constants and a few methods.
 * 
 *
 */

public class BlockListDB {
	
	// define database name and version
	private static final String DATABASE_NAME = "BlockList";
	private static final String DATABASE_TABLE = "BlockListTable";
	private static final int DATABASE_VERSION = 1;
	
	// define columns
	// Field names -- use the KEY_XXX constants here and in
	// client code, so it's all consistent and checked at compile-time.
	public static final String KEY_ROWID = "_id";
	public static final int INDEX_ROWID = 0;
	public static final String KEY_NUMBER = "phone_number";
	
	public static final String[] KEYS_ALL =	{ BlockListDB.KEY_ROWID, BlockListDB.KEY_NUMBER};
	public static final String[] KEY_INPUT_NUMBER = {BlockListDB.KEY_NUMBER};
	
	// define database variables
	private DbHelper ourHelper;
	private final Context ourContext;
	private SQLiteDatabase ourDatabase;
	
	/** Construct DB for this activity context. */
	public BlockListDB(Context c){
		ourContext = c;
	}
	
	/** Opens up a connection to the database. Do this before any operations. */
	public void open() throws SQLException{
		ourHelper = new DbHelper(ourContext);
		ourDatabase = ourHelper.getWritableDatabase();
	}
	
	/** Closes the database connection. Operations are not valid after this. */
	public void close(){
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
				BlockListDB.KEY_ROWID + "=" + rowId, null) > 0;
	}

	/** Returns a cursor for all the rows. Caller should close or manage the cursor. */
	public Cursor queryAll() {
		return ourDatabase.query(DATABASE_TABLE,
			KEYS_ALL,  // i.e. return all columns; 2 columns in this case
			null, null, null, null,
			BlockListDB.KEY_NUMBER+ " ASC"  // order-by, "DESC" for descending
		);
	}
	
	/** Creates a ContentValues hash for our data. Pass in to create/update. */
	public ContentValues createContentValues(String number) {
		ContentValues values = new ContentValues();
		values.put(BlockListDB.KEY_NUMBER, number);
		return values;
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

		public DbHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}
		
		/** Creates the initial (empty) database. */	
		// called only once when database has to be created for the first time
		@Override
		public void onCreate(SQLiteDatabase db) { 
			// TODO Auto-generated method stub
			db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" + KEY_ROWID + 
					" INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_NUMBER + 
					" TEXT NOT NULL);");
		}
		
		
		/** Called at version upgrade time, in case we want to change/migrate
		 the database structure. Here we just do nothing. */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXIST " + DATABASE_NAME);
			onCreate(db);
		}
	}
}

