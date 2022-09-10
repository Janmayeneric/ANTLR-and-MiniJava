package com.hsinyu.object;

/**
 * it used to describe the relationship that
 * a method from a class called by another class
 * 
 * @author 10130
 *
 */
public class CalledMethod {
	
	String name;
	String [] references;
	String[] params;
	
	public CalledMethod() {
		
	}
	
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setReferences(String[] references) {
		this.references = references;
	}
	
	public void setParams(String[] params) {
		this.params = params;
	}
	
	
	public String getName() {
		return this.name;
	}
	
	public String[] getParams() {	
		return this.params;
	}
	public String[] getReferences() {
		return this.references;
	}
	
}
