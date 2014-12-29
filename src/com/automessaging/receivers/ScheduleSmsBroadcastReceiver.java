package com.automessaging.receivers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.automessaging.common.Astat;
import com.automessaging.database.ProfilesDatabaseHelper;
import com.automessaging.schedule.ScheduleMessage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.telephony.gsm.SmsManager;
import android.util.Log;

public class ScheduleSmsBroadcastReceiver extends BroadcastReceiver{
	
	ProfilesDatabaseHelper mDbHelper = null;
	private String Numbers, ScheduleTime, ScheduleMessage, formatedCurrentTime;
	Date SchTime, CurTime;
	int ID;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		getScheduleMessageDetails();
		// here you can start an activity or service depending on your need
        // for ex you can start an activity to vibrate phone or to ring the phone   
		Calendar c = Calendar.getInstance();
		SimpleDateFormat dateFormater = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
		SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
		formatedCurrentTime = format1.format(c.getTime());
		try {
			SchTime = dateFormater.parse(ScheduleTime);
			CurTime = dateFormater.parse(formatedCurrentTime);
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("exception in mycallreceiver date formates :"+e.getMessage());
		}
		if(CurTime.after(SchTime) && Numbers.length() > 0){
			
			String[] NumStrArr = Numbers.split(", ");     
			for(int i = 0; i < NumStrArr.length; i++){
				String phoneNumberReciver=NumStrArr[i];// phone number to which SMS to be send
				String message = ScheduleMessage;// message to send
			    SmsManager sms = SmsManager.getDefault(); 
			    sms.sendTextMessage(phoneNumberReciver, null, message, null, null);
		}
		
		//Delete roe from database
			
			mDbHelper.DeleteSentScheduleMessageDetails(ID);
		
		}else{
			
		}
       
//       String message = ScheduleMessage;// message to send
//       SmsManager sms = SmsManager.getDefault(); 
//       sms.sendTextMessage(phoneNumberReciver, null, message, null, null);
       Log.v(this.getClass().getName(), ":::::::::check.... SMS Schedule broadcast Receiver called::::::::::  ");
       // Show the toast  like in above screen shot
       //Toast.makeText(context, "Alarm Triggered and SMS Sent", Toast.LENGTH_LONG).show();
	}

	private void getScheduleMessageDetails() {
		// TODO Auto-generated method stub
		Cursor objcur=mDbHelper.getProfileDetails("SELECT * FROM ScheduleMessageDetails");
		
		try{
			if(objcur!=null&&objcur.moveToFirst()){
				int count=0;
				objcur.moveToPosition(count);
				do{
					Numbers = objcur.getString(1);
					ScheduleTime = objcur.getString(2);
					ScheduleMessage = objcur.getString(3);
					ID = objcur.getInt(0);
					
				}while(objcur.moveToNext());			
			}
			}catch(Exception e){
				
				System.out.print("get scheduled message details check...."+e.getMessage());
			}finally{
				//objcur.close();
			}
	}

}
