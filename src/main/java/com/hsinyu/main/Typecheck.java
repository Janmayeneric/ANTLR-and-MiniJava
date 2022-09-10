package com.hsinyu.main;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;

import com.hsinyu.object.CalledMethod;
import com.hsinyu.object.Classdecl;
import com.hsinyu.object.Methoddecl;
import com.hsinyu.object.Program;
import com.hsinyu.object.Variable;

public class Typecheck {
	

	/**
	 * overload is not permitted, so we only store one distinct name of class and variable
	 * it has scope to all global value
	 */
	HashSet<String> names_decl_global;
	
	
	
	/**
	 * same as the above one
	 * this one has the scope for only one class
	 */
	HashSet<String>	names_decl_class;
	HashSet<String> names_decl_method;
	
	/**
	 * it use to store the declaration for each class
	 */
	HashMap<String,HashSet<String>> class_declarations;
	
	Program program;
	/**
	 * we check if any extended name of class is delcared before
	 * later more detail
	 */
	String[] names_extended_class;
	
	String currentclass;
	
	ArrayDeque<CalledMethod> called_methods;
	
	/**
	 * use program object as the "root"
	 * @param program
	 */
	public Typecheck(Program program, MiniJavaListener listener) {
		// checking any declaration duplication
		this.names_decl_global = new HashSet<String>();
		
		// declaration of method in the class
		this.names_decl_class = new HashSet<String>();
		this.names_decl_method = new HashSet<String>();
		
		this.class_declarations = new HashMap<String,HashSet<String>>();
		
		
		this.names_extended_class = listener.getExtendedClassName().toArray(new String[listener.getExtendedClassName().size()]);
		this.program = program;
		
		this.currentclass = null;
		
		this.called_methods = new ArrayDeque<CalledMethod>();
		
	}
	
	public void programTable() {
		System.out.println("In the entire Program:");
		
		
		for(Classdecl c : program.getClassdecls()) {
			this.names_decl_global.add(c.getName());
		}
		
		/**
		 * they store the same collection of array, one is Hashset one is array
		 * when two size are different, it means  some duplication in declaration
		 */
		if(this.names_decl_global.size() < program.getClassdecls().length) {
			System.out.println("Multiple Declaration of the classes");
		}
		
		
		/**
		 * reference call is a.b() method
		 * it might call method from other class
		 * check if the reference call is valid
		 * if we can find the method in certain class
		 */
		if(this.called_methods.size() > 0) {
			
			
			while(!called_methods.isEmpty()) {
				this.findReferenceCall(called_methods.pop());
			}
		}
		
		Classdecl[] classdecls = program.getClassdecls();
		for(int i = 0; i < classdecls.length ; i ++) {
			if(classdecls[i].getExteneded_class() != null) {
				for(Classdecl c : classdecls) {
					
					/**
					 * check if the class is the extend class from other class
					 * because when it extends other class
					 * it can use the variable and method from other class
					 */
					if(c.getName().equals(classdecls[i].getExteneded_class())) {
						classdecls[i].setExtendedClass(c);
						break;
					}
				}
			}
			
			// call and print the class information
			classTable(classdecls[i]);
			
		}
		
		/**
		 * check the main class, if the main class have the call method from the right class 
		 */
		for(int i = 0; i < program.getMainclass().getVars().length; i++) {
			if(!this.names_decl_global.contains(program.getMainclass().getVars()[i])) {
				this.printOutBoundError(program.getMainclass().getVars()[i]);
			}
		}
		
		
		
		
		
	}
	
