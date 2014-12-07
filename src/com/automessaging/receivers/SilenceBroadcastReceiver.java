package com.automessaging.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.provider.CallLog;
import android.util.Log;
import android.widget.Toast;


public class SilenceBroadcastReceiver extends BroadcastReceiver{
	
	@Override
    public void onReceive(Context context, Intent intent) {
       
		AudioManager audio = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		
		Log.v(this.getClass().getName(), ":::::::::check.... Silence broadcast Receiver called::::::::::  ");
		
		//Toast.makeText(context, "Check..receiver silence...", Toast.LENGTH_SHORT).show();
       
       audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
       
       
    }
}

