package com.example.chat;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class Register extends Activity {

	TextView title;
	EditText username;
	Button submit;
	GoogleCloudMessaging gcm = null;
	String gcm_reg_id;
	String SENDER_ID = "976537138421";
	String API_KEY = "YOUR_API_KEY";
	private ProgressDialog dialogue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		Typeface font = Typeface.createFromAsset(getAssets(), "Manish.ttf");
		title = (TextView) findViewById(R.id.TvRegistertitle);
		username = (EditText) findViewById(R.id.etUsername);
		submit = (Button) findViewById(R.id.BSubmit);
		title.setTypeface(font);
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				register();
			}
		});
		SharedPreferences userInfo = getSharedPreferences("mypref", 0);
		if(userInfo.getBoolean("Login", false)){
			Intent i = new Intent(Register.this,UserList.class);
			startActivity(i);
			finish();
		}
		
	}

	protected void register() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			System.out.println("Here");
			new RegisterGcm().execute();
		} else {
			Toast.makeText(getBaseContext(), "No network Connection....",
					Toast.LENGTH_LONG).show();
		}
	}

	private class RegisterGcm extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialogue = new ProgressDialog(Register.this);
			dialogue.setTitle("Registering...");
			dialogue.setMessage("Please wait");
			dialogue.setCancelable(false);
			dialogue.show();
		}

		@Override
		protected String doInBackground(Void... params) {

			String msg = "";
			if (gcm == null) {
				gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
			}
			try {
				gcm_reg_id = gcm.register(SENDER_ID);
				msg = "Gcm registration ID=" + gcm_reg_id;
			} catch (IOException e) {
				e.printStackTrace();
				msg = "GCM_ERROR";
			}

			return msg;
		}

		@Override
		protected void onPostExecute(String msg) {
			if (msg.equals("GCM_ERROR")) {
				System.out.println("Error occured while GCM registration");
				dialogue.dismiss();
			} else {
				new RegisterUser().execute();
			}
		}

	}

	private class RegisterUser extends AsyncTask<Void, Void, String> {

		String url_ = "http://smartlogicks.16mb.com/chatGcm/register.php";

		@Override
		protected String doInBackground(Void... params) {
			try {
				URL url = new URL(url_);

				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setReadTimeout(15000);
				conn.setConnectTimeout(15000);
				conn.setRequestMethod("POST");
				conn.setDoInput(true);
				conn.setDoOutput(true);

				OutputStream os = conn.getOutputStream();
				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(os, "UTF-8"));
				String urlParameters = "username="
						+ URLEncoder.encode(username.getText().toString(),
								"UTF-8") + "&gcm_reg_id="
						+ URLEncoder.encode(gcm_reg_id, "UTF-8");
				writer.write(urlParameters);
				writer.flush();
				writer.close();
				os.close();

				int responseCode = conn.getResponseCode();

				if (responseCode == HttpURLConnection.HTTP_OK) {


					SharedPreferences userInfo = getSharedPreferences("mypref", 0);
					SharedPreferences.Editor editer = userInfo.edit();
					editer.putBoolean("Login", true);
					editer.putString("username", username.getText().toString());
					editer.commit();
					return "User have been Registered sucessfully";

				} else {

					return "Error occured in Registration";

				}

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return "Null";
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			dialogue.dismiss();
			SharedPreferences userInfo = getSharedPreferences("mypref", 0);
			Intent i = new Intent(Register.this,UserList.class);
			startActivity(i);
			Toast.makeText(Register.this, result+" "+userInfo.getBoolean("Login", false), Toast.LENGTH_LONG).show();
			finish();
		}

	}

}