	public void classTable(Classdecl classdecl) {
		
		this.currentclass = classdecl.getName();
		
		/**
		 * this hash set is only use to check the scope under same class, so initiate every time we go into one class
		 */
		this.names_decl_class = new HashSet<String>();
		this.names_decl_class.add("this"); // every class can use this variable, so save it
		
		System.out.println("\tClass " +  classdecl.getName());
		
		
		
		if(classdecl.getExteneded_class() != null) {
			System.out.println(" extend " + classdecl.getExteneded_class());
		}
		
		Variable[] var_decls = classdecl.getVars_decl();
		System.out.println("\t\tDeclared Variables:");
		for(int i = 0; i < var_decls.length ; i ++) {
			System.out.println("\t\t\tType:" +var_decls[i].getType() + "\tName:" + var_decls[i].getName());
			
			// check and store its into the hash set for duplication checking
			this.isDeclared(var_decls[i].getName(), this.names_decl_class, true,true);
			this.isDeclared(var_decls[i].getName(), this.names_decl_global,false,true);
		}
		
		/**
		 * inherited class share the methods and variable declaration from its extends class
		 */
		if(classdecl.getExteneded_class() != null) {
			for(Methoddecl m : classdecl.getExtendedClass().getMethods_decl()) {
				this.isDeclared(m.getName(), this.names_decl_class, true,false);
			}
			
			for(Variable v :classdecl.getExtendedClass().getVars_decl()) {
				this.isDeclared(v.getName(), this.names_decl_class, true,false);
			}
		}
		
	
		
		Methoddecl[] methoddecls = classdecl.getMethods_decl();
		System.out.println("\t\tDeclared Methods:");
		for(int i = 0; i < methoddecls.length ; i ++) {
			
			methodTable(methoddecls[i],classdecl.getExteneded_class() != null );
		}
		
		/**
		 * check if all the variable, to make sure they are declared before using
		 */
		for(int i=0;i<classdecl.getVars().length;i++) {
			if(!this.names_decl_global.contains(classdecl.getVars()[i])) {
				if(!this.names_decl_class.contains(classdecl.getVars()[i])) {
					this.printOutBoundError(classdecl.getVars()[i]);
				}
			}
		}
		
		this.class_declarations.put(classdecl.getName(), this.names_decl_class);
		
	}
	
	public void methodTable(Methoddecl methoddecl,boolean isExtend) {
		this.names_decl_method = new HashSet<String>();
		System.out.println("\t\t\t"+"Type: " +methoddecl.getType()+"\tName: " +methoddecl.getName());
		if(!isExtend) {
			this.isDeclared(methoddecl.getName(), this.names_decl_class, true,true);
		}
		
		this.isDeclared(methoddecl.getName(), this.names_decl_method, true,true);
		this.isDeclared(methoddecl.getName(), this.names_decl_global,false ,true);
		
		System.out.println("\t\t\t\t"+ "Parameter:	");
		for(Variable p: methoddecl.getParam()) {
			this.isDeclared(p.getName(), this.names_decl_class, false,true);
			this.isDeclared(p.getName(), this.names_decl_method, true,true);
			this.isDeclared(p.getName(), this.names_decl_global,false,true);
			System.out.println("\t\t\t\t\tType: " + p.getType() + "\tName: " +p.getName());
		}
		System.out.println("\t\t\t\t"+ "Declared Variable:	");
		for(int i = 0; i < methoddecl.getVars_decl().length ; i ++) {
			this.isDeclared(methoddecl.getVars_decl()[i].getName(), this.names_decl_class, false,true);
			this.isDeclared(methoddecl.getVars_decl()[i].getName(), this.names_decl_method, true,true);
			this.isDeclared(methoddecl.getVars_decl()[i].getName(), this.names_decl_global,false,true);
			System.out.println("\t\t\t\t\tType: " + methoddecl.getVars_decl()[i].getType() + "\tName: " + methoddecl.getVars_decl()[i].getName());
		}
		
		
		/**
		 * if there is any use of calling method, store all premix and wait for processing later
		 * only valid use of this is in the first, and this inidicate the current class
		 * so only assign the current class name to it if the first premix is this
		 * it check from outside the class, so need to assign something
		 */
		if(methoddecl.getCalledVars().length > 0) {
			for(CalledMethod c : methoddecl.getCalledVars()) {
				String[] references = c.getReferences();
				if(references[0].equals("this")) {
					references[0] = this.currentclass;
					c.setReferences(references);
				}
				this.called_methods.push(c);
			}
		}
		for(int i=0;i<methoddecl.getVars().length;i++) {
			if(!this.names_decl_global.contains(methoddecl.getVars()[i])) {
				if(!this.names_decl_class.contains(methoddecl.getVars()[i])) {
					if(!this.names_decl_method.contains(methoddecl.getVars()[i])) {
						this.printOutBoundError(methoddecl.getVars()[i]);
					}
				}
			}
		}
		
		
		/**
		 * push the method calling information into the stack
		 */
		CalledMethod[] callees = methoddecl.getCalledVars();
		
		for(CalledMethod c: callees) {
			this.called_methods.push(c);
		}
		
		
	}
	
	
	/**
	 * it add the declared name into the name of declared variable or class name or method name
	 * two scenario
	 * 		- if it in hashmap, return error
	 * 		- if not, add into the set depend on user's demand
	 * 		- if yes, reject and only return error printing
	 * @param checkString
	 */
	public boolean isDeclared(String checkString, HashSet<String> checkSet , boolean add, boolean need_report) {
		if(checkSet.contains(checkString)) {
			System.out.println("--------Error: overloading behavior------");
			System.out.println("name " + checkString + " is declared multiple times");
			return true;
		}else if(add){
			checkSet.add(checkString);
			return false;
		}
		return false;
	}
	
