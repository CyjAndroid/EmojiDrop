package com.example.emojidrop;

public class Point {

	private float x;
	
	private float y;
	
	private int location;//left:0  right:1

	public int getLocation() {
		return location;
	}

	public void setLocation(int location) {
		this.location = location;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}
	
	public void setXY(float x,float y){
		this.x = x;
		this.y = y;
	}
}
