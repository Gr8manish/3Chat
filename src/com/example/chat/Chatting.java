package com.example.chat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Chatting extends ListActivity {

	private ArrayList<User_object> items_to_be_added = new ArrayList<User_object>();
	private Runnable sendMsg, receiveMsg;
	private Adapter_item myadapter;
	Button send;
	EditText etMsg;
	String toUser = null, MyUsername = null, senderusername = null,
			senderMsg = null;
	String message = "";
	String SENDER_ID = "976537138421";
	String API_KEY = "YOUR_API_KEY";
	private ProgressDialog dialogue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chatting);

		Intent i = getIntent();
		toUser = i.getStringExtra("toUser");

		send = (Button) findViewById(R.id.bSend);
		etMsg = (EditText) findViewById(R.id.etMsg);

		myadapter = new Adapter_item(this, R.layout.list_item,
				items_to_be_added);

		setListAdapter(myadapter);

		sendMsg = new Runnable() {
			public void run() {
				handlerSend.sendEmptyMessage(0);
			}
		};
		receiveMsg = new Runnable() {
			public void run() {
				handlerReceived.sendEmptyMessage(0);
			}
		};

		send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Thread thread = new Thread(null, sendMsg, "MsgSendThread");
				thread.start();

			}
		});

		registerReceiver(broadCast_Receiver, new IntentFilter("NEW_CHAT_MSG"));

		SharedPreferences userInfo = getSharedPreferences("mypref", 0);
		MyUsername = userInfo.getString("username", "Noname");
		addPrevChatMsgs();
	}

	private void addPrevChatMsgs() {
		Database db = new Database(getApplicationContext());
		items_to_be_added = db.getSpecificUserMsgs(toUser);
		db.close();
		myadapter = new Adapter_item(Chatting.this, R.layout.list_item,
				items_to_be_added);
		setListAdapter(myadapter);
	}

	BroadcastReceiver broadCast_Receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle b = intent.getExtras();
			senderMsg = b.getString("msg");
			senderusername = b.getString("username");

			if (senderusername.equals(toUser)) {
				Thread thread = new Thread(null, receiveMsg,
						"MsgReceivedThread");
				thread.start();
				Toast.makeText(Chatting.this,
						senderusername + " " + senderMsg + " received",
						Toast.LENGTH_LONG).show();
			}
		}
	};

	private Handler handlerSend = new Handler() {
		public void handleMessage(Message msg) {

			items_to_be_added.add(new User_object("You", etMsg.getText()
					.toString()));
			message = etMsg.getText().toString();
			myadapter = new Adapter_item(Chatting.this, R.layout.list_item,
					items_to_be_added);
			setListAdapter(myadapter);
			etMsg.setText("");

			new SendMsg().execute();
		}
	};

	private Handler handlerReceived = new Handler() {
		public void handleMessage(Message msg) {

			items_to_be_added.add(new User_object(senderusername, senderMsg));
			/*
			 * Database db = new Database(getApplicationContext());
			 * db.addMsg("You", senderusername, senderMsg); db.close();
			 */

			myadapter = new Adapter_item(Chatting.this, R.layout.list_item,
					items_to_be_added);
			setListAdapter(myadapter);
		}
	};

	private class SendMsg extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			/*
			 * dialogue = new ProgressDialog(Chatting.this);
			 * dialogue.setTitle("Sending msg...");
			 * dialogue.setMessage("Please wait");
			 * dialogue.setCancelable(false); dialogue.show();
			 */
		}

		@Override
		protected Void doInBackground(Void... params) {
			// sending msg
			URL url;
			try {
				url = new URL("http://smartlogicks.16mb.com/chatGcm/gcm.php");
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
						+ URLEncoder.encode(toUser.trim(), "UTF-8")
						+ "&message=" + URLEncoder.encode(message, "UTF-8")
						+ "&apiKey=" + URLEncoder.encode(API_KEY, "UTF-8")
						+ "&MyUsername="
						+ URLEncoder.encode(MyUsername, "UTF-8");
				System.out.println("urlParameters=" + urlParameters);
				writer.write(urlParameters);
				writer.flush();
				writer.close();
				os.close();

				int responseCode = conn.getResponseCode();
				if (responseCode == HttpURLConnection.HTTP_OK) {
					System.out.println("Message send sucessfully");

					Database db = new Database(getApplicationContext());
					System.out.println("addMsg=" + toUser.trim() + " You "
							+ message);
					db.addMsg(toUser.trim(), "You", message);
					db.close();

					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(conn.getInputStream()));
					StringBuilder stringBuilder = new StringBuilder();
					String line;
					while ((line = bufferedReader.readLine()) != null) {
						stringBuilder.append(line + '\n');
					}

					String jsonString = stringBuilder.toString();
					System.out.println(jsonString);
				} else {
					System.out.println("Error in connection");
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;

		}

		@Override
		protected void onPostExecute(Void params) {
			// dialogue.dismiss();
			etMsg.setText("");
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(broadCast_Receiver);
	}
}
