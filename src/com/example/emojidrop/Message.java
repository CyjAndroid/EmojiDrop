package com.example.emojidrop;

public class Message {

	private String content;
	private int location;//left:0  right:1
	public String getContent() {
		return content;
	}
	public int getLocation() {
		return location;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public void setLocation(int location) {
		this.location = location;
	}
	
	
	public Message(String content,int location){
		this.content = content;
		this.location = location;
	}
}