	public void printOutBoundError(String var_name) {
		System.out.println("--------Error: Declaration error------");
		System.out.println("cannot find declaration for " + var_name);
	}
	
	/**
	 * it use for searching the reference called
	 * like a.b()
	 * if b() is in a class
	 */
	public void findReferenceCall(CalledMethod called_var) {
		String classname = this.findFinalClass(called_var.getReferences());
		
		if(this.findType(classname, null, called_var.getName()).equals("")) {
			System.out.println("cannot find method" + called_var.getName());
		}
		
		
	}
	
	/**
	 * it search from the first class, to find out which class they represent 
	 * a.b.c.d.f.g() = > find a.b.c.d.f represent which class
	 * @param ref
	 * @return
	 */
	public String findFinalClass(String[] ref) {

		String currentClass = ref[0];
		for(int i = 1; i <  ref.length  ; i++) {
			currentClass = this.findType(currentClass, null, ref[i]);
			if(currentClass.equals("")) {
				return "";
			}
		}
		return currentClass;
		
	}
	
	/**
	 * More findType explanation is from the visitor interface
	 * @param c
	 * @param m
	 * @param name
	 * @return
	 */
	public String findType(String c, String m, String name) {
    	
    	if(m == null) {
    		if(c==null) {
    			//	we only find for class name
    			for(Classdecl n: this.program.getClassdecls()) {
    				if(n.getName().equals(name)) {
    					return n.getName();
    				}
    			}
    		}else {
    			//	we find for variables and methods declared by class
    			for(Classdecl n: this.program.getClassdecls()) {
    				if(n.getName().equals(c)) {
    					for(Methoddecl v: n.getMethods_decl()) {
    						if(v.getName().equals(name)) {
    							return v.getType();
    						}
    					}
    					for(Variable p: n.getVars_decl()) {
    						if(p.getName().equals(name)) {
    							return p.getType();
    						}
    					}
    				}
    			}
    		}
    	}else {
    		//	we find for variables under the specific class and name
    		for(Classdecl n: this.program.getClassdecls()) {
    			if(n.getName().equals(c)) {
    				for(Methoddecl v: n.getMethods_decl()) {
    					if(v.getName().equals(m)) {
    						for(Variable u : v.getParam()) {
    							if(u.getName().equals(name)) {
    								return u.getType();
    							}
    						}
    						for(Variable u: v.getVars_decl() ) {
    							if(u.getName().equals(name)) {
    								return u.getType();
    							}
    						}
    						for(Variable p: n.getVars_decl()) {
    							if(p.getName().equals(name)) {
        							return p.getType();
        						}
    						}
    					}
    				}
    			}
    		}
    	}
    	return "";
    }
	
	
}
