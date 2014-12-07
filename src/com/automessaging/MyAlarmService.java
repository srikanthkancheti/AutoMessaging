package com.automessaging;

import java.util.Date;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class MyAlarmService extends Service {

	// ProfilesDatabaseHelper myDbhelper = new
	// ProfilesDatabaseHelper(MyAlarmService.this);
	@Override
	public void onCreate() {
		Toast.makeText(getApplicationContext(), "Service Created", 1).show();
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) {

		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void onDestroy() {
		Toast.makeText(getApplicationContext(), "Service Destroy", 1).show();
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub

		Toast.makeText(getApplicationContext(), "Service Running ", 1).show();
		return super.onStartCommand(intent, flags, startId);
	}

	//
	// @Override
	// public void onStart(Intent intent, int startId) {
	// super.onStart(intent, startId);
	// Toast.makeText(this, "MyAlarmService.onStart()",
	// Toast.LENGTH_LONG).show();
	// }

	private void stopAndCurrentTimeDifference(Date currentTime, Date stopTime) {
		// TODO Auto-generated method stub

	}

	private void startAndCurrentTimeDifference(Date currentTime, Date startTime) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onUnbind(Intent intent) {
		Toast.makeText(this, "MyAlarmService.onUnbind()", Toast.LENGTH_LONG)
				.show();
		return super.onUnbind(intent);
	}

}
