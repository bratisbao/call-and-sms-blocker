package com.atiffarrukh.callandsmsblocker;

import android.os.Bundle;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

public class About extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.prefs_dialog);
        ((TextView)findViewById(R.id.text)).setText(R.string.about_text);
	
	}
}
