package com.automessaging;

import com.automessaging.schedule.ScheduleMessage;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HomeActivity extends Activity{
	
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_home);
		
		Button btn1 = (Button) findViewById(R.id.button_schedule);
		Button btn2 = (Button) findViewById(R.id.button_auto);
		
		ActionBar actionBar = getActionBar();
		actionBar.hide();
		
		btn1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(HomeActivity.this, ScheduleMessage.class);
				startActivity(intent);
				//finish();
			}
		});
		
		btn2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent inte = new Intent(HomeActivity.this, ProfileListActivity.class);
				startActivity(inte);
				//finish();
			}
		});
	}
   
	
}
