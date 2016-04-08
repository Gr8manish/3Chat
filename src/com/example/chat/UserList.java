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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class UserList extends Activity {

	JSONArray jsonArray = null;
	private ProgressDialog dialogue;
	ListView list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.users_list);

		list = (ListView) findViewById(R.id.user_list);

		String users = getUserList();
	}

	private String getUserList() {
		setDialogue();
		new GetUsers().execute();
		return null;
	}

	private void setDialogue() {
		dialogue = new ProgressDialog(UserList.this);
		dialogue.setTitle("Loading Users...");
		dialogue.setMessage("Please wait");
		dialogue.setCancelable(false);
		dialogue.show();
	}

	private class GetUsers extends AsyncTask<Void, Void, String> implements
			OnItemClickListener {

		String url_ = "http://smartlogicks.16mb.com/chatGcm/get_users.php";
		HttpURLConnection conn;

		@Override
		protected String doInBackground(Void... params) {
			try {
				URL url = new URL(url_);

				conn = (HttpURLConnection) url.openConnection();
				conn.setReadTimeout(15000);
				conn.setConnectTimeout(15000);
				conn.setRequestMethod("POST");
				conn.setDoInput(true);
				conn.setDoOutput(true);

				OutputStream os = conn.getOutputStream();
				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(os, "UTF-8"));
				SharedPreferences userInfo = getSharedPreferences("mypref", 0);
				String urlParameters = "username="
						+ URLEncoder
								.encode(userInfo.getString("username", "Null"),
										"UTF-8");
				writer.write(urlParameters);
				writer.flush();
				writer.close();
				os.close();

				int responseCode = conn.getResponseCode();

				if (responseCode == HttpURLConnection.HTTP_OK) {

					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(conn.getInputStream()));
					StringBuilder stringBuilder = new StringBuilder();
					String line;
					while ((line = bufferedReader.readLine()) != null) {
						stringBuilder.append(line + '\n');
					}

					String jsonString = stringBuilder.toString();
					System.out.println(jsonString);
					jsonArray = new JSONArray(jsonString);

					return "User List Loaded succesfully";

				} else {

					return "Error occured in Registration";

				}

			} catch (MalformedURLException e) {
				e.printStackTrace();
				System.out.println("Exception1");
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Exception2");
			} catch (JSONException e) {
				e.printStackTrace();
				System.out.println("Exception3");
			} finally {
				conn.disconnect();
			}
			return "Null";
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			System.out.println(jsonArray.length());
			String[] names = new String[jsonArray.length()];
			for (int i = 0; i < jsonArray.length(); i++) {
				try {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					String username = jsonObject.getString("username");
					names[i] = username;
					System.out.println(username);
				} catch (JSONException e) {
					e.printStackTrace();
					System.out.println("Json array exception");
				}

			}

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					UserList.this, android.R.layout.simple_list_item_1,
					android.R.id.text1, names);
			list.setAdapter(adapter);
			list.setOnItemClickListener(this);

			dialogue.dismiss();

			Toast.makeText(UserList.this, result, Toast.LENGTH_LONG).show();
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			String item = list.getItemAtPosition(position).toString().trim();
			System.out.println(item + " clicked");
			Intent i = new Intent(UserList.this, Chatting.class);
			i.putExtra("toUser", item);
			startActivity(i);
		}

	}

}
