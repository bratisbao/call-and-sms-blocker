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

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.atiffarrukh.callandsmsblocker.R;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;


public class WhiteListActivity extends SherlockListActivity implements OnClickListener, ActionBar.OnNavigationListener,ActionBar.OnMenuVisibilityListener{
	
	Button addButton;
	Button enterButton;
	Button peopleButton;
	Button saveNumber;
	Button cancel;
	EditText enteredNumber;
	TextView rowText;
	
	String number;
	
	WhiteListDB enterValues = new WhiteListDB(this);
	SimpleCursorAdapter cursorAdapter;
	
	static final int choiceDialogID = 0;
	static final int numberEntryDialogID = 1;
	private static final int PICK_CONTACT = 0;
	Long rowID;
	
	
	//List<String> blackList = new ArrayList<String>();
	
	//ListAdapter adapter;
	
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        
		Context context = getSupportActionBar().getThemedContext();
	    ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(context, R.array.locations_whitelist, R.layout.sherlock_spinner_item);
	    list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

	    getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
	    getSupportActionBar().setListNavigationCallbacks(list, this);
        
        
        
        
        
        
        
        
        setContentView(R.layout.activity_whitelist);
        
        //ListView list = (ListView) findViewById(R.id.list);
        rowID = null;
        
        // Start up DB connection (closed in onDestroy).
        enterValues = new WhiteListDB(this);
        enterValues.Open();
        
        // Get the "all rows" cursor. startManagingCursor() is built in for the common case,
        // takes care of closing etc. the cursor.
        Cursor cursor = enterValues.queryAll();
        startManagingCursor(cursor);
        
		// Adapter: maps cursor keys, to R.id.XXX fields in the row layout.
        String[] from = new String[]{enterValues.KEY_NUMBER};
        int[] to = new int[]{R.id.rowtext};
		// Adapter: maps cursor keys, row fields in the row layout.
        SimpleCursorAdapter cursorAdapter =
        new SimpleCursorAdapter(this, R.layout.row, cursor, from, to);

     //   list.setAdapter(cursorAdapter);
		setListAdapter(cursorAdapter);
		registerForContextMenu(getListView());

       // enterValues.close();
        
        //adapter = new ListAdapter(null);
      //  list.setAdapter(adapter);
        
        addButton = (Button) findViewById(R.id.whiteListAdd);
        addButton.setOnClickListener(this);
        
       }
    
	// Placing this next to onCreate(), help to remember to mDB.close().
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		enterValues.Close();
	}
	
	
    // Removes the given rowId from the database, updates the UI.
    public void remove(long rowId) {
    	String TAG = "My Tag";
    	Log.d(TAG, "Row ID is " + rowId);
		enterValues.deleteRow(rowId);
		//cursorAdapter.notifyDataSetChanged();  // confusingly, this does not work
		//cursorAdapter.getCursor().requery();  // need this
        Cursor cursor = enterValues.queryAll();
        startManagingCursor(cursor);
        String[] from = new String[]{enterValues.KEY_NUMBER};
        int[] to = new int[]{R.id.rowtext};
		// Adapter: maps cursor keys, row fields in the row layout.
        SimpleCursorAdapter cursorAdapter =
        new SimpleCursorAdapter(this, R.layout.row, cursor, from, to);
		setListAdapter(cursorAdapter);
    }
	
    
/******************************************* DIALOG BOX ***************************/
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	Dialog dialog = null;
    	
    	switch (id) {
		case choiceDialogID:
			dialog = new Dialog(this);
			dialog.setContentView(R.layout.dialog);
	    	dialog.setTitle("Add caller");
	    	enterButton = (Button) dialog.findViewById(R.id.enterButton);
	    	peopleButton = (Button) dialog.findViewById(R.id.peopleButton);
	    	dialog.setCancelable(true);
	    	
	    	enterButton.setOnClickListener(this);
	    	peopleButton.setOnClickListener(this);
			break;
			
		case numberEntryDialogID:
			dialog = new Dialog(this);
			dialog.setTitle("Enter Number");
			dialog.setContentView(R.layout.enternumberdialog);
			saveNumber = (Button) dialog.findViewById(R.id.save);
			cancel = (Button) dialog.findViewById(R.id.canel);
			enteredNumber = (EditText) dialog.findViewById(R.id.number);
			
			cancel.setOnClickListener(this);
			saveNumber.setOnClickListener(this);
			break;
			
		default:
			break;
		}
    	
    	
    	return dialog;
    }
    
    /****************************   ONCLICKS   **********************************/

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		case R.id.whiteListAdd:
				Log.d("My TAG", "Add Button");
				showDialog(choiceDialogID);
			break;
		
		case R.id.enterButton:
			showDialog(numberEntryDialogID);
			dismissDialog(choiceDialogID);
		break;
		
		case R.id.canel:
			dismissDialog(numberEntryDialogID);
		break;
		
		case R.id.save:
			number = enteredNumber.getText().toString();
			//enterValues.open();
			if(number.equals(""))
				Toast.makeText(this, "Please Enter Number", Toast.LENGTH_SHORT).show();
			else{
				//if (rowID != null) {
					//enterValues.updateRow(rowID, enterValues.createContentValues(number));
				//}
				//else {
					rowID = enterValues.createRow(enterValues.createContentValues(number));
				//}
		        Cursor cursor = enterValues.queryAll();
		        startManagingCursor(cursor);
		        String[] from = new String[]{enterValues.KEY_NUMBER};
		        int[] to = new int[]{R.id.rowtext};
				// Adapter: maps cursor keys, row fields in the row layout.
		        SimpleCursorAdapter cursorAdapter =
		        new SimpleCursorAdapter(this, R.layout.row, cursor, from, to);

		     // list.setAdapter(cursorAdapter);
				setListAdapter(cursorAdapter);
		        
			//  blackList.add(number);
			//	adapter.notifyDataSetChanged();
			    enteredNumber.setText("");
			    //enterValues.close();
			    dismissDialog(numberEntryDialogID);
			}
			break;
		case R.id.peopleButton:
			Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
			startActivityForResult(intent, PICK_CONTACT);
		break;

		default:
			break;
		}
		
	}

