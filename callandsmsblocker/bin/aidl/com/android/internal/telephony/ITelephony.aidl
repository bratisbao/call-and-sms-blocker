package com.android.internal.telephony;

/**
 * Interface used to interact with the phone.  Mostly this is used by the
 * TelephonyManager class.  A few places are still using this directly.
 * Please clean them up if possible and use TelephonyManager insteadl.
 *
 *
 */
 
interface ITelephony {

    /**
     * End call or go to the Home screen
     *
     * @return whether it hung up
     */
	
	boolean endCall();
	
	/**
     * Answer the currently-ringing call.
     *
     * If there's already a current active call, that call will be
     * automatically put on hold.  If both lines are currently in use, the
     * current active call will be ended.
     */
	
	void answerRingingCall();
	
	 /**
     * Silence the ringer if an incoming call is currently ringing.
     * (If vibrating, stop the vibrator also.)
     *
     * It's safe to call this if the ringer has already been silenced, or
     * even if there's no incoming call.  (If so, this method will do nothing.)
     */
	
	void silenceRinger();

}
