package com.hsinyu.object;

public class Program {
	Mainclass mainclass;
	Classdecl[] classdecls;
	
	public Program(){
		mainclass = new Mainclass();
		classdecls = new Classdecl[0];
	}
	
	public void setMainclass(Mainclass mainclass) {
		this.mainclass = mainclass;
	}
	
	public void setClassdecls(Classdecl[] methoddecls) {
		this.classdecls = methoddecls;
	}
	
	public Mainclass getMainclass() {
		return this.mainclass;
	}
	
	public Classdecl[] getClassdecls() {
		return this.classdecls;
	}
}