package com.automessaging;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.automessaging.ProfileActivity.ProfileNamesModel;
import com.automessaging.common.Astat;
import com.automessaging.database.ProfilesDatabaseHelper;
import com.automessaging.receivers.SilenceBroadcastReceiver;
import com.automessaging.receivers.UnsilenceBroadcastReceiver;

public class ProfileListActivity extends Activity{

	ListView profilesList;
	private boolean[] checkArray;
	private ArrayList<ProfileData> profile_list  = null;
	private ProfilesListAdapter _ProfileListAdapter = null;
	private boolean isUpdateAB = false;
	private TextView profile_tv, start_tv, stop_tv, callLogsTextView;
	private RelativeLayout rv;
	private ToggleButton toggleButton;
	private ProfilesDatabaseHelper DbHelper = new ProfilesDatabaseHelper(ProfileListActivity.this);
	public boolean[] toggleCheckArray;
	private EditText edt;
	Button btn_ok, btn_cancel;
	
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_profile_list);
		profilesList = (ListView) findViewById(R.id.listView1);
		rv = (RelativeLayout) findViewById(R.id.relativeLayout_home_list_item);
		toggleButton = (ToggleButton) findViewById(R.id.toggleButton1);
		profile_tv = (TextView) findViewById(R.id.textView_home_profileName);
		start_tv = (TextView) findViewById(R.id.textView_home_fromTime);
		stop_tv = (TextView) findViewById(R.id.textView_home_toTime);
		
		profile_list = new ArrayList<ProfileListActivity.ProfileData>();
		Log.v(getClass().getName(), "database count " + profile_list.size());

		setData();
		if(profilesList.getCount() == 0){
		showDialog();
		}
		


	}

	private void showDialog() {
		// TODO Auto-generated method stub
		
			final Dialog alert = new Dialog(ProfileListActivity.this);
			alert.setContentView(R.layout.home_dialog);
			alert.setTitle("Profile Name");
			alert.setCancelable(false);
			alert.getWindow().setFormat(PixelFormat.TRANSLUCENT);
			edt = (EditText)alert.findViewById(R.id.home_profileName_editText);
			btn_ok = (Button)alert.findViewById(R.id.home_ok_button);
			btn_cancel = (Button)alert.findViewById(R.id.home_cancel_button);
			
			btn_ok.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String value = edt.getText().toString().trim();
					// Do something with value!
					if(value.length() <= 10 && value.length()>=4){
							
							Astat.profileName = "";
							Astat.replyMessage = "";
							Intent profileIntent = new Intent(ProfileListActivity.this,ProfileActivity.class);
							profileIntent.putExtra("profile_name", value);
							startActivity(profileIntent);
							alert.dismiss();
							finish();
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
					if(profilesList.getCount() == 0){
						finish();
					}
				}
			});
			
			alert.show();
			
		}
	

	
	private void setData() {
		// TODO Auto-generated method stub
		profile_list.clear();
		Cursor profileCursor = DbHelper
				.getProfiles("SELECT * FROM ProfileDetails");// fetchAllProfiles();
		try {
			if (null != profileCursor && profileCursor.moveToFirst()) {
				do {
					ProfileData _profileData = new ProfileData();
					_profileData.setProfileName(profileCursor.getString(1));
					_profileData.setStartDate(profileCursor.getString(2));
					_profileData.setStopDate(profileCursor.getString(3));
					_profileData.setStartHourOfDay(profileCursor.getInt(4));
					_profileData.setStartMinOfHour(profileCursor.getInt(5));
					_profileData.setStopHourOfDay(profileCursor.getInt(6));
					_profileData.setStopMinOfHour(profileCursor.getInt(7));
					_profileData.setSelectedGroups(profileCursor.getString(8));
					_profileData.setToggleStatus(profileCursor.getLong(9));
					_profileData.set_id(profileCursor.getInt(0));
					profile_list.add(_profileData);
					_profileData = null;
				} while (profileCursor.moveToNext());

			}
		} catch (Exception ex) {

		} finally {
			if (null != profileCursor)
				profileCursor.close();
		}

		checkArray = new boolean[profile_list.size()];
		toggleCheckArray = new boolean[profile_list.size()];
		_ProfileListAdapter = new ProfilesListAdapter();
		profilesList.setAdapter(_ProfileListAdapter);
		RingMode();
	
	}

	private void RingMode() {
		// TODO Auto-generated method stub
		AudioManager audio = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
		audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
		quit(); 
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		setData();
		if(profilesList.getCount() == 0){
			showDialog();
			}
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_home_actions, menu);
		MenuItem item = null;
		if (profilesList.getCount() > 0) {
			if (!isUpdateAB) {
				menu.findItem(R.id.action_discard).setEnabled(true).setVisible(true);
				menu.findItem(R.id.action_done).setEnabled(false).setVisible(false);
				menu.findItem(R.id.action_new).setEnabled(true).setVisible(true);
				menu.findItem(R.id.action_settings).setEnabled(true);
				item = menu.add(Menu.NONE, R.id.action_discard, Menu.NONE, R.string.delete);
				item = menu.add(Menu.NONE, R.id.action_new, Menu.NONE, "Add Profile");
			} else {
				menu.findItem(R.id.action_done).setEnabled(true).setVisible(true);
				menu.findItem(R.id.action_discard).setEnabled(false).setVisible(false);
				menu.findItem(R.id.action_new).setEnabled(false).setVisible(false);

				item = menu.add(Menu.NONE, R.id.action_done, Menu.NONE,R.string.done);
			}
		} else {
			menu.findItem(R.id.action_discard).setEnabled(false).setVisible(false);
			menu.findItem(R.id.action_done).setEnabled(false).setVisible(false);
			menu.findItem(R.id.action_new).setEnabled(true).setVisible(true);
			menu.findItem(R.id.action_settings).setEnabled(false).setVisible(false);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {

		case R.id.action_done:
			StringBuilder _itemBuilder = new StringBuilder();
			_ProfileListAdapter.setEditMode(false);
			_ProfileListAdapter.notifyDataSetChanged();
			isUpdateAB = false;
			invalidateOptionsMenu();
			for (int i = 0; i < profile_list.size(); i++) {
				if (checkArray[i]) {
					_itemBuilder.append("'"
							+ profile_list.get(i).getProfileName() + "'" + ",");
					DbHelper.deleteProfileReplyMessages(profile_list.get(i).getProfileName());
				}
			}
			if (_itemBuilder.length() > 0) {
				_itemBuilder.deleteCharAt(_itemBuilder.length() - 1);
				
				DbHelper.deleteProfilesFromDb(_itemBuilder.toString());
				
			}
			setData();
			if(profile_list.size()< 1){
				//System.exit(0);
				
				quit(); 
				DbHelper.deleteGrousData();
			}
			break;
		case R.id.action_discard:
			// discard action
			_ProfileListAdapter.setEditMode(true);
			_ProfileListAdapter.notifyDataSetChanged();
			isUpdateAB = true;
			invalidateOptionsMenu();
			break;

		case R.id.action_new:
			// New profile action
			showDialog();
			//profileDialog();
			return true;
		case R.id.action_settings:
			// settings action
			settingsActivity();
			return true;

		}
		return true;
	}

	private void quit() {
		// TODO Auto-generated method stub
		Intent startMain = new Intent(Intent.ACTION_MAIN);
	    startMain.addCategory(Intent.CATEGORY_HOME);
	    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    startActivity(startMain);
	}

	private void settingsActivity() {
		// TODO Auto-generated method stub

		Intent settingsIntent = new Intent(ProfileListActivity.this,
				SettingsActivity.class);
		startActivity(settingsIntent);

	}

	public class ProfilesListAdapter extends BaseAdapter {

		LayoutInflater inflater;
		private boolean isEdit;

		public ProfilesListAdapter() {
			this.inflater = LayoutInflater.from(ProfileListActivity.this);
		}

		@Override
		public int getCount() {
			return profile_list == null ? 0 : profile_list.size();
		}

		@Override
		public ProfileData getItem(int position) {
			return profile_list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		/* Following code view the list view in holded surface */
		@Override
		public View getView(final int position, View convertView,ViewGroup parent) {
			final ViewHolder holder = convertView == null ? new ViewHolder(): (ViewHolder) convertView.getTag();

			if (convertView == null) {
				convertView = inflater.inflate(R.layout.profile_list_item, null);
				holder.tv = (TextView) convertView.findViewById(R.id.textView_home_profileName);
				holder.tv2 = (TextView) convertView.findViewById(R.id.textView_home_fromTime);
				holder.tv3 = (TextView) convertView.findViewById(R.id.textView_home_toTime);
				holder.iv = (ImageView) convertView.findViewById(R.id.checkbox);
				holder.tglBtn = (ToggleButton) convertView.findViewById(R.id.toggleButton1);
				holder.rl = (RelativeLayout) convertView.findViewById(R.id.relativeLayout_home_list_item);
				convertView.setTag(holder);
			}

			holder.tv.setText(profile_list.get(position).getProfileName());
			holder.tv2.setText(profile_list.get(position).getStartDate());
			holder.tv3.setText(profile_list.get(position).getStopDate());
			
			
			if (isEdit) {
				holder.iv.setVisibility(View.VISIBLE);
				holder.iv.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (checkArray != null && checkArray[position]) {
							checkArray[position] = false;
							_ProfileListAdapter.notifyDataSetChanged();
						} else {
							checkArray[position] = true;
							_ProfileListAdapter.notifyDataSetChanged();
						}
					}
				});

				if (checkArray != null && checkArray[position]) {
					holder.iv.setImageResource(R.drawable.setting_check);
					holder.tglBtn.setVisibility(View.INVISIBLE);
				} else {
					holder.iv.setImageResource(R.drawable.setting_check_box_bg);
					holder.tglBtn.setVisibility(View.INVISIBLE);
				}
			} else {
				holder.iv.setVisibility(View.GONE);
			}
			
			holder.rl.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					String tempProfileName = profile_list.get(position).getProfileName();
					String tempStartTime = profile_list.get(position).getStartDate();
					String tempStopTime = profile_list.get(position).getStopDate();
					String tempSelectedGroups = profile_list.get(position).getSelectedGroups();
					String profile_replyMessage = DbHelper.getProfileReplyMessage(tempProfileName);
					Intent profileIntent = new Intent(ProfileListActivity.this, ProfileActivity.class)
					.putExtra("home_profile_name", tempProfileName)
					.putExtra("home_Start_Time", tempStartTime)
					.putExtra("home_Stop_Time", tempStopTime)
					.putExtra("profile_replyMessage", profile_replyMessage)
					.putExtra("profile_id", profile_list.get(position).get_id())
					.putExtra("Edit", true);
					
					startActivity(profileIntent);
				}
			});
			
			
			final SharedPreferences prefs = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
			boolean tglStatus = (profile_list.get(position).getToggleStatus() == 1)?true:false;

			holder.tglBtn.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					// TODO Auto-generated method stub
					int flag;
					//int flag = (holder.tglBtn.isChecked()==true)? 1:0;
					// create new calendar instance
					String startDate = profile_list.get(position).getStartDate();
					Calendar profileStartCalendar = Calendar.getInstance();
					Date prodileStartDate = null, prodileStoptDate = null;
					SimpleDateFormat dateFormater = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
					try {
						prodileStartDate = dateFormater.parse(startDate);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(prodileStartDate!= null){
					profileStartCalendar.setTime(prodileStartDate);
					}
					Calendar profileStopCalendar = Calendar.getInstance();
					// set the time to selected profile stop time
					// create a pending intent to be called at profile stop time
					String stopDate = profile_list.get(position).getStopDate();
					Log.v(this.getClass().getName(),"check..profile stop time while checkbox check...:  "+ stopDate);
					try {
						prodileStoptDate = dateFormater.parse(stopDate);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(prodileStoptDate!= null){
						profileStopCalendar.setTime(prodileStoptDate);
					}
					
					
					if(holder.tglBtn.isChecked()){
						flag = 1;
						holder.tglBtn.setChecked(prefs.getBoolean("toggle_state", true));
						
						Intent silenceReceiverIntent = new Intent(ProfileListActivity.this, SilenceBroadcastReceiver.class);
						Intent unSilenceReceiverIntent = new Intent(ProfileListActivity.this, UnsilenceBroadcastReceiver.class);
						
						SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
						Editor editor = pref.edit();
						
						editor.putString("start_date", startDate);
						
						Log.v(this.getClass().getName(),"check..profile start time while checkbox check...:  "+ startDate);
						
						
						AlarmManager am = (AlarmManager) ProfileListActivity.this.getSystemService(ALARM_SERVICE);
						// create a pending intent to be called at profile start time
						PendingIntent profileStartPI = PendingIntent.getBroadcast(ProfileListActivity.this, 0,silenceReceiverIntent,PendingIntent.FLAG_UPDATE_CURRENT);
						// schedule time for pending intent, and set the interval to day so that this event will repeat at the selected time every day
						am.setRepeating(AlarmManager.RTC_WAKEUP,profileStartCalendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY, profileStartPI);
						PendingIntent profileStopPI = PendingIntent.getBroadcast(ProfileListActivity.this, 0,unSilenceReceiverIntent,PendingIntent.FLAG_UPDATE_CURRENT);
						am.setRepeating(AlarmManager.RTC_WAKEUP,profileStopCalendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY, profileStopPI);
					}else{
						holder.tglBtn.setChecked(false);
						holder.tglBtn.setSelected(false);
						flag = 0;
						//holder.tglBtn.setChecked(prefs.getBoolean("toggle_state", false));
						AudioManager audio = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
						audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
					}
					DbHelper.updateToggleStatus(flag, profile_list.get(position).get_id());
				}
			});
			
			if(tglStatus == true){
				
				holder.tglBtn.setChecked(true);
				
				Intent silenceReceiverIntent = new Intent(ProfileListActivity.this, SilenceBroadcastReceiver.class);
				Intent unSilenceReceiverIntent = new Intent(ProfileListActivity.this, UnsilenceBroadcastReceiver.class);
				
				SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
				Editor editor = pref.edit();
				String startDate = profile_list.get(position).getStartDate();
				editor.putString("start_date", startDate);
				
				Log.v(this.getClass().getName(),"check..profile start time while checkbox check...:  "+ startDate);
				// create new calendar instance
				Calendar profileStartCalendar = Calendar.getInstance();
				Date prodileStartDate = null, prodileStoptDate = null;
				SimpleDateFormat dateFormater = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
				try {
					prodileStartDate = dateFormater.parse(startDate);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(prodileStartDate!= null){
				profileStartCalendar.setTime(prodileStartDate);
				}
				
				AlarmManager am = (AlarmManager) ProfileListActivity.this.getSystemService(ALARM_SERVICE);
				// create a pending intent to be called at profile start time
				PendingIntent profileStartPI = PendingIntent.getBroadcast(ProfileListActivity.this, 0,silenceReceiverIntent,PendingIntent.FLAG_UPDATE_CURRENT);
				// schedule time for pending intent, and set the interval to day so that this event will repeat at the selected time every day
				am.setRepeating(AlarmManager.RTC_WAKEUP,profileStartCalendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY, profileStartPI);
				Log.v(this.getClass().getName(),"check..profile start calender :  "+profileStartCalendar.getTime());
				Calendar profileStopCalendar = Calendar.getInstance();
				// set the time to selected profile stop time
				// create a pending intent to be called at profile stop time
				String stopDate = profile_list.get(position).getStopDate();
				Log.v(this.getClass().getName(),"check..profile stop time while checkbox check...:  "+ stopDate);
				try {
					prodileStoptDate = dateFormater.parse(stopDate);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(prodileStoptDate!= null){
					profileStopCalendar.setTime(prodileStoptDate);
					}
				
				PendingIntent profileStopPI = PendingIntent.getBroadcast(ProfileListActivity.this, 0,unSilenceReceiverIntent,PendingIntent.FLAG_UPDATE_CURRENT);
				am.setRepeating(AlarmManager.RTC_WAKEUP,profileStopCalendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY, profileStopPI);
				
				editor.putString("stop_date", stopDate);
				editor.putString("profile_name", profile_list.get(position).getProfileName());
				editor.putBoolean("toggle_state", true);
				editor.commit();

			}else{
				holder.tglBtn.setChecked(false);
				Intent silenceReceiverIntent = new Intent(ProfileListActivity.this, SilenceBroadcastReceiver.class);
				Intent unSilenceReceiverIntent = new Intent(ProfileListActivity.this, UnsilenceBroadcastReceiver.class);

				//get Alarm manager instance
				AlarmManager am = (AlarmManager) ProfileListActivity.this.getSystemService(ALARM_SERVICE);
				//build intent for start time
				PendingIntent profileStartPI = PendingIntent.getService(ProfileListActivity.this, 0, silenceReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				//cancel it
				am.cancel(profileStartPI);
				//build intent for stop time 
				PendingIntent profileStopPI = PendingIntent.getService(ProfileListActivity.this, 0, unSilenceReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				//cancel it
				am.cancel(profileStopPI);
				
				SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
				Editor editor = pref.edit();
				editor.putBoolean("toggle_state", true);
			}


			return convertView;
			
		}


		public void setEditMode(boolean isEdit) {
			this.isEdit = isEdit;
		}
		

		private class ViewHolder {
			public RelativeLayout rl;
			private TextView tv, tv2, tv3;
			private ImageView iv;
			private ToggleButton tglBtn;
			private String starttime, stopTime;
			RelativeLayout relative;

		}

	}

	public class ProfileData {

		public String ProfileName, StartDate, StopDate, SelectedGroups;
		public long toggleStatus;
		public int StartHourOfDay, StartMinOfHour, StartSecOfMin,
				StopHourOfDay, StopMinOfHour, StopSecOfMin, _id;
		
		public void set_id(int _id) {
			this._id = _id;
		}


		public int get_id() {
			return _id;
		}

		public void setToggleStatus(long long1) {
			// TODO Auto-generated method stub
			this.toggleStatus = long1;
		}
		
		public long getToggleStatus() {
			return toggleStatus;
		}

		
		public String getProfileName() {
			return ProfileName;
		}

		public void setProfileName(String profileName) {
			this.ProfileName = profileName;
		}

		public String getStartDate() {
			return StartDate;
		}

		public void setStartDate(String StartDate) {
			this.StartDate = StartDate;
		}

		public String getStopDate() {
			return StopDate;
		}

		public void setStopDate(String stopDate) {
			this.StopDate = stopDate;
		}

		public int getStartHourOfDay() {
			return StartHourOfDay;
		}

		public void setStartHourOfDay(int startHourOfDay) {
			this.StartHourOfDay = startHourOfDay;
		}

		public int getStartMinOfHour() {
			return StartMinOfHour;
		}

		public void setStartMinOfHour(int startMinOfHour) {
			this.StartMinOfHour = startMinOfHour;
		}


		public int getStopHourOfDay() {
			return StopHourOfDay;
		}

		public void setStopHourOfDay(int stopHourOfDay) {
			this.StopHourOfDay = stopHourOfDay;
		}

		public int getStopMinOfHour() {
			return StopMinOfHour;
		}

		public void setStopMinOfHour(int stopMinOfHour) {
			this.StopMinOfHour = stopMinOfHour;
		}

		public String getSelectedGroups() {
			return SelectedGroups;
		}

		public void setSelectedGroups(String selectedGroups) {
			this.SelectedGroups = selectedGroups;
		}

	}


}
