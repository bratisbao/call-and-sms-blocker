package com.atiffarrukh.callandsmsblocker.broadcastreceiver;

import java.lang.reflect.Method;
import com.android.internal.telephony.ITelephony;
import com.atiffarrukh.callandsmsblocker.BlockListDB;
import com.atiffarrukh.callandsmsblocker.WhiteListDB;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.PhoneLookup;
import android.sax.StartElementListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;


public class TelephoneBroadcastReceiver extends BroadcastReceiver {
	String TAG = "My Tag"; //Used for TAG in LOG
	String incomingNumber = " ??? "; // Used for getting incoming number
	 // Used to get the incoming Number
	//String incno1 = "15555215556";
	int k = 0;
	String inNumber;
	boolean isMatch; 
	Cursor cursor;
	Context contextA;
	Intent i;
	

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		//contextA = context;
		contextA = context;
		Log.d(TAG, "INF: Broadcast received.");

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

		boolean activateServices = prefs.getBoolean("activate", true); // service activation
		boolean activateBlackList = prefs.getBoolean("callsBlock", true); //check for blocklist
		boolean activateWhiteList = prefs.getBoolean("callsAllow", false); //check for whitelist
		boolean blockAll = prefs.getBoolean("blockall", false);
		boolean sendSMS = prefs.getBoolean("autosms", true);
		boolean privateNumbers = prefs.getBoolean("private", false);
		
		