/************************************ ONACTIVITYRESULT FOR CONTACTS *********************/	
	
	
	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);

		switch (reqCode) {
		case (PICK_CONTACT) :
			if (resultCode == Activity.RESULT_OK) {
				String contactId = ""; 
				String phoneNumber = "";
				Uri contactData = data.getData();
				Cursor cur = managedQuery(contactData, null, null, null, null);
				ContentResolver contect_resolver = getContentResolver();

				if (cur.moveToFirst()) {
					String id = cur.getString(cur.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
					String name = "";
					//String no = "";

					Cursor phoneCur = contect_resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[] { id }, null);

					if (phoneCur.moveToFirst()) {
						name = phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
						contactId = phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					}

					Log.e("Phone no & name :***: ", name + " : " + contactId);
					//txt.append(name + " : " + no + "\n");

					//id = null;
					//name = null;
					//contactId = null;
					//phoneCur = null;
				}
				// while (phones.moveToNext()) { 
				//String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
				//contactId = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				/* String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)); 
	        	   if (Boolean.parseBoolean(hasPhone)) { 
	        	                // You know have the number so now query it like this
	        		   Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
	        				   									null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId, 
	        				   									null, null); 
	        		   while (phones.moveToNext()) { 
	        			   phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));                 
	        	    } 
	        	    phones.close(); 
	        	   }*/ 

				Log.d("Contact Id", contactId + " " );
				//}
				rowID = enterValues.createRow(enterValues.createContentValues(contactId));
				//id = null;
				//name = null;
				contactId = null;
				//phoneCur = null;
				contect_resolver = null;
				cur = null;

				//	blackList.add(name);
				//  adapter.notifyDataSetChanged();
				dismissDialog(choiceDialogID);

				//if (rowID != null) {
				//enterValues.updateRow(rowID, enterValues.createContentValues(number));
				//}
				//else {
				//rowID = enterValues.createRow(enterValues.createContentValues(phoneNumber));
				//}
				Cursor cursor = enterValues.queryAll();
				startManagingCursor(cursor);
				String[] from = new String[]{enterValues.KEY_NUMBER};
				int[] to = new int[]{R.id.rowtext};
				// Adapter: maps cursor keys, row fields in the row layout.
				SimpleCursorAdapter cursorAdapter =
						new SimpleCursorAdapter(this, R.layout.row, cursor, from, to);

				// list.setAdapter(cursorAdapter);
				setListAdapter(cursorAdapter);

				//  blackList.add(number);
				//	adapter.notifyDataSetChanged();
				//enteredNumber.setText("");
				//enterValues.close();

			}
		break;
		}
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		this.finish();
	}
	
/**	*************************************************** CONTEXT MENU *************************************/
    // Create context menu for click-hold in list.
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	                                ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main, menu);
	}
    
    // Context menu item-select.
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		String TAG = "My Tag";
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
			case R.id.menu_delete:
				Log.d(TAG, "Row ID is " + info.id);
				remove(info.id);
				return true;				
			default:
				return super.onContextItemSelected(item);
		}
	}
	
	// Gets RowId
	@Override
	protected void onListItemClick(ListView l, View v, int position, long rowId) {
		super.onListItemClick(l, v, position, rowId);
		rowID = rowId;
	}
	
/** **************************************************** MENU *****************************************/
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater menuInflator = getMenuInflater();
		menuInflator.inflate(R.menu.whitelistmenu, menu);
		return true;
	}
	
	/*@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		
		switch (item.getItemId()) {
		case R.id.prefs:
			Intent intent = new Intent(this,Prefs.class);
			startActivity(intent);
			break;
			
		case R.id.blocklistact:
			Intent intentBlockList = new Intent(this,MainActivity.class);
			startActivity(intentBlockList);
			break;
			
		default:
			AdapterContextMenuInfo tst =  (AdapterContextMenuInfo) item.getMenuInfo();
			remove(tst.id);
			break;
		}
	return false;
	}*/
	

	/** **************************************************** NAVIGATION *****************************************/
	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// TODO Auto-generated method stub
		Log.d("SomeTag", "Get click event at position: " + itemPosition);
		switch (itemPosition) {
		case 1:
			Intent i = new Intent();
			i.setClass(getApplicationContext(), MainActivity.class);
			startActivity(i);
			//return true;
			break;
		case 2 :
			Intent intent = new Intent(this,Prefs.class);
			startActivity(intent);
			//return true;
			break;
		}
		
		return true;
	}

	@Override
	public void onMenuVisibilityChanged(boolean isVisible) {
		// TODO Auto-generated method stub
		
	}
}
