package com.example.chat;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;

public class Gcm_Broadcast_Receiver extends WakefulBroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {

		ComponentName comp = new ComponentName(context.getPackageName(),
				Gcm_Intent_Service.class.getName());

		startWakefulService(context, (intent.setComponent(comp)));
		setResultCode(Activity.RESULT_OK);

		System.out.println("Inside Gcm_Broadcast_Receiver");

		Bundle extras = intent.getExtras();
		Intent i = new Intent("NEW_CHAT_MSG");
		i.putExtra("msg", extras.getString("message"));
		i.putExtra("username", extras.getString("username"));
		
		//sending broadcast for chatting activity
		context.sendBroadcast(i);
		Database db = new Database(context);
		db.addMsg("You", extras.getString("username"), extras.getString("message"));
		db.close();
	}
}
