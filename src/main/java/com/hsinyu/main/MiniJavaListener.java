package com.hsinyu.main;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;

import com.hsinyu.minijava.MiniJavaGrammarBaseListener;
import com.hsinyu.minijava.MiniJavaGrammarParser;
import com.hsinyu.minijava.MiniJavaGrammarParser.ClassdeclContext;
import com.hsinyu.minijava.MiniJavaGrammarParser.ExprContext;
import com.hsinyu.minijava.MiniJavaGrammarParser.ExprlistContext;
import com.hsinyu.minijava.MiniJavaGrammarParser.ExprrestContext;
import com.hsinyu.minijava.MiniJavaGrammarParser.FormallistContext;
import com.hsinyu.minijava.MiniJavaGrammarParser.FormalrestContext;
import com.hsinyu.minijava.MiniJavaGrammarParser.MainclassContext;
import com.hsinyu.minijava.MiniJavaGrammarParser.MethoddeclContext;
import com.hsinyu.minijava.MiniJavaGrammarParser.OpContext;
import com.hsinyu.minijava.MiniJavaGrammarParser.ProgramContext;
import com.hsinyu.minijava.MiniJavaGrammarParser.StatementContext;
import com.hsinyu.minijava.MiniJavaGrammarParser.TypeContext;
import com.hsinyu.minijava.MiniJavaGrammarParser.VardeclContext;
import com.hsinyu.object.*;
public class MiniJavaListener extends MiniJavaGrammarBaseListener {
	
	
	Mainclass mainclass;
	Program program;
	Classdecl classdecl;
	Methoddecl methoddecl;
	
	ArrayList<Classdecl> classdecls;
	ArrayList<Methoddecl> methoddecls;
	ArrayList<String> extended_class_names;
	ArrayList<CalledMethod> called_vars;
	
	HashSet<String> classNames;
	HashSet<String> methodNames;
	HashSet<String> varNames;
	
	ArrayDeque<Variable> var_decls;
	ArrayDeque<Integer> ps_var_decls;
	ArrayDeque<String> vars;
	ArrayDeque<Integer> ps_var;
	
	
	MiniJavaGrammarParser.ProgramContext end;

	
    
    public MiniJavaListener() {
    	
    	this.program = null;
    	this.mainclass = null;
    	
    	/**
    	 * two application is used to construct the stack for the listener
    	 * this stack is for the declared variables
    	 */
    	this.var_decls = new ArrayDeque<Variable>();
    	this.ps_var_decls = new ArrayDeque<Integer>();
    	
    	// stack for the variable, not declare, but using variable
    	this.vars = new ArrayDeque<String>();
    	this.ps_var = new ArrayDeque<Integer>();
    	
    	/**
    	 * to store the list of the method , variable or etc that belong to the nodes(class node, method node)
    	 */
    	this.methoddecls = new ArrayList<Methoddecl>();
    	this.called_vars = new ArrayList<CalledMethod>();
    	this.classdecls = new ArrayList<Classdecl>();
    	
    	// check if the extended class name is existed at the end of listening
    	this.extended_class_names = new ArrayList<String>();
    	
    	
    	
    	// return the end point for visitor to start 
    	this.end = null;
    }
    @Override
    public void enterProgram(MiniJavaGrammarParser.ProgramContext ctx) {
    	this.classdecls = new ArrayList<Classdecl>();
    	this.program = new Program();
    	this.classNames = new HashSet<String>();
    }
    
    
    
    @Override
    public void exitProgram(MiniJavaGrammarParser.ProgramContext ctx) {
    	this.program = new Program();
    	this.program.setMainclass(this.mainclass);
    	this.program.setClassdecls(this.classdecls.toArray(new Classdecl[classdecls.size()]));
    	this.end = ctx;
    }
    
    

    @Override
    public void enterMainclass(MiniJavaGrammarParser.MainclassContext ctx) {
    	this.mainclass = new Mainclass();
    }

    
    @Override
    public void exitMainclass(MiniJavaGrammarParser.MainclassContext ctx) {
    	this.mainclass.setName(ctx.ID().get(0).getText());
    	this.mainclass.setArgsname(ctx.ID().get(1).getText());
    	
    	
    }
    
    @Override
    public void enterClassdecl(MiniJavaGrammarParser.ClassdeclContext ctx) {
    	this.classdecl = new Classdecl();
    	this.methodNames = new HashSet<String>();
    	this.ps_var_decls.push(this.var_decls.size());
    	this.ps_var.push(this.vars.size());
    	this.methoddecls = new ArrayList<Methoddecl>();
    }

    
    @Override
    public void exitClassdecl(MiniJavaGrammarParser.ClassdeclContext ctx) {
    	classdecl.setName(ctx.ID().get(0).getText());
    	
    	//	for possible extended class name
    	if(ctx.ID().size() > 1) {
    		classdecl.setExteneded_class(ctx.ID(1).getText());
    		this.extended_class_names.add(ctx.ID(1).getText());
    	}
    	
    	this.classdecl.setMethods_decl(this.methoddecls.toArray(new Methoddecl[this.methoddecls.size()]));
    	classdecl.setVars_decl(this.getVarDeclsFromStack());
    	classdecl.setVars(this.getVarsFromStack());
    	
    	
    	
    	this.classdecls.add(classdecl);
    }
    
    @Override
    public void exitVardecl(MiniJavaGrammarParser.VardeclContext ctx) {
    	this.var_decls.push(new Variable(ctx.ID().getText(),ctx.type().getText()));
    }
    
