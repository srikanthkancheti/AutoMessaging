package com.automessaging.schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.automessaging.R;
import com.automessaging.database.ProfilesDatabaseHelper;
import com.automessaging.receivers.ScheduleSmsBroadcastReceiver;

public class ScheduleMessage extends Activity implements OnItemClickListener{
	ProfilesDatabaseHelper mDbHelper = new ProfilesDatabaseHelper(ScheduleMessage.this);
	EditText message_edt;   
	static EditText schedule_date_edt;
	EditText schedule_time_edt;
	ImageView cal_iv, time_iv;
	Button schedule_btn;
	private int year, month, day, hour, minute;
	private String formattedStartHour, formattedStartMinute, StartTime, formatedCurrentTime, scheduleDateTime, scheduleMessage, enteredNumbers;
	private Date startDate, stopDate, curDate;
	private SimpleDateFormat dateFormater;
	// Store contacts values in these arraylist
	public static ArrayList<String> phoneValueArr = new ArrayList<String>();
	public static ArrayList<String> nameValueArr = new ArrayList<String>();
	String toNumberValue="";
	// hashMap with multiple values with default size and load factor
	HashMap<String, ArrayList<String>> multiContactsMap = new HashMap<String, ArrayList<String>>();
	// create an arrayList to store contact numbers
	ArrayList<String> selectedContactsList = new ArrayList<String>();
		
    private MultiAutoCompleteTextView mAuto;
    Cursor cursor;
	
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
          
          readContactData();

  		// specify the minimum type of characters before drop-down list is shown
          mAuto.setThreshold(1);
  		// comma to separate the different colors
          mAuto.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
          mAuto.setOnItemClickListener(this);
		
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
					scheduleMessage = message_edt.getText().toString();
					
					saveSelectedContacts();
					
