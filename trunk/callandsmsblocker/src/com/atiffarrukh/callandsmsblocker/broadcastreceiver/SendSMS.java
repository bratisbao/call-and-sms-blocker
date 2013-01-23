package com.atiffarrukh.callandsmsblocker.broadcastreceiver;

import java.util.ArrayList;
import com.actionbarsherlock.app.SherlockActivity;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class SendSMS extends Service{

	Context context;
	Intent i;
	String number;
	String defMessage;
	
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.d("SMS Service", "Service Started");
		
	
	}
	
	/*@Override
	public void onc() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		Log.d("SMS Service", "Service Started");
	
		defMessage = "I am busy right now and will call you latter";
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		boolean saveSMS = prefs.getBoolean("savesms", true);
		boolean addSign = prefs.getBoolean("addsign", true);
		String finalMessage =  prefs.getString("message", defMessage);
		
		try {
		
		defMessage = "I am busy right now and will call you latter.";
		
		finalMessage = prefs.getString("message", defMessage);
		if(finalMessage.equals(""))
			finalMessage = defMessage;
		} catch (RuntimeException e) {
			// TODO: handle exception
			finalMessage = defMessage;
		}
		
		if(addSign)
			finalMessage += "\nSent using Calls and SMS Blocker for android. Download from:market://details?id=com.atiffarrukh.callandsmsblocker";
		
		
		number = i.getStringExtra("number");
		
		sendSMS(finalMessage, number);
		
		if(saveSMS)
			storeMessage(number,finalMessage);
	}*/
	
		private void storeMessage(String phoneNumber2, String message) {
		// TODO Auto-generated method stub
		ContentValues values = new ContentValues();
		values.put("address", phoneNumber2);
		values.put("body",message);
		getContentResolver().insert(Uri.parse("content://sms/sent"), values);
	}


	private void sendSMS(String phoneNumber, String message) {
		// TODO Auto-generated method stub
		String SENT = "SMS_SENT";
		String DELIVERED = "SMS_DELIVERED";
		Log.d("SMS Service", "Seding SMS...");
		/* PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
	        new Intent(SENT), 0);

	    PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
	        new Intent(DELIVERED), 0);
		 */


		//---when the SMS has been sent---
		registerReceiver(new BroadcastReceiver(){
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode())
				{
				case Activity.RESULT_OK:
					Toast.makeText(getBaseContext(), "SMS sent", 
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					Toast.makeText(getBaseContext(), "Generic failure", 
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					Toast.makeText(getBaseContext(), "No service", 
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					Toast.makeText(getBaseContext(), "Null PDU", 
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					Toast.makeText(getBaseContext(), "Radio off", 
							Toast.LENGTH_SHORT).show();
					break;
				}
				//arg0.unregisterReceiver(arg0);
			}
		}, new IntentFilter(SENT));

		//---when the SMS has been delivered---
		registerReceiver(new BroadcastReceiver(){
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode())
				{
				case Activity.RESULT_OK:
					Toast.makeText(getBaseContext(), "SMS delivered", 
							Toast.LENGTH_SHORT).show();
					break;
				case Activity.RESULT_CANCELED:
					Toast.makeText(getBaseContext(), "SMS not delivered", 
							Toast.LENGTH_SHORT).show();
					break;                        
				}
				//arg0.unregisterReceiver(this);
			}
		}, new IntentFilter(DELIVERED));        

		SmsManager sms = SmsManager.getDefault();
		ArrayList<String> parts = sms.divideMessage(message);

		ArrayList<PendingIntent> sentPI = new ArrayList<PendingIntent>();
		for (int i=0; i < parts.size(); i++){

			PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(SENT), PendingIntent.FLAG_CANCEL_CURRENT);
			sentPI.add(pi);
		}

		ArrayList<PendingIntent> deliveredPI = new ArrayList<PendingIntent>();
		for (int i=0; i < parts.size(); i++){

			PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(SENT), PendingIntent.FLAG_CANCEL_CURRENT);
			deliveredPI.add(pi);
		}
		sms.sendMultipartTextMessage(phoneNumber, null, parts, sentPI, deliveredPI);  

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		i = intent;
		operation();
		return START_STICKY;
	}

	private void operation() {
		// TODO Auto-generated method stub
		context = getApplicationContext();
		defMessage = "I am busy right now and will call you latter";
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		boolean saveSMS = prefs.getBoolean("savesms", true);
		boolean addSign = prefs.getBoolean("addsign", true);
		String finalMessage;
		//i = getIntent();
		
		try {
		
		defMessage = "I am busy right now and will call you latter.";
		
		finalMessage = prefs.getString("message", defMessage);
		if(finalMessage.equals(""))
			finalMessage = defMessage;
		} catch (NullPointerException e) {
			// TODO: handle exception
			finalMessage = defMessage;
		}
		
		if(addSign)
			finalMessage += "\nSent using Calls and SMS Blocker for android.\nDownload from: https://play.google.com/store/apps/details?id=com.atiffarrukh.callandsmsblocker";
		
		
		number = i.getStringExtra("number");
		
		sendSMS(number, finalMessage);
		
		if(saveSMS)
			storeMessage(number,finalMessage);
	}

}
