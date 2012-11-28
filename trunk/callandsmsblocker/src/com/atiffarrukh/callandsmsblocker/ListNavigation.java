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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;


public class ListNavigation extends SherlockActivity implements ActionBar.OnNavigationListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		//setContentView(R.layout.list_navigation);
		Context context = getSupportActionBar().getThemedContext();
	    ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(context, R.array.locations_blocklist, R.layout.sherlock_spinner_item);
	    list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

	    getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
	    getSupportActionBar().setListNavigationCallbacks(list, this);
	
	
	}
	
	
	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// TODO Auto-generated method stub
		Log.d("SomeTag", "Get click event at position: " + itemPosition);
		switch (itemPosition) {
		case 0:
			Intent i = new Intent();
			i.setClass(getApplicationContext(), MainActivity.class);
			startActivity(i);
			return true;
			//break;
		case 1:
			Intent j = new Intent();
			j.setClass(getApplicationContext(), WhiteListActivity.class);
			startActivity(j);
			return true;
			//break;
		}
		
		return true;
	}

}
