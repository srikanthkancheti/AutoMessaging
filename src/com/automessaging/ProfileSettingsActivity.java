package com.automessaging;

import android.app.Activity;
import android.os.Bundle;

public class ProfileSettingsActivity extends Activity{
	
	protected void onCreate(Bundle bundle){
		super.onCreate(bundle);
		setContentView(R.layout.activity_profile_settings);
	}
	
	protected void onResume()
	{
	    super.onResume();
	    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}
	
	protected void onPause()
	{
	    super.onPause();
	    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

}
