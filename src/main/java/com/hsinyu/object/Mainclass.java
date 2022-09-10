package com.hsinyu.object;

public class Mainclass {
	String name;
	String args_name;
	String[] vars;
	CalledMethod[] called_vars;
	
	public Mainclass() {
		this.vars = new String[0];
		called_vars = new CalledMethod[0];
	}
	
	public void setCalledVars(CalledMethod[] called_vars) {
		this.called_vars = called_vars;
	}
	
	public void setName(String in) {
		this.name = in;
	}
	
	public void setArgsname(String in) {
		this.args_name = in;
	}
	
	public void setVars(String[] vars) {
		this.vars = vars;
	}
	
	public String getArgsname() {
		return this.args_name;
	}
	
	public String[] getVars() {
		return this.vars;
	}
	public CalledMethod[] getCalledVars() {
		return this.called_vars;
	}
	
	
	
}