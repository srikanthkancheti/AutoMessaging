package com.automessaging;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.TimeZone;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class TimeDurationActivity extends Activity {
	
	
	private TextView dateView, timeView, stopDateView, stopTimeView;
	private int year, month, day, hour, minute, sec;
	private SimpleDateFormat dateFormater;
	private String StartTime, StopTime, currentTime, formattedDate, formatedCurrentTime, 
			formattedStartHour, formattedStartMinute,formattedStopHour, formattedStopMinute, profileStartDate, profileStartTime,
			profileStopDate, profileStopTime, start_AM_PM, stop_AM_PM, startDateTime, stopDateTime;
	private Date startDate, stopDate, curDate;
	private Button setTime_btn;
	LinearLayout start_date_ll, start_time_ll, stop_date_ll, stop_time_ll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		dateView = (TextView) findViewById(R.id.start_time_picker_start_date_tv);
		timeView = (TextView) findViewById(R.id.start_time_picker_start_time_tv);
		stopDateView = (TextView) findViewById(R.id.stop_time_picker_stop_date_tv);
		stopTimeView = (TextView) findViewById(R.id.stop_time_picker_stop_time_tv);
		setTime_btn = (Button) findViewById(R.id.setTime_button);
		
		Intent intent = getIntent();
		profileStartDate = intent.getStringExtra("startDate_from_profile");
		profileStartTime = intent.getStringExtra("startTime_from_profile");
		start_AM_PM = intent.getStringExtra("start_am_pm");
		profileStopDate = intent.getStringExtra("stopDate_from_profile");
		profileStopTime = intent.getStringExtra("stopTime_from_profile");
		stop_AM_PM = intent.getStringExtra("stop_am_pm");
		
		 start_date_ll=(LinearLayout)findViewById(R.id.start_date_ll);
		 start_time_ll=(LinearLayout)findViewById(R.id.start_time_ll);
		 stop_date_ll=(LinearLayout) findViewById(R.id.stop_date_ll);
		 stop_time_ll=(LinearLayout) findViewById(R.id.stop_time_ll);
		Calendar calendar = Calendar.getInstance();
		currentTime = calendar.getTime().toString();
	      year = calendar.get(Calendar.YEAR);
	      month = calendar.get(Calendar.MONTH);
	      day = calendar.get(Calendar.DAY_OF_MONTH);
	      hour = calendar.get(Calendar.HOUR_OF_DAY);
          minute = calendar.get(Calendar.MINUTE);
          System.out.println("checking current date and time format.............."+currentTime);
       //  sec = calendar.get(Calendar.SECOND);
          
          dateFormater = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
          dateFormater.setTimeZone(TimeZone.getTimeZone("GMT"));
          
          SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
          
          formatedCurrentTime = format1.format(calendar.getTime());
          System.out.println("checking current date and time formatedCurrentTime.............."+formatedCurrentTime);
          
          if(null != profileStartDate && profileStopDate != null){
  			dateView.setText(profileStartDate);
  			timeView.setText(profileStartTime+" "+start_AM_PM);
  			stopDateView.setText(profileStopDate);
  			stopTimeView.setText(profileStopTime+" "+stop_AM_PM);
  			
  		}else{
  			StringTokenizer tokensCurrent = new StringTokenizer(formatedCurrentTime, "  ");
			String curDate = tokensCurrent.nextToken();// this will contain "Current Date of profile start time"
			String curTime = tokensCurrent.nextToken();// this will contain "Current Time of profile start time"
			String Cur_AM_PM = tokensCurrent.nextToken();
			dateView.setText(curDate);
			timeView.setText(curTime+" "+Cur_AM_PM);
			stopDateView.setText(curDate);
  			stopTimeView.setText(curTime+" "+Cur_AM_PM);
			
  		}
	      
	      start_date_ll.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			    DatePickerDialog mDatePickerdialog = new DatePickerDialog(TimeDurationActivity.this,
			            new startDateSetListener(), year, month, day);
			    mDatePickerdialog.show();
			}
		});
	      
	      start_time_ll.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
	            TimePickerDialog mTimePicker;
	            mTimePicker = new TimePickerDialog(TimeDurationActivity.this, new TimePickerDialog.OnTimeSetListener() {
	                @Override
	                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
	                	
	                    String temp = new StringBuffer().append(updateStartTime(selectedHour, selectedMinute)).toString(); 
	                	 StartTime = new StringBuffer().append(dateView.getText().toString()).append(" ").append(temp).toString();
	     	            System.out.println("checking the Start time.............."+StartTime);
	                  
		     	           try {
		   					startDate = dateFormater.parse(StartTime);
		   					curDate = dateFormater.parse(formatedCurrentTime);
		   					System.out.println("checking the Start date format.............."+startDate.toString());
		   					System.out.println("checking the current date format.............."+curDate.toString());
			   				  	if(startDate.after(curDate)){
				 	            	timeView.setText(temp);	
				 	            }else{
				 	            	Toast.makeText(getApplicationContext(), "Start time should be after current time !", Toast.LENGTH_LONG).show();
				 	            }
		   				} catch (ParseException e) {
		   					// TODO Auto-generated catch block
		   					e.printStackTrace();
		   				}
		   	            
	                }
					
	            }, hour, minute, true);//Yes 24 hour time no 12 hour time
	           // mTimePicker.setTitle("Select Time");
	            

	            mTimePicker.show();
	           
			}
		});  
	      
	      stop_date_ll.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DatePickerDialog mDatePickerdialog = new DatePickerDialog(TimeDurationActivity.this,
			            new stopDateSetListener(), year, month, day);
			    mDatePickerdialog.show();
			}
		});
	      
	      stop_time_ll.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TimePickerDialog mTimePicker;
	            mTimePicker = new TimePickerDialog(TimeDurationActivity.this, new TimePickerDialog.OnTimeSetListener() {
	                @Override
	                public void onTimeSet(TimePicker timePicker, int selectedStopHour, int selectedStopMinute) {
	                	
	                 	 String temp2 = new StringBuffer().append(updateStopTime(selectedStopHour, selectedStopMinute)).toString(); 
	                	 StopTime = new StringBuffer().append(stopDateView.getText().toString()).append(" ").append(temp2).toString();
	     	             System.out.println("checking the Start time.............."+StopTime);
	     	            try {
							stopDate = dateFormater.parse(StopTime);
							System.out.println("checking the Stop date format.............."+stopDate.toString());
							//if(stopDate.after(startDate)){
								stopTimeView.setText(temp2);
			 	           // }else{
			 	            //	Toast.makeText(getApplicationContext(), "Stop time should be after start time !", Toast.LENGTH_LONG).show();
			 	           // }
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                }

					private Object updateStopTime(int selectedStopHour,
							int selectedStopMinute) {
						// TODO Auto-generated method stub
						String timeSet = "";
				        if (selectedStopHour > 12) {
				        	selectedStopHour -= 12;
				            timeSet = "PM";
				        } else if (selectedStopHour == 0) {
				        	selectedStopHour += 12;
				            timeSet = "AM";
				        } else if (selectedStopHour == 12)
				            timeSet = "PM";
				        else
				            timeSet = "AM";

				        formattedStopHour = "" + selectedStopHour;
				        formattedStopMinute = "" + selectedStopMinute;

				        if(selectedStopHour < 10){

				        	formattedStopHour = "0" + selectedStopHour;
				        }
				        if(selectedStopMinute < 10){

				        	formattedStopMinute = "0" + selectedStopMinute;
				        }
				 
				        // Append in a StringBuilder
				         String aTime = new StringBuilder().append(formattedStopHour).append(':')
				                .append(formattedStopMinute).append(" ").append(timeSet).toString();
				         return aTime;
					}
					
	            }, hour, minute, true);//Yes 24 hour time no 12 hour time
	            mTimePicker.show();
			}
		});
	      
	      setTime_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				int a = 0,b = 0,c = 0,d=0;
				
				startDateTime = dateView.getText().toString()+" "+timeView.getText().toString();
				stopDateTime = stopDateView.getText().toString()+" "+stopTimeView.getText().toString();
				
			//	if(profileStartDate != null){
					StringTokenizer tokensStart = new StringTokenizer(timeView.getText().toString(), ":");
					
					StringTokenizer tokensStop = new StringTokenizer(stopTimeView.getText().toString(), ":");
					
					
					String startTimeHour = tokensStart.nextToken();
					String temp2 = tokensStart.nextToken();
					StringTokenizer tokensStartMin = new StringTokenizer(temp2, " ");
					String startTimeMin =  tokensStartMin.nextToken();
					
					String stopTimeHour = tokensStop.nextToken();
					String temp1 = tokensStop.nextToken();
					StringTokenizer tokensStopMin = new StringTokenizer(temp1, " ");
					String stopTimeMin = tokensStopMin.nextToken();
					
					a = Integer.parseInt(stopTimeHour);
					b = Integer.parseInt(stopTimeMin);
					c = Integer.parseInt(startTimeHour);
					d = Integer.parseInt(startTimeMin);
					
					Intent timeIntent = new Intent(TimeDurationActivity.this,ProfileActivity.class);
					timeIntent.putExtra("stop_time", stopDateTime);
					timeIntent.putExtra("stop_hour_of_day",a);
					timeIntent.putExtra("stop_min_of_hour",b);

					timeIntent.putExtra("start_time", startDateTime);
					timeIntent.putExtra("start_hour_of_day",c);
					timeIntent.putExtra("start_min_of_hour",d);
					startActivity(timeIntent);
					
