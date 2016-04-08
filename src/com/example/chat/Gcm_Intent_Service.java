package com.example.chat;

import android.app.IntentService;
import android.content.Intent;

public class Gcm_Intent_Service extends IntentService {

	public Gcm_Intent_Service() {
		super("Gcm_Intent_Service");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		System.out.println("inside Gcm_Intent_Service");
	}

}
