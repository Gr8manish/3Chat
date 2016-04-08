package com.example.chat;

public class User_object {
	private String Username;
	private String message;

	public User_object() {

	}

	public User_object(String user, String msg) {
		this.Username = user;
		this.message = msg;
	}

	public String getUsername() {
		return Username;
	}

	public void setUsername(String user) {
		this.Username = user;
	}

	public String getmessage() {
		return message;
	}

	public void setmessage(String msg) {
		this.message = msg;
	}

}
