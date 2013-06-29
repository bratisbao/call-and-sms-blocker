package com.atiffarrukh.callandsmsblocker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseConnector {

	private SQLiteDatabase database;
	private DatabaseOpenHelper dbOpenHelper;
	
	public DatabaseConnector(Context context, String DB_NAME){
		dbOpenHelper = new DatabaseOpenHelper(context, DB_NAME,null,1);
	}
	
	public void open() throws SQLException{
		database = dbOpenHelper.getWritableDatabase();
	}
	
	public void close(){
		if(database != null){
			database.close();
		}
	}
	
	public void insertContact(String name, String number){
		ContentValues conValues = new ContentValues();
		conValues.put("name", name);
		conValues.put("number", number);
		open();
		database.insert("contact", null, conValues);
		close();
	}
	
	public void updateContact(long id, String name, String number){
		ContentValues editContact =  new ContentValues();
		editContact.put("name", name);
		editContact.put("number", number);
		open();
		database.update("contact", editContact, "_id=" + id, null);
		close();
	}
	
	public Cursor getAllValues() {
		return database.query("contact", new String[] {"_id","name"}, null, null, null
				, null, null);
		
	}
	
	public Cursor getOneContact(long id){
		return database.query("contact", null, "_id=" + id, null, null, null, null);
	}
	
	public void deleteContact(long id){
		open();
		database.delete("contact", "_id=" + id, null);
		close();
	}
	
	public Cursor searchNumber(String number){
		return database.rawQuery("SELECT * FROM " + "contact" + " WHERE " + "number" + " = '" + number + "'", null);//query(DATABASE_TABLE, KEY_INPUT_NUMBER, number, null, null, null, null);//("SELECT " + KEY_NUMBER + " FROM " + DATABASE_TABLE + " WHERE " + KEY_NUMBER + " = " + number, null);

	}
}
