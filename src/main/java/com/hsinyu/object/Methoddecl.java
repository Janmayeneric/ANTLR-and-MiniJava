package com.hsinyu.object;

public class Methoddecl {
	String name;
	String type;
	Variable[] vars_decl;
	String[] vars;
	CalledMethod[] called_vars;
	Variable[] param;
	
	
	public Methoddecl() {
		called_vars = new CalledMethod[0];
		param = new Variable[0];
	}
	
	public void setVars(String[] vars) {
		this.vars = vars;
	}

	public void setCalledVars(CalledMethod[] called_vars) {
		this.called_vars = called_vars;
	}
	
	public void setParam(Variable[] param) {
		this.param = param;
	}
	
	public void setName(String in) {
		this.name = in;
	}
	
	public void setType(String in) {
		this.type = in;
	}
	
	public void setVars_decl(Variable[] in) {
		this.vars_decl = in;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Variable[] getVars_decl() {
		return this.vars_decl;
	}
	
	public String getType() {
		return this.type;
	}
	

	
	public String[] getVars() {
		return this.vars;
	}
	
	public Variable[] getParam() {
		return this.param;
	}
	
	public CalledMethod[] getCalledVars() {
		return this.called_vars;
	}
	
	
}
