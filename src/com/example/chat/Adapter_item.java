package com.example.chat;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Adapter_item extends ArrayAdapter<User_object> {

	private ArrayList<User_object> items_to_be_added;

	public Adapter_item(Context context, int textViewResourceId,
			ArrayList<User_object> objects) {
		super(context, textViewResourceId, objects);
		this.items_to_be_added = objects;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.list_item, null);
			holder = new ViewHolder();
			holder.tvUsername = (TextView) row.findViewById(R.id.tvUsername);
			holder.tvMsg = (TextView) row.findViewById(R.id.tvMsg);
			holder.LLrow = (LinearLayout) row.findViewById(R.id.LLrow);

			row.setTag(holder);

	
		} else {
			holder = (ViewHolder) row.getTag();
		}

		User_object i = items_to_be_added.get(position);

		if (i != null) {

			if (holder.tvUsername != null) {
				if (!i.getUsername().equals("You")) {
					holder.LLrow.setGravity(Gravity.RIGHT);
					holder.tvMsg.setBackgroundColor(Color.GREEN);
				}
				holder.tvUsername.setText(i.getUsername());
			}
			if (holder.tvMsg != null) {
				holder.tvMsg.setText(i.getmessage());
			}
		}

		return row;

	}

	static class ViewHolder {
		public TextView tvUsername;
		public TextView tvMsg;
		public LinearLayout LLrow;
	}

}
