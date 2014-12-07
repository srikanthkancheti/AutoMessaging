package com.automessaging;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {

	private static int SPLASH_TIME_OUT = 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		ActionBar actionBar = getActionBar();
		actionBar.hide();

		new Handler().postDelayed(new Runnable() {

			/*
			 * Show splash screen with a timer. this will be useful when you
			 * want to show case your app logo
			 */

			@Override
			public void run() {

				Intent i = new Intent(SplashActivity.this, HomeActivity.class);
				startActivity(i);
				finish();

			}

		}, SPLASH_TIME_OUT);

	}

}
