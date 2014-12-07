package com.automessaging;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;

public class SettingsActivity extends Activity{
	
	protected void onCreate(Bundle bundle){
		super.onCreate(bundle);
		setContentView(R.layout.activity_settings);
		
		ActionBar actionBar = getActionBar();
		actionBar.hide();   
	}

}
