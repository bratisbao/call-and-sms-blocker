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
import com.google.ads.AdRequest;
import com.google.ads.AdView;

import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

@SuppressWarnings("deprecation")
public class MainActivity extends SherlockListActivity implements
		OnClickListener, ActionBar.OnNavigationListener {

	Button addButton;
	Button enterButton;
	Button peopleButton;
	Button saveNumber;
	Button cancel;
	Button cancelEdit, saveEdit;
	EditText enteredNumber, enteredName, editName, editNumber;
	TextView rowText;
	String formattedPhoneNumber;
	String number;
	String name;
	Long id;
	boolean numberBool;
	SharedPreferences prefs;
	Dialog dialog,d;
	
	BlockListDB enterValues;
	SimpleCursorAdapter cursorAdapter;

	static final int choiceDialogID = 0;
	static final int numberEntryDialogID = 1;
	static final int editNumberDialogID = 2;
	private static final int PICK_CONTACT = 0;
	Long rowID;
	public static final String GAME_PREFERENCES = "GamePrefs";		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Google Ads
				AdView adView = (AdView)this.findViewById(R.id.adView);
			    adView.loadAd(new AdRequest());//
		
		 SharedPreferences settings = getSharedPreferences(GAME_PREFERENCES, MODE_PRIVATE);
	     
		if (settings.getBoolean("firstTime", true)) {//Warning note
			Log.d("in", "Dialog");
			d = new Dialog(this);
			d.setTitle("Please Note");
			d.setContentView(R.layout.warning_dialog);
			Button b = (Button) d.findViewById(R.id.ack);
			b.setOnClickListener(this);
			d.show();
		}
		
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		numberBool = prefs.getBoolean("number", true); // if view of list is by number or name

		Context context = getSupportActionBar().getThemedContext();
		ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(
				context, R.array.locations_blocklist,
				R.layout.sherlock_spinner_item);
		list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getSupportActionBar().setListNavigationCallbacks(list, this);

		rowID = null;

		// Start up DB connection (closed in onDestroy).
		enterValues = new BlockListDB(this);
		enterValues.open();

		// Get the "all rows" cursor. startManagingCursor() is built in for the
		// common case,
		// takes care of closing etc. the cursor.
		Cursor cursor = enterValues.queryAll();
		startManagingCursor(cursor);

		// Adapter: maps cursor keys, to R.id.XXX fields in the row layout.
		if (numberBool) {
			String[] from = new String[] { enterValues.KEY_NUMBER };
			int[] to = new int[] { R.id.rowtext };
			// Adapter: maps cursor keys, row fields in the row layout.
			SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this,
					R.layout.row, cursor, from, to);

			setListAdapter(cursorAdapter);
			registerForContextMenu(getListView());
		} else {
			String[] from = new String[] { enterValues.KEY_NAME};
			int[] to = new int[] { R.id.rowtext };
			// Adapter: maps cursor keys, row fields in the row layout.
			SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this,
					R.layout.row, cursor, from, to);
	
			setListAdapter(cursorAdapter);
			registerForContextMenu(getListView());
		}
		addButton = (Button) findViewById(R.id.addButton);
		addButton.setOnClickListener(this);

	}

	// Placing this next to onCreate(), help to remember to mDB.close().
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		enterValues.close();
	}

	// Removes the given rowId from the database, updates the UI.
	public void remove(long rowId) {
		String TAG = "My Tag";
		Log.d(TAG, "Row ID is " + rowId);
		enterValues.deleteRow(rowId);
		// cursorAdapter.notifyDataSetChanged(); // confusingly, this does not
		// work
		// cursorAdapter.getCursor().requery(); // need this
		Cursor cursor = enterValues.queryAll();
		startManagingCursor(cursor);
		if (numberBool) {
			String[] from = new String[] { enterValues.KEY_NUMBER };
			int[] to = new int[] { R.id.rowtext };
			// Adapter: maps cursor keys, row fields in the row layout.
			SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this,
					R.layout.row, cursor, from, to);

			setListAdapter(cursorAdapter);
			
		} else {
			String[] from = new String[] { enterValues.KEY_NAME};
			int[] to = new int[] { R.id.rowtext };
			// Adapter: maps cursor keys, row fields in the row layout.
			SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this,
					R.layout.row, cursor, from, to);
	
			setListAdapter(cursorAdapter);
			
		}
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
			dialog.setTitle("Enter Contact");
			dialog.setContentView(R.layout.enternumberdialog);
			saveNumber = (Button) dialog.findViewById(R.id.save);
			cancel = (Button) dialog.findViewById(R.id.canel);
			enteredNumber = (EditText) dialog.findViewById(R.id.number);
			enteredName = (EditText) dialog.findViewById(R.id.name);
			cancel.setOnClickListener(this);
			saveNumber.setOnClickListener(this);
			break;

		case editNumberDialogID:
			dialog = new Dialog(this);
			dialog.setTitle("Edit Contact");
			dialog.setContentView(R.layout.edit_dialog);
			editName = (EditText) dialog.findViewById(R.id.nameEditEt);
			editNumber = (EditText) dialog.findViewById(R.id.numberEditEt);
			editName.setText("");
			editNumber.setText("");
			cancelEdit = (Button) dialog.findViewById(R.id.cancelEditBt);
			cancelEdit.setOnClickListener(this);
			saveEdit = (Button) dialog.findViewById(R.id.saveEditBt);
			saveEdit.setOnClickListener(this);
			Cursor c = enterValues.getOneContact(this.id);
			Log.d("edit Dialog", "id " + this.id );
			if (c.moveToFirst()) {
				editName.setText("");
				if(!numberBool){
					int nameIndex = c.getColumnIndex("name");
					editName.setText(c.getString(nameIndex));
				}				
				int numberIndex = c.getColumnIndex("phone_number");
				editNumber.setText(c.getString(numberIndex));
			}

			break;

		default:
			break;
		}

		return dialog;
	}

	/**************************** ONCLICKS **********************************/

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.addButton:
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
			name = enteredName.getText().toString();

			if (number.equals("") || name.equals(""))
				Toast.makeText(this, "Please fill all fields",
						Toast.LENGTH_SHORT).show();
			else {

				rowID = enterValues.createRow(enterValues.createContentValues(
						number, name));

				Cursor cursor = enterValues.queryAll();
				startManagingCursor(cursor);
				if (numberBool) {
					String[] from = new String[] { enterValues.KEY_NUMBER };
					int[] to = new int[] { R.id.rowtext };
					// Adapter: maps cursor keys, row fields in the row layout.
					SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this,
							R.layout.row, cursor, from, to);

					setListAdapter(cursorAdapter);
					
				} else {
					String[] from = new String[] { enterValues.KEY_NAME};
					int[] to = new int[] { R.id.rowtext };
					// Adapter: maps cursor keys, row fields in the row layout.
					SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this,
							R.layout.row, cursor, from, to);
			
					setListAdapter(cursorAdapter);
					
				}

				enteredNumber.setText("");
				enteredName.setText("");

				dismissDialog(numberEntryDialogID);
			}
			break;
		case R.id.peopleButton:
			Intent intent = new Intent(Intent.ACTION_PICK,
					ContactsContract.Contacts.CONTENT_URI);
			startActivityForResult(intent, PICK_CONTACT);
			break;
		case R.id.cancelEditBt:
			
			dialog.dismiss();
			break;

		case R.id.saveEditBt:
			number = editNumber.getText().toString();
			name = editName.getText().toString();
			// enterValues.open();
			if (number.equals("") || name.equals(""))
				Toast.makeText(this, "Please fill all fields",
						Toast.LENGTH_SHORT).show();
			else {
				
				if(numberBool){
					
					enterValues.updateRow(id,enterValues.createContentValues(number, name));
					//enterValues.deleteRow(id);
				}
				else
					enterValues.updateRow(id,enterValues.createContentValues(number, name));

				Cursor cursor = enterValues.queryAll();
				startManagingCursor(cursor);

				if (numberBool) {
					String[] from = new String[] { enterValues.KEY_NUMBER };
					int[] to = new int[] { R.id.rowtext };
					// Adapter: maps cursor keys, row fields in the row layout.
					SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this,
							R.layout.row, cursor, from, to);

					setListAdapter(cursorAdapter);
					
				} else {
					String[] from = new String[] { enterValues.KEY_NAME};
					int[] to = new int[] { R.id.rowtext };
					// Adapter: maps cursor keys, row fields in the row layout.
					SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this,
							R.layout.row, cursor, from, to);
			
					setListAdapter(cursorAdapter);
					
				}
				dialog.dismiss();
			}
			
			break;
			
		case R.id.ack:
			d.dismiss();
			SharedPreferences settings = getSharedPreferences(GAME_PREFERENCES, MODE_PRIVATE);
		    SharedPreferences.Editor prefEditor = settings.edit();
		    prefEditor.putBoolean("firstTime", false);
		    prefEditor.commit();
			break;

		default:
			break;
		}

	}
	
	private void editDialog(){
		
		dialog = new Dialog(this);
		dialog.setTitle("Edit Contact");
		dialog.setContentView(R.layout.edit_dialog);
		editName = (EditText) dialog.findViewById(R.id.nameEditEt);
		editNumber = (EditText) dialog.findViewById(R.id.numberEditEt);
		editName.setText("");
		editNumber.setText("");
		cancelEdit = (Button) dialog.findViewById(R.id.cancelEditBt);
		cancelEdit.setOnClickListener(this);
		saveEdit = (Button) dialog.findViewById(R.id.saveEditBt);
		saveEdit.setOnClickListener(this);
		Cursor c = enterValues.getOneContact(this.id);
		Log.d("edit Dialog", "id " + this.id );
		if (c.moveToFirst()) {
			editName.setText("");
			if(!numberBool){
				int nameIndex = c.getColumnIndex("name");
				editName.setText(c.getString(nameIndex));
			}				
			int numberIndex = c.getColumnIndex("phone_number");
			editNumber.setText(c.getString(numberIndex));
		}
		dialog.show();
	}

	/***************************** ******* ONACTIVITYRESULT FOR CONTACTS *********************/

	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);

		switch (reqCode) {
		case (PICK_CONTACT):
			if (resultCode == Activity.RESULT_OK) {
				String contactId = "";
				String name = "";
				Uri contactData = data.getData();
				Cursor cur = managedQuery(contactData, null, null, null, null);
				ContentResolver contect_resolver = getContentResolver();

				if (cur.moveToFirst()) {
					String id = cur
							.getString(cur
									.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

					// String no = "";

					Cursor phoneCur = contect_resolver.query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = ?", new String[] { id }, null);

					if (phoneCur.moveToFirst()) {
						name = phoneCur
								.getString(phoneCur
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
						contactId = phoneCur
								.getString(phoneCur
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					}

					Log.e("Phone no & name :***: ", name + " : " + contactId);

				}

				Log.d("Contact Id", contactId + " ");

				rowID = enterValues.createRow(enterValues.createContentValues(
						contactId, name));

				contactId = null;

				contect_resolver = null;
				cur = null;

				dismissDialog(choiceDialogID);

				Cursor cursor = enterValues.queryAll();
				startManagingCursor(cursor);
				if (numberBool) {
					String[] from = new String[] { enterValues.KEY_NUMBER };
					int[] to = new int[] { R.id.rowtext };
					// Adapter: maps cursor keys, row fields in the row layout.
					SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this,
							R.layout.row, cursor, from, to);

					setListAdapter(cursorAdapter);
					registerForContextMenu(getListView());
				} else {
					String[] from = new String[] { enterValues.KEY_NAME};
					int[] to = new int[] { R.id.rowtext };
					// Adapter: maps cursor keys, row fields in the row layout.
					SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this,
							R.layout.row, cursor, from, to);
			
					setListAdapter(cursorAdapter);
					registerForContextMenu(getListView());
				}

			}
			break;
		}
	}

	/** *************************************************** CONTEXT MENU *************************************/
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
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.menu_delete:
			Log.d(TAG, "Row ID is " + info.id);
			remove(info.id);
			return true;
		case R.id.menu_edit:
			id = info.id;
			Log.d("id", "" + id);
			//showDialog(editNumberDialogID);
			editDialog();
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
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { // TODO
	 * Auto-generated method stub MenuInflater menuInflator = getMenuInflater();
	 * menuInflator.inflate(R.menu.prefsmenu, menu); return true; }
	 * 
	 * @Override public boolean onMenuItemSelected(int featureId, MenuItem item)
	 * { // TODO Auto-generated method stub
	 * 
	 * switch (item.getItemId()) { case R.id.prefs: Intent intent = new
	 * Intent(this,Prefs.class); startActivity(intent); break;
	 * 
	 * case R.id.whitelistact: Intent intentWhiteList = new
	 * Intent(MainActivity.this, WhiteListActivity.class);
	 * startActivity(intentWhiteList); break;
	 * 
	 * default: AdapterContextMenuInfo tst = (AdapterContextMenuInfo)
	 * item.getMenuInfo(); remove(tst.id); break; } return false; }
	 */

	/**************************** NAVIGATION *****************************************************/
	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// TODO Auto-generated method stub
		Log.d("SomeTag", "Get click event at position: " + itemPosition);
		switch (itemPosition) {

		case 1:
			Intent j = new Intent(this, WhiteListActivity.class);

			startActivity(j);

			break;

		case 2:
			Intent intent = new Intent(this, Prefs.class);
			startActivity(intent);
			break;
		}

		return true;
	}
}
