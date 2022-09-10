package com.hsinyu.object;



public class Variable {
	String name;
	String type;
	
	public Variable() {
		this.name =	null;
		this.type = null;
	}
	
	public Variable(String name, String type) {
		this.name = name;
		this.type = type;
	}
	
	public void setName(String in) {
		this.name = in;
	}
	
	public void setType(String in) {
		this.type = in;
	}
	

	
	public String getName() {
		return this.name;
	}
	
	public String getType() {
		return this.type;
	}
	

}
