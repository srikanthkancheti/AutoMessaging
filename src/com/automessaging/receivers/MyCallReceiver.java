package com.automessaging.receivers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.automessaging.database.ProfilesDatabaseHelper;

public class MyCallReceiver extends BroadcastReceiver {

	public class NumberModel {
		
		 public String phoneNumber;
		 
		public String getPhoneNumber() {
			return phoneNumber;
		}

		public void setPhoneNumber(String title) {
			this.phoneNumber = title;
		}

	}

	private Date curDate, StartDate, StopDate;
	private String incomingNumber, formatedCurrentTime, profileName, profileReply, groupReplyMsg;
	public Context mContext;
	ProfilesDatabaseHelper DbHelper = null;
	private ArrayList<NumberModel> numbers_list = null;

	@Override
	public void onReceive(Context mContext, Intent intent) {
		// TODO Auto-generated method stub
		SharedPreferences prefs = mContext.getSharedPreferences("MyPref",Context.MODE_PRIVATE);
		
		DbHelper = new ProfilesDatabaseHelper(mContext);
		
		String startDate = prefs.getString("start_date", null);
		String stopDate = prefs.getString("stop_date", null);
		profileName = prefs.getString("profile_name", null);
		
		Calendar c = Calendar.getInstance();
		SimpleDateFormat dateFormater = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
		SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
		formatedCurrentTime = format1.format(c.getTime());
		try {
			StartDate = dateFormater.parse(startDate);
			StopDate = dateFormater.parse(stopDate);
			curDate = dateFormater.parse(formatedCurrentTime);
			Log.v(this.getClass().getName(),"check..in myCallReceiver Start : " +StartDate);
			Log.v(this.getClass().getName(),"check..in myCallReceiver Stop : " +StopDate);
			Log.v(this.getClass().getName(),"check..in myCallReceiver Current : " +curDate);
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("exception in mycallreceiver date formates :"+e.getMessage());
		}
		
			 if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
						TelephonyManager.EXTRA_STATE_RINGING)) {
					// This code will execute when the phone has an incoming call
					 
				 if (StartDate != null && StopDate != null && curDate.after(StartDate) && curDate.before(StopDate)) {
					// get the phone number
						 incomingNumber = intent
							.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
						groupReplyMsg = DbHelper.getGroupRelpyMessage(incomingNumber);
						profileReply = DbHelper.getProfileReplyMessage(profileName);
						
						if(groupReplyMsg != null){
							
							Log.v(this.getClass().getName(),"check..receiver group reply Message : " + groupReplyMsg);
							
							SmsManager smsManager = SmsManager.getDefault();
							smsManager.sendTextMessage(incomingNumber, null,
									groupReplyMsg, null, null);
						} else if(incomingNumber != null && profileReply != null){
							try{
								SmsManager smsManager = SmsManager.getDefault();
								smsManager.sendTextMessage(incomingNumber, null, profileReply, null, null);
								Log.v(this.getClass().getName(),"check..receiver incoming Number : " + incomingNumber);
								Log.v(this.getClass().getName(),"check..receiver reply Message : " + profileReply);
								
								}catch(Exception e){
									e.printStackTrace();
								}
						}

				 }else{
					 Log.v(this.getClass().getName(),"check..myCallReceiver condition fails : ");
				 }
					

				}else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
						TelephonyManager.EXTRA_STATE_IDLE)
						|| intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
								TelephonyManager.EXTRA_STATE_OFFHOOK)) {
					// This code will execute when the call is disconnected
//					Toast.makeText(context, "Detected call hangup event",
//							Toast.LENGTH_LONG).show();
					
					
					
				}
			 
	}
}
			 

