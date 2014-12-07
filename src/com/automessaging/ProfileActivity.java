package com.automessaging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.automessaging.common.Astat;
import com.automessaging.database.ProfilesDatabaseHelper;
import com.automessaging.groups.GroupsActivity;
public class ProfileActivity extends Activity {

	ProfilesDatabaseHelper mDbHelper = new ProfilesDatabaseHelper(ProfileActivity.this);

	private TextView profileNameTV, customMessage, startTime, endTime, replyGroup_tv;
	private CheckBox smsAutoResponse, phoneAutoResponse, autoSmsreading;
	private RelativeLayout responseTime_Rl, replyGroup, smsAutoReply, callAutoResponse;
	private LinearLayout autoReply, profile_lv;
	private Button btn;
	private ImageView sms_check_iv, call_check_iv;
	private String StartTime, StopTime, profileName, start, stop, currentDateTime,
			replyMessage, homeProfileName, homeStartTime, homeStopTime, homeProfileReplyMessage, selectedGroups;
	int StartHourOfDay, StartMinOfHour, StartSecOfMin, StopHourOfDay,
			StopMinOfHour, StopSecOfMin;
	private AlertDialog dialog;
	private boolean timeEdited = false;
	private boolean replyMsgEdited = false;
	private boolean isProfileSaved = false;
	boolean profileEdited = false;
	boolean replyGroupEdited = false;
	boolean isSmsAutoReply = false;
	boolean isCallAutoReply = false;
	int Ring_Mode = 0;
	int togglestatus = 0;
	int profileId;
	EditText edt;
	public ArrayList<ProfileNamesModel> profile_array_list = null;
	ActionBar actionBar;

	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_profile_setup);
		InitializeUI();
		Intent intent = getIntent();
		profileName = intent.getStringExtra("profile_name");
		homeProfileName = intent.getStringExtra("home_profile_name");
		homeStartTime = intent.getStringExtra("home_Start_Time");
		homeStopTime = intent.getStringExtra("home_Stop_Time");
		homeProfileReplyMessage= intent.getStringExtra("profile_replyMessage");
		selectedGroups = intent.getStringExtra("selected_groups");
		Astat.profileId = intent.getExtras().getInt("profile_id");
		profile_array_list = new ArrayList<ProfileActivity.ProfileNamesModel>();
		
		replyGroup.setClickable(false);
		//replyGroup.getBackground().setAlpha(45);
		//Astat.profileId = profileId;
		
		getProfileNames();
		
	  	   if (Astat.profileName != null && Astat.profileName.equalsIgnoreCase(Astat.KEY_EMPTY)) {
			   profileName = intent.getStringExtra("profile_name");
			   profileNameTV.setText(profileName);
		       Astat.profileName = profileName;
		    }else if(homeProfileName != null){
		    	
				Astat.profileName = homeProfileName;
				getProfileDetails();

		}

		if(null != selectedGroups){
			startTime.setText(Astat.startTime);
			endTime.setText(Astat.stopTime);
			profileNameTV.setText(Astat.profileName);
			customMessage.setText(Astat.replyMessage);
			replyGroup_tv.setText(selectedGroups);
			Astat.selectedGroups = selectedGroups;
		}
		actionBar = getActionBar();
		actionBar.setTitle(profileName);
		/**
		 * getting start and stop tyime from TimeDurationActivity and setting
		 * that strings to textviews
		 */
		StartTime = intent.getStringExtra("start_time");
		StopTime = intent.getStringExtra("stop_time");
		replyMessage = intent.getStringExtra("reply_msg");
		
		if (null != replyMessage && StartTime == null) {

			Astat.replyMessage = replyMessage;
			customMessage.setText(Astat.replyMessage);
			profileNameTV.setText(Astat.profileName);
			getProfileDetails();
			replyMsgEdited = true;
			replyGroup_tv.setText(selectedGroups);
		}

		StartHourOfDay = intent.getIntExtra("start_hour_of_day", 0);
		StartMinOfHour = intent.getIntExtra("start_min_of_hour", 0);
		StopHourOfDay = intent.getIntExtra("stop_hour_of_day", 0);
		StopMinOfHour = intent.getIntExtra("stop_min_of_hour", 0);

		Log.v(this.getClass().getName(), "check.." + StartTime);
		Log.v(this.getClass().getName(), "check.." + StopTime);
		Log.v(this.getClass().getName(), "check.." + Astat.profileName);
		
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm a");
		currentDateTime = sdf.format(c.getTime());
		Log.v(this.getClass().getName(), "check..current date and time : "
				+ currentDateTime);

		AudioManager myAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		if (StartTime == currentDateTime) {
			/**
			 * do phone silent, Ring mode 0 is for silent 1 is for vibration and
			 * 2 is for ring mode
			 */

			myAudioManager.setRingerMode(0);

		}
		if (StopTime == currentDateTime) {

			myAudioManager.setRingerMode(2);
		} else {
			myAudioManager.setRingerMode(2);
		}
		
		profile_lv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final Dialog alert = new Dialog(ProfileActivity.this);
				alert.setContentView(R.layout.profile_edit_dialog);
				alert.setTitle("Edit");
				alert.setCancelable(false);
				edt = (EditText)alert.findViewById(R.id.profile_profileName_editText);
				Button btn_ok = (Button)alert.findViewById(R.id.profile_ok_button);
				Button btn_cancel = (Button)alert.findViewById(R.id.profile_cancel_button);
				edt.setText(profileNameTV.getText());
				
				btn_ok.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String value = edt.getText().toString();
						// Do something with value!
						if(value.length() <= 10 && value.length()>=4){
							if( Astat.profileId != 0){
								profileNameTV.setText(value);
								Astat.profileName = value;
								mDbHelper.editProfileName(Astat.profileId, Astat.profileName);
							}else{
								profileNameTV.setText(value);
								Astat.profileName = value;
							}
							alert.dismiss();
						}if(value.length() < 4){
							edt.setError("Profile name must have 4 characters !");
						}if(value.length() > 10){
							edt.setError("Profile name should not exceed 10 characters !");
						}
					}
				});
				
				btn_cancel.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						alert.dismiss();
						
					}
				});
				
				alert.show();
				

			}
		});

		autoReply.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if(customMessage.getText().length() >0){
				Intent replyMsgIntent = new Intent(ProfileActivity.this,
						ReplyMsgActivity.class).putExtra("profile_name", profileNameTV.getText()).putExtra("msgFrom_profile", customMessage.getText().toString());
				Astat.profileName = profileNameTV.getText().toString();
				Astat.startTime = startTime.getText().toString();
				Astat.stopTime = endTime.getText().toString();
				Astat.selectedGroups = replyGroup_tv.getText().toString();
				startActivity(replyMsgIntent);

			}else{
				Intent replyMsgIntent = new Intent(ProfileActivity.this, ReplyMsgActivity.class).putExtra("profile_name", profileNameTV.getText());
				startActivity(replyMsgIntent);
				Astat.startTime = "";
				Astat.stopTime = "";
				finish();
			}
			}
		});

		responseTime_Rl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/**
				 * saveing profile name before calling timedurationactivity to
				 * retrive this name to profile activity after setting the start
				 * and stop time
				 */
				timeEdited = true;
				//if(timeEdited == true){
				if(startTime.getText().length()>0){
				StringTokenizer tokensStart = new StringTokenizer(startTime.getText().toString(), "  ");
				String startDate = tokensStart.nextToken();// this will contain "Start Date of profile start time"
				String startTime = tokensStart.nextToken();// this will contain "Start Time of profile start time"
				String Start_AM_PM = tokensStart.nextToken();// this will contain "AM or PM of profile start time"
				StringTokenizer tokensStop = new StringTokenizer(endTime.getText().toString(), "  ");
				String stopDate = tokensStop.nextToken();// this will contain "Stop Date of profile start time"
				String stopTime = tokensStop.nextToken();// this will contain "Stop Time of profile start time"
				String Stop_AM_PM = tokensStop.nextToken();// this will contain "AM or PM of profile stop time"
				
				Intent dateTimeIntent = new Intent(ProfileActivity.this,TimeDurationActivity.class)
				.putExtra("startDate_from_profile", startDate)
				.putExtra("startTime_from_profile", startTime)
				.putExtra("start_am_pm", Start_AM_PM)
				.putExtra("stopDate_from_profile", stopDate)
				.putExtra("stopTime_from_profile", stopTime)
				.putExtra("stop_am_pm", Stop_AM_PM);
				startActivity(dateTimeIntent);
				}else{
					Intent dateTimeIntent = new Intent(ProfileActivity.this,
							TimeDurationActivity.class);
					startActivity(dateTimeIntent);
				}
				Astat.startTime = startTime.getText().toString();
				Astat.stopTime = endTime.getText().toString();
				finish();
			}
		});
		
		replyGroup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				replyGroupEdited = true;
				Intent groupsIntent = new Intent(ProfileActivity.this, GroupsActivity.class);
				startActivity(groupsIntent);
				
			}
		});

		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getProfileNames();
				
				if(profile_array_list.size() > 0 ){
								
					mDbHelper.updateDateAndTime(Astat.profileName, Astat.startTime, Astat.stopTime, StartHourOfDay, StartMinOfHour, StopHourOfDay, StopMinOfHour, selectedGroups); 
					replyMsgEdited = false;
					profileEdited = false;
					Intent homeIntent = new Intent(ProfileActivity.this,ProfileListActivity.class);
					startActivity(homeIntent);
					finish();
								
				}else{
									
					 if(customMessage.getText().length() >0){
										
				   	   if(startTime.getText().length() > 0){
								
							mDbHelper.storeTime("INSERT INTO ProfileDetails (ProfileName, StartDate, StopDate, StartHourOfDay, StartMinOfHour,  StopHourOfDay, StopMinOfHour, SelectedGroups, ToggleStatus) "
										+ "values('"
										+ profileNameTV.getText().toString()
										+ "', '"
										+ startTime.getText()
										+ "','"
										+ endTime.getText()
										+ "', '"
										+ StartHourOfDay
										+ "', '"
										+ StartMinOfHour
										+ "', '"
										+ StopHourOfDay
										+ "', '"
										+ StopMinOfHour
										+ "', '"+selectedGroups+"', '"+0+"')");
								
								Intent homeIntent = new Intent(ProfileActivity.this,ProfileListActivity.class);
								startActivity(homeIntent);

								finish();
					
							}else
								Toast.makeText(getApplicationContext(), "Please set Start and Stop time for the profile !", Toast.LENGTH_LONG).show();
					 } else
			            Toast.makeText(getApplicationContext(), "Please enter a reply message for profile !", Toast.LENGTH_LONG).show();
			      }
	
			}
			
		});
		
		smsAutoReply.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(! isSmsAutoReply){
					sms_check_iv.setImageResource(R.drawable.setting_check);
					isSmsAutoReply = true;
				}else{
					sms_check_iv.setImageResource(R.drawable.setting_check_box_bg);
					isSmsAutoReply = false;
				}
			}
		});
		
		callAutoResponse.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(! isCallAutoReply){
					call_check_iv.setImageResource(R.drawable.setting_check);
					replyGroup.setClickable(true);
					replyGroup.getBackground().setAlpha(0);
					isCallAutoReply = true;
				}else{
					call_check_iv.setImageResource(R.drawable.setting_check_box_bg);
					isCallAutoReply = false;
				}
				
			}
		});

	}


	private void getProfileNames() {
		// TODO Auto-generated method stub
		//profile_array_list.clear();
		Cursor objcur=mDbHelper.getProfileNames("SELECT ProfileName FROM ProfileDetails WHERE ProfileName = '"+profileNameTV.getText().toString()+"'");
		try{
		if(objcur!=null&&objcur.moveToFirst()){
			int count=0;
			objcur.moveToPosition(count);
			do{
				ProfileNamesModel _profileNamessModel = new ProfileNamesModel();
				_profileNamessModel.setProfileName(objcur.getString(0));
				profile_array_list.add(_profileNamessModel);
				_profileNamessModel = null;
				
			}while(objcur.moveToNext());			
		}
		}catch(Exception e){
			
			System.out.print("get Groups check...."+e.getMessage());
		}finally{
			//objcur.close();
		}
		
	}

	private void getProfileDetails() {
		// TODO Auto-generated method stub
		Cursor objcur=mDbHelper.getProfileDetails("SELECT * FROM (SELECT p._Id, p.ProfileName, p.StartDate, p.StopDate, p.SelectedGroups, r.Message FROM ProfileDetails p, ReplyMessagesTable r WHERE p.ProfileName = r.ProfileName) WHERE ProfileName = '"+profileNameTV.getText().toString()+"'");
		try{
		if(objcur!=null&&objcur.moveToFirst()){
			int count=0;
			objcur.moveToPosition(count);
			do{
				Astat.replyMessage = objcur.getString(5);
				Astat.startTime = objcur.getString(2);
				Astat.stopTime = objcur.getString(3);
				Astat.selectedGroups = objcur.getString(4);
				Astat.profileId = objcur.getInt(0);
			}while(objcur.moveToNext());			
		}
		}catch(Exception e){
			
			System.out.print("get populating profile details check...."+e.getMessage());
		}finally{
			//objcur.close();
		}
		
		customMessage.setText(Astat.replyMessage);
		startTime.setText(Astat.startTime);
		endTime.setText(Astat.stopTime);
		replyGroup_tv.setText(Astat.selectedGroups);
	}

	@Override
	protected void onResume() {
		super.onResume();
		getProfileNames();
		getProfileDetails();
		customMessage.setText(Astat.replyMessage);
		profileNameTV.setText(Astat.profileName);
		actionBar.setTitle(Astat.profileName);
		if(homeProfileName != null){
			Astat.profileName = homeProfileName;
			profileNameTV.setText(homeProfileName);
			getProfileDetails();
		}
		else if(null != replyMessage && Astat.replyMessageIsEdited) {
			
			profileNameTV.setText(Astat.profileName);
			getProfileDetails();
			Astat.replyMessageIsEdited = false;
			replyGroup_tv.setText(Astat.selectedGroups);
		}
		if(StartTime != null && StopTime != null){
			startTime.setText(StartTime);
			endTime.setText(StopTime);
			profileNameTV.setText(Astat.profileName);
			customMessage.setText(Astat.replyMessage);
			Astat.startTime = startTime.getText().toString();
			Astat.stopTime = endTime.getText().toString();
			replyGroup_tv.setText(Astat.selectedGroups);
		}

		
		if(null != selectedGroups){
			replyGroup_tv.setText(selectedGroups);
			Astat.selectedGroups = selectedGroups;
		}
		if(Astat.profileId == 0 && timeEdited && replyMsgEdited){
			startTime.setText(Astat.startTime);
			endTime.setText(Astat.stopTime);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1) {
			if (resultCode == 2) {
				start = data.getStringExtra("start_time");
				startTime.setText(start);
			}
			if (resultCode == 3) {
				stop = data.getStringExtra("stop_time");
				endTime.setText(stop);
			}
		}

		Log.v(this.getClass().getName(), "check.." + start);
		Log.v(this.getClass().getName(), "check.." + stop);
	}// onActivityResult

	public void InitializeUI() {
		// TODO Auto-generated method stub
		profileNameTV = (TextView) findViewById(R.id.textview_profile_name);
		customMessage = (TextView) findViewById(R.id.auto_reply_tv2);
		startTime = (TextView) findViewById(R.id.auto_response_onoff_tv2);
		endTime = (TextView) findViewById(R.id.auto_response_onoff_tv3);
		btn = (Button) findViewById(R.id.profile_setup_ok);
		responseTime_Rl = (RelativeLayout) findViewById(R.id.relativeLayout_response_duration);
		autoReply = (LinearLayout) findViewById(R.id.autoReply_linearlayout);
		replyGroup = (RelativeLayout) findViewById(R.id.linearLayput_replyGroup);
		replyGroup_tv = (TextView) findViewById(R.id.reply_group_tv);
		profile_lv = (LinearLayout) findViewById(R.id.profile_name_linearLayout);
		smsAutoReply = (RelativeLayout) findViewById(R.id.service1_relativelayout);
		callAutoResponse = (RelativeLayout) findViewById(R.id.service2_relativelayout);
		sms_check_iv = (ImageView) findViewById(R.id.sms_checkbox_imageView);
		call_check_iv = (ImageView)findViewById(R.id.call_response_checkbox_imageView);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		Intent intent = new Intent(ProfileActivity.this, ProfileListActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_profile_actions, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.profile_action_settings:
			// settings action
			profileSettingsActivity();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void profileSettingsActivity() {
		// TODO Auto-generated method stub

		Intent profileSettings = new Intent(ProfileActivity.this,
				ProfileSettingsActivity.class);
		startActivity(profileSettings);

	}
	
public class ProfileNamesModel {
		
		public  String profile_Name;

		public String getProfileName() {
			return profile_Name;
		}

		public void setProfileName(String title) {
			this.profile_Name = title;
		}

	}

}
