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
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.atiffarrukh.callandsmsblocker.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class Prefs extends SherlockPreferenceActivity implements ActionBar.OnNavigationListener{
	Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		int theme = R.style.Theme_Sherlock_ForceOverflow;
		setTheme(theme);
		super.onCreate(savedInstanceState);
	
		addPreferencesFromResource(R.xml.prefs);// Add preferences function
		
		Context context = getSupportActionBar().getThemedContext();
	    ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(context, R.array.locations_prefs, R.layout.sherlock_spinner_item);
	    list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

	    getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
	    getSupportActionBar().setListNavigationCallbacks(list, this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		//menu.add("About").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add("").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add("How-To").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add("About").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add("Rate it").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add("Contact").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		
		return true;
	
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		if(item.getTitle().equals("How-To")){
				Intent i = new Intent(this,HowToDialog.class );
				startActivity(i);
			}
		else if(item.getTitle().equals("About")){
			Intent i = new Intent(this,About.class );
			startActivity(i);	
			}
		else if(item.getTitle().equals("Rate it")){
			Uri uri = Uri.parse("market://details?id=com.atiffarrukh.callandsmsblocker");
			Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
			try {
			  startActivity(goToMarket);
			} catch (ActivityNotFoundException e) {
			  Toast.makeText(context, "Couldn't launch the market", Toast.LENGTH_LONG).show();
			}
			}
		else if(item.getTitle().equals("Contact")){
			Intent i = new Intent(this,ContactDialog.class );
			startActivity(i);	
			}

		
		return false;
		
	}

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
			Intent intent = new Intent(this,WhiteListActivity.class);
			startActivity(intent);
			//return true;
			break;
		}
		
		return true;
	}

}