//				}
//				else{
//				try {
//				    a = Integer.parseInt(formattedStopHour.toString());
//				    b = Integer.parseInt(formattedStopMinute.toString());
//				    c = Integer.parseInt(formattedStartHour.toString());
//				    d = Integer.parseInt(formattedStartMinute.toString());
//				} catch(NumberFormatException nfe) {
//				   System.out.println("Could not parse " + nfe);
//				} 
//				
//				Intent timeIntent = new Intent(TimeDurationActivity.this,ProfileActivity.class);
//				timeIntent.putExtra("stop_time", StopTime);
//				timeIntent.putExtra("stop_hour_of_day",a);
//				timeIntent.putExtra("stop_min_of_hour",b);
//
//				timeIntent.putExtra("start_time", StartTime);
//				timeIntent.putExtra("start_hour_of_day",c);
//				timeIntent.putExtra("start_min_of_hour",d);
//
//				startActivity(timeIntent);
//			}
			finish();
			}
		});
	 
	      
	}

	public class startDateSetListener implements OnDateSetListener {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			int mYear = year;
            int mMonth = monthOfYear+1;
            int mDay = dayOfMonth;
           // int month = monthOfYear + 1;
            String formattedMonth = "" + mMonth;
            String formattedDayOfMonth = "" + mDay;
            if(mMonth < 10){

            	formattedMonth = "0" + mMonth;
            }
            if(mDay < 10){

            	formattedDayOfMonth  = "0" + mDay ;
            }
            dateView.setText(new StringBuilder()
                    // Month is 0 based so add 1
                    .append(formattedDayOfMonth).append("-").append(formattedMonth).append("-")
                    .append(mYear).append(" "));
            System.out.println(dateView.getText().toString());

        }

	}
	
	public class stopDateSetListener implements OnDateSetListener {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			int mYear = year;
            int mMonth = monthOfYear+1;
            int mDay = dayOfMonth;
            String formattedMonth = "" + mMonth;
            String formattedDayOfMonth = "" + mDay;
            if(mMonth < 10){

            	formattedMonth = "0" + mMonth;
            }
            if(mDay < 10){

            	formattedDayOfMonth  = "0" + mDay ;
            }
            stopDateView.setText(new StringBuilder()
                    // Month is 0 based so add 1
                    .append(formattedDayOfMonth).append("-").append(formattedMonth).append("-")
                    .append(mYear).append(" "));
            System.out.println(stopDateView.getText().toString());
		}

	}

	// Used to convert 24hr format to 12hr format with AM/PM values
	public String updateStartTime(int hours, int mins) {
		// TODO Auto-generated method stub
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
	
}

