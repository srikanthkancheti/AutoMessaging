package com.automessaging.schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.automessaging.ProfileListActivity;
import com.automessaging.R;
import com.automessaging.TimeDurationActivity;
import com.automessaging.receivers.ScheduleSmsBroadcastReceiver;
import com.automessaging.receivers.SilenceBroadcastReceiver;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

public class ScheduleMessage extends Activity{
	
	EditText phone_edt, message_edt;   
	static EditText schedule_date_edt;
	EditText schedule_time_edt;
	ImageView cal_iv, time_iv;
	Button schedule_btn;
	private int year, month, day, hour, minute;
	String formattedStartHour, formattedStartMinute, StartTime, formatedCurrentTime;
	private Date startDate, stopDate, curDate;
	private SimpleDateFormat dateFormater;
	
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_schedule_message);
		initializeUi();
		
		Calendar calendar = Calendar.getInstance();
	      year = calendar.get(Calendar.YEAR);
	      month = calendar.get(Calendar.MONTH);
	      day = calendar.get(Calendar.DAY_OF_MONTH);
	      hour = calendar.get(Calendar.HOUR_OF_DAY);
          minute = calendar.get(Calendar.MINUTE);
		
          dateFormater = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
          dateFormater.setTimeZone(TimeZone.getTimeZone("GMT"));
          SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
          formatedCurrentTime = format1.format(calendar.getTime());
          System.out.println("checking current date and time formatedCurrentTime.............."+formatedCurrentTime);
          
		
		
		cal_iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DatePickerDialog mDatePickerdialog = new DatePickerDialog(ScheduleMessage.this,
			            new startDateSetListener(), year, month, day);
			    mDatePickerdialog.show();
			}
		});
		
		time_iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TimePickerDialog mTimePicker;
	            mTimePicker = new TimePickerDialog(ScheduleMessage.this, new TimePickerDialog.OnTimeSetListener() {
	                @Override
	                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
	                	
	                    String temp = new StringBuffer().append(updateStartTime(selectedHour, selectedMinute)).toString(); 
	                	 StartTime = new StringBuffer().append(schedule_date_edt.getText().toString()).append(" ").append(temp).toString();
	     	            System.out.println("checking the Start time.............."+StartTime);
	                  
		     	           try {
		   					startDate = dateFormater.parse(StartTime);
		   					curDate = dateFormater.parse(formatedCurrentTime);
		   					System.out.println("checking the Start date format.............."+startDate.toString());
		   					System.out.println("checking the current date format.............."+curDate.toString());
			   				  	if(startDate.after(curDate)){
				 	            	schedule_time_edt.setText(temp);	
				 	            }else{
				 	            	Toast.makeText(getApplicationContext(), "Schdule time should be after current time !", Toast.LENGTH_LONG).show();
				 	            }
		   				} catch (ParseException e) {
		   					// TODO Auto-generated catch block
		   					e.printStackTrace();
		   				}
		   	            
	                }

						public String updateStartTime(int hours, int mins) {
							// TODO Auto-generated method stub
							// Used to convert 24hr format to 12hr format with AM/PM values
							String timeSet = "";
					        if (hours > 12) {
					            hours -= 12;
					            timeSet = "PM";
					        } else if (hours == 0) {
					            hours += 12;
					            timeSet = "AM";
					        } else if (hours == 12)
					            timeSet = "PM";
					        else
					            timeSet = "AM";

					        formattedStartHour = "" + hours;
					        formattedStartMinute = "" + mins;

					        if(hours < 10){

					        	formattedStartHour = "0" + hours;
					        }
					        if(mins < 10){

					        	formattedStartMinute = "0" + mins;
					        }
					 
					        // Append in a StringBuilder
					         String aTime = new StringBuilder().append(formattedStartHour).append(':')
					                .append(formattedStartMinute).append(" ").append(timeSet).toString();
					         return aTime;
						}
					
					
	            }, hour, minute, true);//Yes 24 hour time no 12 hour time
	           // mTimePicker.setTitle("Select Time");
	            

	            mTimePicker.show();
		}
		});
		
		
	schedule_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					Date SmsScheduleTime = null;
					String scheduleDateTime = schedule_date_edt.getText()+" "+schedule_time_edt.getText().toString();
					Calendar smsScheduleCalendar = Calendar.getInstance();
					SimpleDateFormat dateFormater = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
					try {
						SmsScheduleTime = dateFormater.parse(scheduleDateTime);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(SmsScheduleTime!= null){
						smsScheduleCalendar.setTime(SmsScheduleTime);
					}
					
					Intent scheduleReceiverIntent = new Intent(ScheduleMessage.this, ScheduleSmsBroadcastReceiver.class);
					AlarmManager am = (AlarmManager) ScheduleMessage.this.getSystemService(ALARM_SERVICE);
					PendingIntent ScheduleSmsSPI = PendingIntent.getBroadcast(ScheduleMessage.this, 1,scheduleReceiverIntent,PendingIntent.FLAG_UPDATE_CURRENT);
					am.set(AlarmManager.RTC_WAKEUP,smsScheduleCalendar.getTimeInMillis(), ScheduleSmsSPI);
					Log.v(this.getClass().getName(),"check..profile Schedule calender :  "+smsScheduleCalendar.getTime());
					finish();
				}
			});
	}

	private void initializeUi() {
		// TODO Auto-generated method stub
		schedule_btn = (Button) findViewById(R.id.schedule_button);
		phone_edt = (EditText) findViewById(R.id.schedele_phone_editText);
		message_edt = (EditText) findViewById(R.id.schedule_message_editText);
		schedule_date_edt = (EditText) findViewById(R.id.schedule_cal_editText);
		schedule_time_edt = (EditText) findViewById(R.id.schedule_time_editText);
		cal_iv = (ImageView) findViewById(R.id.schedule_cal_imageView);
		time_iv = (ImageView) findViewById(R.id.schedule_time_imageView);
		
		
	}
}