					//finish();
				}

				private void saveSelectedContacts() {
					// TODO Auto-generated method stub
					String[] str = mAuto.getText().toString().split(", ");
					 List<String> values = null;
					 Date SmsScheduleTime = null;
					if(str.length > 0){
					
						for(int i=0; i<str.length; i++) {
							
							if(multiContactsMap.containsKey(str[i])){
								
								// Get a set of the entries
							    Set<Entry<String, ArrayList<String>>> setMap = multiContactsMap.entrySet();
							    // Get an iterator
							    Iterator<Entry<String,  ArrayList<String>>> iteratorMap = setMap.iterator();
							    
							    System.out.println("\n HashMap with Multiple contact numbers");
							    // display all the elements
							    while(iteratorMap.hasNext()) {
							    	Map.Entry<String, ArrayList<String>> entry = 
							    			(Map.Entry<String, ArrayList<String>>) iteratorMap.next();
							        String key = entry.getKey();
							        values = entry.getValue();
							        System.out.println("Key = '" + key + "' has values: " + values);
							        
							     // retrieving data from string list array in for loop
							        for (int j=0;j < values.size();j++)
							        {
							        	enteredNumbers = values.get(j)+",";
							        }
							        //enteredNumbers = values.toString();
							    }
								
							}else{
								//store the entered numbers in database
								enteredNumbers = str[i]+", ";
							}
						}
				}else{
					Toast.makeText(getApplicationContext(), "please enter a phone number to schedule message.", Toast.LENGTH_LONG).show();
				}
				
					if(scheduleMessage.length() > 0){
						
						if(schedule_date_edt.getText().length() > 0){
							
							if(schedule_time_edt.getText().length() > 0){
								
								scheduleDateTime = schedule_date_edt.getText()+" "+schedule_time_edt.getText().toString();
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
								
								mDbHelper.executeSQL("INSERT INTO ScheduleMessageDetails (Numbers, ScheduleTime, ScheduleMessage) VALUES ('"+enteredNumbers+"', '"+scheduleDateTime+"', '"+scheduleMessage+"')");
								
								Toast.makeText(getApplicationContext(), "Message Scheduled.", Toast.LENGTH_LONG).show();
								finish();
							}else{
								Toast.makeText(getApplicationContext(), "Please select a time", Toast.LENGTH_LONG).show();
							}
						}else{
							Toast.makeText(getApplicationContext(), "Please select a date", Toast.LENGTH_LONG).show();
						}
					}else{
						Toast.makeText(getApplicationContext(), "Please enter a message to schedule", Toast.LENGTH_LONG).show();
					}
				}
			});
	}



	private void readContactData() {
		// TODO Auto-generated method stub
		
		String phoneNumber = "";
		String phoneName = "";
		phoneValueArr.clear();
		nameValueArr.clear();
		
		try{
		
		ContentResolver content = getContentResolver();
		
        cursor = content.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                PEOPLE_PROJECTION, null, null, null);
        
        if(null != cursor && cursor.moveToFirst()){
        	do{
        		// Get Phone number
    			phoneNumber =""+cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
    			phoneName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
    			phoneValueArr.add(phoneNumber.toString());
    			nameValueArr.add(phoneName.toString());
        	}while(cursor.moveToNext());
     
        }
        
        //cursor.close();
        
		}catch(Exception e){
			
			Log.i("Read contacts data","Exception : "+ e);
			
		}finally {
			//if (null != cursor)
				//cursor.close();
		}
		
		ContactListAdapter adapter = new ContactListAdapter(this, cursor);
        mAuto.setAdapter(adapter);
	}



	private void initializeUi() {
		// TODO Auto-generated method stub
		schedule_btn = (Button) findViewById(R.id.schedule_button);
		mAuto = (MultiAutoCompleteTextView) findViewById(R.id.mmWhoNo);
		message_edt = (EditText) findViewById(R.id.schedule_message_editText);
		schedule_date_edt = (EditText) findViewById(R.id.schedule_cal_editText);
		/**schedule_date_edt.setKeyListener(null); 
		schedule_date_edt.setClickable(false); 
		schedule_date_edt.setCursorVisible(false); 
		schedule_date_edt.setFocusable(false); 
		schedule_date_edt.setFocusableInTouchMode(false);
		 above all do the same thing as EditText not editable*/
		schedule_date_edt.setFocusableInTouchMode(false);
		schedule_time_edt = (EditText) findViewById(R.id.schedule_time_editText);
		schedule_time_edt.setFocusableInTouchMode(false);
		cal_iv = (ImageView) findViewById(R.id.schedule_cal_imageView);
		time_iv = (ImageView) findViewById(R.id.schedule_time_imageView);
		
		
	}
	
	public static class ContactListAdapter extends CursorAdapter implements Filterable {
        public ContactListAdapter(Context context, Cursor c) {
            super(context, c);
            mContent = context.getContentResolver();
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
             final LayoutInflater inflater = LayoutInflater.from(context);
             
             View retView = inflater.inflate(R.layout.schedule_msg_custcontview,parent,false);
             return retView;

        }        

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            //((TextView) view).setText(cursor.getString(2));
        	TextView pname = (TextView)view.findViewById(R.id.ccontName);
            TextView pnum = (TextView)view.findViewById(R.id.ccontNo); 
            pname.setText(cursor.getString(2));
            pnum.setText(cursor.getString(1));

        }

        @Override
        public String convertToString(Cursor cursor) {
            return cursor.getString(2);
        }

        @Override
        public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
            if (getFilterQueryProvider() != null) {
                return getFilterQueryProvider().runQuery(constraint);
            }

            StringBuilder buffer = null;
            String[] args = null;
            if (constraint != null) {
                buffer = new StringBuilder();
            buffer.append("UPPER(");
            buffer.append(ContactsContract.Contacts.DISPLAY_NAME);
            buffer.append(") GLOB ?");
                args = new String[] { constraint.toString().toUpperCase() + "*" };
            }

            return mContent.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PEOPLE_PROJECTION,
                    buffer == null ? null : buffer.toString(), args,
                    null);
        }



        private ContentResolver mContent;           
    }

 private static final String[] PEOPLE_PROJECTION = new String[] {
        ContactsContract.Contacts._ID,
        ContactsContract.CommonDataKinds.Phone.NUMBER,
        ContactsContract.Contacts.DISPLAY_NAME,
    };

@Override
public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	// TODO Auto-generated method stub

	TextView temp = (TextView) view.findViewById(R.id.ccontNo);
	TextView temp2 = (TextView) view.findViewById(R.id.ccontName);
    final String selectedNumber = temp.getText().toString();
    final String selectedName = temp2.getText().toString();
    
   if (selectedNumber != null) {
   	
   	InputMethodManager imm = (InputMethodManager) getSystemService(
   		    INPUT_METHOD_SERVICE);
   		imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

   	Log.d("Autocomplete selected Contact: ", " Name:"+selectedName+" Number:"+selectedNumber);
   	
   	if(multiContactsMap.containsKey(selectedName) && multiContactsMap.containsValue(selectedNumber)){
   		
   		Toast.makeText(getApplicationContext(), "Contact already selected", Toast.LENGTH_LONG).show();
   	}else{
   		
   		selectedContactsList.add(selectedNumber);
   		multiContactsMap.put(selectedName, selectedContactsList);
   	}
   	
   }
 }
}
