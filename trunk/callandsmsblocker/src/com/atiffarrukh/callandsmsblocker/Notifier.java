package com.atiffarrukh.callandsmsblocker;

import android.app.NotificationManager;
import android.content.Context;


public class Notifier {
	private static int NOTIFIER_ID = 1;
	//private SharedPreferences prefs;
	private NotificationManager mNotificationManager;
	
	public Notifier(Context context){
		
		//prefs = PreferenceManager.getDefaultSharedPreferences(context);
		mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		disableNotification();
	}
	
	private void disableNotification(){
		mNotificationManager.cancel(NOTIFIER_ID);
	}
}
