package com.hsinyu.object;


public class Classdecl {
	String name;
	String exteneded_class;
	Variable[] vars_decl;
	Methoddecl[] methods_decl;
	String[] vars;
	CalledMethod[] called_method;
	Classdecl exteneded_classs;
	
	public Classdecl() {
		name = null;
		exteneded_class = null;
		vars_decl = null;
		methods_decl = null;
		this.vars = null;
		this.called_method = new CalledMethod[0];
	}
	
	public void setName(String in) {
		this.name = in;
	}
	
	public Classdecl getExtendedClass() {
		return this.exteneded_classs;
	}
	
	public void setCallee(CalledMethod[] in) {
		this.called_method = in;
	}
	
	public void setExteneded_class(String in) {
		this.exteneded_class = in;
	}
	
	public void setVars_decl(Variable[] in) {
		this.vars_decl = in;
	}
	
	public void setExtendedClass(Classdecl c) {
		this.exteneded_classs = c;
	}
	
	public void setMethods_decl(Methoddecl[] in) {
		this.methods_decl = in;
	}
	
	public void setVars(String[] vars) {
		this.vars = vars;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getExteneded_class() {
		return this.exteneded_class;
	}
	
	public Variable[] getVars_decl() {
		return this.vars_decl;
	}
	
	public Methoddecl[] getMethods_decl() {
		return this.methods_decl;
	}
	
	public String[] getVars() {
		return this.vars;
	}
	
	public CalledMethod[] getCallee() {
		return this.called_method;
	}
	
}