		if(activateServices){
			//if(callBlockCheck){


				try{

					TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);// Get Telephone Manager
					Log.v(TAG, "Get getTeleService...");	
					Class c = Class.forName(tManager.getClass().getName());
					Method m = c.getDeclaredMethod("getITelephony");
					m.setAccessible(true);
					com.android.internal.telephony.ITelephony telephonyService = (ITelephony) m.invoke(tManager); //Access the AIDL file
					Bundle bundle = intent.getExtras();

					incomingNumber = bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
					Log.v(TAG,incomingNumber );
					//Log.v(TAG,incno1 );
					/**************************************** BLOCK PRIVATE NUMBERS ************************************/
					if (privateNumbers) {
						if (Integer.parseInt(incomingNumber) < 0) {
							telephonyService = (ITelephony) m.invoke(tManager);
							telephonyService.endCall();
						}
					}
					
					
					/**************************************** BLOCK ALL NUMBERS ****************************************/
					if(blockAll){
						if(!contactExists(context,incomingNumber)){
							telephonyService = (ITelephony) m.invoke(tManager);
							telephonyService.endCall();
						}
					}

					/***************************************** BLACKLIST CHECK  ****************************************/
					if(activateBlackList && !activateWhiteList){
						Log.d(TAG, "Opening Blocklist Database...");
						BlockListDB ourDatabase = new BlockListDB(context);
						ourDatabase.open();

						String searchNumber = ourDatabase.KEY_NUMBER;
						cursor =  ourDatabase.searchNumber(incomingNumber); //Search for the Incoming number in the database
						if(cursor == null){
							Log.e(TAG, "Cursor is null");
						}
						if(cursor.moveToFirst()){ //Cursor.moveToNext() answers a boolean which is true if the move succeeded
							do{
								int colIndex = cursor.getColumnIndex(searchNumber);
								inNumber = cursor.getString(colIndex); //
								Log.d(TAG, "The Incoming Number is " + inNumber);
								//++k;
							}while(cursor.moveToNext());
						}
						ourDatabase.close();
						//for (int i = 0; i < inNumber.length; i++){
						//if(inNumber[i].equals(incomingNumber)){
						//isMatch = true;
						//}
						//else {
						//isMatch = false;
						//}
						//}
						Log.d(TAG, "The Incoming Number is " + inNumber);
						if ( inNumber.equals(incomingNumber)) {
							telephonyService = (ITelephony) m.invoke(tManager);
							//telephonyService.silenceRinger();
							telephonyService.endCall();
							//Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
							//i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_HEADSETHOOK));
							//context.sendOrderedBroadcast(i, "android.permission.CALL_PRIVILEGED");
							//abortBroadcast();
							Log.v(TAG,"BYE BYE BYE" );
						}
						else{
							//telephonyService.answerRingingCall();
							Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
							i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_HEADSETHOOK));
							context.sendOrderedBroadcast(i, "android.permission.CALL_PRIVILEGED");
							Log.v(TAG,"HELLO HELLO HELLO" );
						}
					}
					
					/***************************************** WHITELIST CHECK  ****************************************/
					if(activateWhiteList){
						Log.d(TAG, "Opening Whitelist Database...");
						WhiteListDB ourDatabase = new WhiteListDB(context);
						ourDatabase.Open();

						String searchNumber = ourDatabase.KEY_NUMBER;
						cursor =  ourDatabase.searchNumber(incomingNumber); //Search for the Incoming number in the database
						if(cursor == null){
							Log.e(TAG, "Cursor is null");
						}
						if(cursor.moveToFirst()){ //Cursor.moveToNext() answers a boolean which is true if the move succeeded
							do{
								int colIndex = cursor.getColumnIndex(searchNumber);
								inNumber = cursor.getString(colIndex); //
								Log.d(TAG, "The Incoming Number is " + inNumber);
								//++k;
							}while(cursor.moveToNext());
						}
						ourDatabase.Close();
						//for (int i = 0; i < inNumber.length; i++){
						//if(inNumber[i].equals(incomingNumber)){
						//isMatch = true;
						//}
						//else {
						//isMatch = false;
						//}
						//}
						Log.d(TAG, "The Incoming Number is " + inNumber);
						
						if ( inNumber == null) {
							telephonyService = (ITelephony) m.invoke(tManager);
							//telephonyService.silenceRinger();
							telephonyService.endCall();
							//Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
							//i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_HEADSETHOOK));
							//context.sendOrderedBroadcast(i, "android.permission.CALL_PRIVILEGED");
							//abortBroadcast();
							Log.v(TAG,"BYE BYE BYE" );
							getAbortBroadcast();
							if(sendSMS){
								Log.v(TAG,"sendSMS Check" );
								sendSMSMehtod(incomingNumber);
								//context.stopService(i);
								
						}
						else{
							//telephonyService.answerRingingCall();
							//telephonyService = (ITelephony) m.invoke(tManager);
							//telephonyService.endCall();
							//Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
							//i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_HEADSETHOOK));
							//context.sendOrderedBroadcast(i, "android.permission.CALL_PRIVILEGED");
							Log.v(TAG,"HELLO HELLO HELLO" );
							
								
							}
							
						}
					}
					
					
					/***************************************** BOTH LIST CHECK  ****************************************/
					if(activateBlackList && activateWhiteList){
						Log.d(TAG, "Opening Whitelist Database...");
						WhiteListDB ourDatabase = new WhiteListDB(context);
						ourDatabase.Open();

						String searchNumber = ourDatabase.KEY_NUMBER;
						cursor =  ourDatabase.searchNumber(incomingNumber); //Search for the Incoming number in the database
						if(cursor == null){
							Log.e(TAG, "Cursor is null");
						}
						if(cursor.moveToFirst()){ //Cursor.moveToNext() answers a boolean which is true if the move succeeded
							do{
								int colIndex = cursor.getColumnIndex(searchNumber);
								inNumber = cursor.getString(colIndex); //
								Log.d(TAG, "The Incoming Number is " + inNumber);
								//++k;
							}while(cursor.moveToNext());
						}
						ourDatabase.Close();
						//for (int i = 0; i < inNumber.length; i++){
						//if(inNumber[i].equals(incomingNumber)){
						//isMatch = true;
						//}
						//else {
						//isMatch = false;
						//}
						//}
						Log.d(TAG, "The Incoming Number is " + inNumber);
						
						if ( inNumber == null) {
							telephonyService = (ITelephony) m.invoke(tManager);
							//telephonyService.silenceRinger();
							telephonyService.endCall();
							//Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
							//i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_HEADSETHOOK));
							//context.sendOrderedBroadcast(i, "android.permission.CALL_PRIVILEGED");
							//abortBroadcast();
							Log.v(TAG,"HELLO HELLO HELLO" );
						}
						else{
							//telephonyService.answerRingingCall();
							//telephonyService = (ITelephony) m.invoke(tManager);
							//telephonyService.endCall();
							//Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
							//i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_HEADSETHOOK));
							//context.sendOrderedBroadcast(i, "android.permission.CALL_PRIVILEGED");
							Log.v(TAG,"BYE BYE BYE" );
						}
					}
					
				}catch(Exception e){
					e.printStackTrace();
					Log.e(TAG,
							"FATAL ERROR: could not connect to telephony subsystem");
					Log.e(TAG, "Exception object: " + e);
				}
			//}
		}
		
	}


	private void sendSMSMehtod(String incomingNumber2) {
		// TODO Auto-generated method stub
		i =  new Intent(contextA, SendSMS.class);
		i.putExtra("number", incomingNumber);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Log.d(TAG, "Starting Service");
		contextA.startService(i);
		//contextA.stopService(i);
		
	}

	

	public boolean contactExists(Context context, String number) {
		/// number is the phone number
		Uri lookupUri = Uri.withAppendedPath(
				PhoneLookup.CONTENT_FILTER_URI, 
				Uri.encode(number));
		String[] mPhoneNumberProjection = { PhoneLookup._ID, PhoneLookup.NUMBER, PhoneLookup.DISPLAY_NAME };
		Cursor cur = context.getContentResolver().query(lookupUri,mPhoneNumberProjection, null, null, null);
		try {
			if (cur.moveToFirst()) {
				return true;
			}
		} finally {
			if (cur != null)
				cur.close();
		}
		return false;
	}
}
/**
		if(callBlockCheck){
			Log.d(TAG, "Opening Database...");
			BlockListDB ourDatabase = new BlockListDB(context);
			ourDatabase.open();

			String searchNumber = ourDatabase.KEY_NUMBER;

			try{

				TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);// Get Telephone Manager
				Log.v(TAG, "Get getTeleService...");	
				Class c = Class.forName(tManager.getClass().getName());
				Method m = c.getDeclaredMethod("getITelephony");
				m.setAccessible(true);
				com.android.internal.telephony.ITelephony telephonyService = (ITelephony) m.invoke(tManager); //Access the AIDL file
				Bundle bundle = intent.getExtras();

				incomingNumber = bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
				Log.v(TAG,incomingNumber );
				//Log.v(TAG,incno1 );

				cursor =  ourDatabase.searchNumber(incomingNumber); //Search for the Incoming number in the database

				if(cursor.moveToNext()){ //Cursor.moveToNext() answers a boolean which is true if the move succeeded
					int colIndex = cursor.getColumnIndex(searchNumber);
					inNumber = cursor.getString(colIndex); //
					Log.d(TAG, "The Incoming Number is " + inNumber);
				}
				ourDatabase.close();

				if ( incomingNumber.equals(inNumber) ) {
					telephonyService = (ITelephony) m.invoke(tManager);
					telephonyService.silenceRinger();
					telephonyService.endCall();
					Log.v(TAG,"BYE BYE BYE" );
				}
				else{
					telephonyService.answerRingingCall();
					Log.v(TAG,"HELLO HELLO HELLO" );
				}			
			}catch(Exception e){
				e.printStackTrace();
				Log.e(TAG,
						"FATAL ERROR: could not connect to telephony subsystem");
				Log.e(TAG, "Exception object: " + e);
			}
		}
	}*/