    @Override
    public void enterStatement(MiniJavaGrammarParser.StatementContext ctx) {
    	
    }
    
    @Override
    public void exitStatement(MiniJavaGrammarParser.StatementContext ctx) {
    	if(ctx.ID() != null) {
    	
    		this.vars.push(ctx.ID().getText());
    	}
    	
    }

    
    @Override
    public void enterMethoddecl(MiniJavaGrammarParser.MethoddeclContext ctx) {
    	this.called_vars = new ArrayList<CalledMethod>();
    	this.ps_var_decls.push(this.var_decls.size());
    	this.ps_var.push(this.vars.size());
    	
    	this.varNames = new HashSet<String>();
    	
    	this.methoddecl = new Methoddecl();
    
    }

    
    @Override
    public void exitMethoddecl(MiniJavaGrammarParser.MethoddeclContext ctx) {
    	this.methoddecl.setName(ctx.ID().getText());
    	this.methoddecl.setType(ctx.type().getText());
    	
    	
    	methoddecl.setVars_decl(this.getVarDeclsFromStack());
    	
    	
    	
    	 // take the parameters
    	if(ctx.formallist() != null) {
    		int num_pop =  var_decls.size() - this.ps_var_decls.pop();
    		Variable[] params = new Variable[num_pop];
    		for(int i = 0 ; i < num_pop ; i++) {
    			params[num_pop - 1 - i ] = var_decls.pop();
    		}
    		methoddecl.setParam(params);
    	}
    	
    	// get all variable used in the method
    	methoddecl.setVars(this.getVarsFromStack());
    	
    	
    	
    	methoddecls.add(methoddecl);
    	this.methoddecl = null;
    }
    

    @Override
    public void exitFormallist(MiniJavaGrammarParser.FormallistContext ctx) {
    	this.var_decls.push(new Variable(ctx.ID().getText(),ctx.type().getText()));
    	this.ps_var_decls.push(this.var_decls.size());
    	
    }

    @Override
    public void exitFormalrest(MiniJavaGrammarParser.FormalrestContext ctx) {
    	this.var_decls.push(new Variable(ctx.ID().getText(),ctx.type().getText()));
    }

    

    @Override
    public void enterExpr(MiniJavaGrammarParser.ExprContext ctx) {
    	this.ps_var.push(this.vars.size());
    }

    @Override
    public void exitExpr(MiniJavaGrammarParser.ExprContext ctx) {
    	//	only record the identifier, for scope checking
    	if(ctx.ID() != null) {
    		
    		
    		if(ctx.expr().size() == 0 ) {
    			this.vars.push(ctx.ID().getText());
    		}else {
    			
    			//Expression "." Identifier "(" ( Expression ( "," Expression )* )? ")"
    			CalledMethod called_var = new CalledMethod();
    			called_var.setName(ctx.ID().getText());
    			int num_pop;
    			if(ctx.exprlist() != null) {
    	    		num_pop = this.vars.size() - this.ps_var.pop();
    	    		String[] params = new String[num_pop];
    	    		for(int i = 0  ; i < num_pop ; i ++) {
    	    			params[i] = this.vars.pop();
    	    		}
    	    		called_var.setParams(params);
    	    		
    	    	}
    			
    			// take all expression prior to the identifier 
    			// a.b.c.d.e.f we take a b c d e in sequence
    			num_pop = this.vars.size() - this.ps_var.getFirst() ;
    	    	String[] arr_var = new String[num_pop];
    	    	for(int i = 0 ; i < num_pop ; i++) {
    	    		arr_var[i] = vars.pop();
    	    		
    	    	}
    	    	
    			String[] references = arr_var;
    			called_var.setReferences(references);
    			
    			
    			this.called_vars.add(called_var);
    		}
    	}else if(ctx.THIS() != null) {
    		// this represent to the current class name
    		this.vars.push(ctx.THIS().getText());
    	}
    	
    	
    	
    	ps_var.pop();
    }
    
    @Override
    public void enterExprlist(MiniJavaGrammarParser.ExprlistContext ctx) {
    	this.ps_var.push(this.vars.size());
    }
  
    
    
    
    public Program getProgram() {
    	return this.program;
    }
    
    public ProgramContext getRoot() {
    	return this.end;
    }
    
    public ArrayList<String> getExtendedClassName(){
    	return this.extended_class_names;
    }
    
    
    /**
     * used to take the variable from the stack and transfer them into variable array
     * we have the pointer so it is automatic without any parameters
     * @return
     */
    public Variable[] getVarDeclsFromStack() {
    	int num_pop = var_decls.size() - this.ps_var_decls.pop();
    	Variable[] out = new Variable[num_pop];
    	for(int i = 0 ; i < num_pop ; i++) {
    		out[i] = var_decls.pop();
    	
    	}
    	return out;
    }
    
    public ArrayList<CalledMethod> getCalledMethods() {
    	return this.called_vars;
    }
    
    /**
     * it take the variable name from the stack
     * @return
     */
    public String[] getVarsFromStack() {
    	int num_pop = this.vars.size() - this.ps_var.pop();
    	String[] arr_var = new String[num_pop];
    	for(int i = 0 ; i < num_pop ; i++) {
    		arr_var[i] = vars.pop();
    	}
    	return arr_var;
    }
    
    public void errorReport_duplicate(String location) {
    	System.out.println("--------Error: Multiple declaration------");
    	System.out.println("Error in " + location);
    }
 
}


