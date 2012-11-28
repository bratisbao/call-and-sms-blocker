package com.atiffarrukh.callandsmsblocker.broadcastreceiver;

import com.atiffarrukh.callandsmsblocker.BlockListDB;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		final String TAG = "My TAG";
		//---get the SMS message passed in---
		Bundle bundle = intent.getExtras();        
		SmsMessage[] msgs = null;
		String str = "";      
		String contact = "";
		String gotNumber = "";
		String countryCode = "";

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

		boolean smsBlockCheck = prefs.getBoolean("smsBlock", true);
		countryCode = prefs.getString("countryCode", null);
		countryCode = "+" + countryCode;
		if(smsBlockCheck){
			Cursor cursor = null;
			BlockListDB ourDatabase = new BlockListDB(context);

			String searchNumber = ourDatabase.KEY_NUMBER;
			Log.d(TAG, "THIS IS SMS BROADCAST");

			if (bundle != null)
			{
				//---retrieve the SMS message received---
				Object[] pdus = (Object[]) bundle.get("pdus");
				msgs = new SmsMessage[pdus.length];            
				for (int i=0; i<msgs.length; i++){
					msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);                
					contact += msgs[i].getOriginatingAddress();                     
					//contact += " :";
					//str += msgs[i].getMessageBody().toString();
					//str += "\n";     
					//received = true

					
					Log.d(TAG, "Starting database search");
					ourDatabase.open();
					
					//contact = contact.substring(3); // Removing the +92 from 
					//contact = "0" + contact; 
					Log.d(TAG, "SMS sent by " + contact );
					contact = contact.substring(3);
					contact = "0" + contact;
					Log.d(TAG, "The CONTACT is " + gotNumber);
					cursor = ourDatabase.searchNumber(contact);
					if(cursor == null){
						Log.d(TAG, "EMPTY CURSOR");
					}
					if(cursor.moveToFirst()){ //Cursor.moveToNext() answers a boolean which is true if the move succeeded
						do{
						int colIndex = cursor.getColumnIndex(searchNumber);
						gotNumber = cursor.getString(colIndex); //
						Log.d(TAG, "The Incoming Number is " + gotNumber);
						}while(cursor.moveToNext());
					}


					ourDatabase.close();
					//final Context mContext = context;
					//final String temp = gotNumber;
					//gotNumber = gotNumber.substring(1);
					//gotNumber = countryCode + gotNumber;
					
					if (contact.equals(gotNumber)){ // check if the incoming number matches in the block list.
						Log.v(TAG, "Message broadcast Stopped.");
						abortBroadcast();
						/*	Thread timer = new Thread(){
                		public void run(){
                			try{
                				sleep(1000);
                		        Uri uriSMS = Uri.parse("content://sms/inbox");
                		        Cursor c= mContext.getContentResolver()
                		                                       .query(uriSMS, null, null, null, null);
                		        c.moveToFirst();
                		        if(c.getCount() > 0){
                		            int ThreadId = c.getInt(1);
                		            Log.d("Thread Id", ThreadId+" id - "+c.getInt(0));
                		            Log.d("contact number", c.getString(2));
                		            Log.d("column name", c.getColumnName(2));

                		            String fromAddress = null;
									mContext.getContentResolver().delete(Uri.
                		                   parse("content://sms/conversations/"+ThreadId), "address=?", 
                		                                                     new String[]{fromAddress});
                		           // Log.d("Message Thread Deleted", fromAddress);
                		        }
                		        c.close();

                			}catch(InterruptedException e){
                				Log.d(TAG, "test " + e);
                			}
                		}
                	};
                	timer.start();*/
						cursor.close();
					}
				}
			}
		}
	}
}