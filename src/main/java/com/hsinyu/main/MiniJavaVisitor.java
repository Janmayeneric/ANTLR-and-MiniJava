package com.hsinyu.main;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import org.antlr.v4.codegen.model.chunk.ThisRulePropertyRef_start;

import com.hsinyu.minijava.MiniJavaGrammarBaseVisitor;
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
import com.hsinyu.object.Classdecl;
import com.hsinyu.object.Methoddecl;
import com.hsinyu.object.Program;
import com.hsinyu.object.Variable;

public class MiniJavaVisitor extends MiniJavaGrammarBaseVisitor<String> {

	
	
	Classdecl currentClass;
	Methoddecl currentMethod;
	Program program;
	
    MiniJavaVisitor(Program program) {
    	this.program = program;
    	this.currentMethod = null;
    }
    
      
    /**
     * <T> indicate we must return something
     * but it is not important what we output here
     * just indicate every method here a void method
     */
    @Override
    public String visitProgram(MiniJavaGrammarParser.ProgramContext ctx) {
    	this.visitMainclass(ctx.mainclass());
    	for(int i = 0; i < ctx.classdecl().size() ; i++){
            this.visitClassdecl(ctx.classdecl(i));
        }
        return null;
    }


    @Override
    public String visitMainclass(MiniJavaGrammarParser.MainclassContext ctx) {
    	this.visitStatement(ctx.statement());
        return null;
    }

    @Override
    public String visitClassdecl(MiniJavaGrammarParser.ClassdeclContext ctx) {
    	this.currentClass = new Classdecl();
    	this.currentClass.setName(ctx.ID(0).getText());

    	if(ctx.EXTENDS() != null) {
        	this.currentClass.setExteneded_class(ctx.ID(1).getText());
        }
    	if(ctx.vardecl().size() > 0) {
    		StringBuilder sb = new StringBuilder();
    		for(int i = 0 ; i<ctx.vardecl().size(); i++ ) {
    			sb.append(this.visitVardecl(ctx.vardecl(i)));
    			sb.append(" ");
    		}
    		this.currentClass.setVars_decl(this.stringsToVariables(sb.toString()));
    	}
        if(ctx.methoddecl().size() > 0) {
        	Methoddecl[] methods = new Methoddecl[ctx.methoddecl().size()];
        	for(int i = 0 ; i < ctx.methoddecl().size(); i++) {
        		this.visitMethoddecl(ctx.methoddecl(i));
        		methods[i] = this.currentMethod;
        	}
        	this.currentClass.setMethods_decl(methods);
        }
        
        
        
        
        return null;
    }

    @Override
    public String visitMethoddecl(MiniJavaGrammarParser.MethoddeclContext ctx) {
    
  
    	this.currentMethod = new Methoddecl();
    	
    	this.currentMethod.setName(ctx.ID().getText());
    	
    	if(ctx.formallist() != null){
    		this.currentMethod.setParam(this.stringsToVariables(this.visitFormallist(ctx.formallist())));
    	}
    	
    	if(ctx.vardecl().size() > 0) {
    		
    		StringBuilder sb = new StringBuilder();
    		for(int i = 0 ; i < ctx.vardecl().size(); i++) {
    			sb.append(this.visitVardecl(ctx.vardecl(i)));
    			sb.append(" ");
    		}
    		
    		this.currentMethod.setVars_decl(this.stringsToVariables(sb.toString()));
    	}
    	
    	
    	
    	if(ctx.statement().size() > 0) {
    		for(int i= 0; i <ctx.statement().size(); i++) {
    			this.visitStatement(ctx.statement(i));
    		}
    	}
    	
    	
    	return null;
    	
    	
    }	
    @Override
    public String visitVardecl(MiniJavaGrammarParser.VardeclContext ctx) {
    	return ctx.type().getText() + ","+ ctx.ID().getText();
    }
    	
    
    
    @Override
    public String visitFormallist(MiniJavaGrammarParser.FormallistContext ctx) {
    	StringBuilder sb = new StringBuilder(ctx.type().getText());
    	sb.append("," +ctx.ID().getText());
    	if(ctx.formalrest().size() > 0) {
    		for(int i = 0 ; i < ctx.formalrest().size() ; i++) {
    			sb.append(" ");
    			sb.append(this.visitFormalrest(ctx.formalrest(i)));
    		}
    	}
    	return sb.toString();
    }
    
    @Override
    public String visitFormalrest(MiniJavaGrammarParser.FormalrestContext ctx) {
    	return ctx.type().getText()+ "," + ctx.ID().getText();
    }

    @Override
    public String visitStatement(MiniJavaGrammarParser.StatementContext ctx) {
        
    	// "if" "(" Expression ")" Statement "else" Statement
    	if(ctx.IF() != null) {
        	if(!this.exprCheck("boolean", this.visitExpr(ctx.expr(0)), ctx.expr(0), true)) {
        		return null;
        	}
        	this.visitStatement(ctx.statement(0));
        	this.visitStatement(ctx.statement(1));
        }
    	
    	//"while" "(" Expression ")" Statement
    	if(ctx.WHILE() != null) {
    		if(!this.exprCheck("boolean", this.visitExpr(ctx.expr(0)), ctx.expr(0), true)) {
        		return null;
        	}
    		this.visitStatement(ctx.statement(0));
    	}
    	
    	// "System.out.println" "(" Expression ")" ";"
    	if(ctx.SYSTEMOUT() != null) {
    		if(!this.exprCheck("int", this.visitExpr(ctx.expr(0)), ctx.expr(0), true)) {
        		return null;
        	}
    	}
    	
    	// 	"{" ( Statement )* "}"
    	if(ctx.LBRACE()!= null) {
    		for(int i = 0 ; i < ctx.statement().size() ; i++) {
    			return this.visitStatement(ctx.statement(i));
    		}
    	}
    	
    	// Identifier "=" Expression ";"
    	if(ctx.ID() != null && ctx.LSQUARE() == null) {
    		String classname;
    		String methodname;
    		
    		if(this.currentClass == null) {
    			classname = null;
    			methodname = null;
    		}
    		if(this.currentMethod == null) {
    			classname = this.currentClass.getName();
    			methodname = null;
    		}else {
    			classname = this.currentClass.getName();
    			methodname = this.currentMethod.getName();
    		}
    		String res = this.findType(classname, methodname, ctx.ID().getText());
    		
        	if(res.equals("")) {
        		if(this.currentClass != null) {
        			if(this.currentClass.getExteneded_class() != null) {
        				res = this.findType(this.currentClass.getExteneded_class(), methodname, ctx.ID().getText());
        			}
        		}
        	}
        	
    		if(!res.equals(this.visitExpr(ctx.expr(0)))) {
    			System.out.println("Identifier \"=\" Expression \";\"");
    			this.errorPrint(ctx.getText());
    		}
    		return null;
    	}
    	
    	// Identifier "[" Expression "]" "=" Expression ";"
    	if(ctx.ID() != null && ctx.LSQUARE() != null) {
    		if(!this.exprCheck("int", this.visitExpr(ctx.expr(0)), ctx.expr(0), true)) {
        		return null;
        	}
    		if(!this.exprCheck("int", this.visitExpr(ctx.expr(1)), ctx.expr(1), true)) {
        		return null;
        	}
    		String classname;
    		String methodname;
    		if(this.currentClass == null) {
    			classname = null;
    			methodname = null;
    		}
    		if(this.currentMethod == null) {
    			classname = this.currentClass.getName();
    			methodname = null;
    		}else {
    			classname = this.currentClass.getName();
    			methodname = this.currentMethod.getName();
    		}
    		if(!this.findType(classname, methodname, ctx.ID().getText()).equals("int[]")) {
    			System.out.println("Identifier \"[\" Expression \"]\" \"=\" Expression \";\"");
    			this.errorPrint(ctx.getText());
    		}
    	}
    	return null;
    }

    @Override
    public String visitExpr(MiniJavaGrammarParser.ExprContext ctx) {
    	
    	/**
    	 * base case for the expr
    	 */
        if(ctx.INTEGER() != null) {
        	return "int";
        }
        if(ctx.TRUE() != null || ctx.FALSE() != null) {
        	return "boolean";
        }
        if(ctx.ID() != null && ctx.getChildCount() == 1) {
        	String classname;
    		String methodname;
    		if(this.currentClass == null) {
    			classname = null;
    			methodname = null;
    		}
    		if(this.currentMethod == null) {
    			classname = this.currentClass.getName();
    			methodname = null;
    		}else {
    			classname = this.currentClass.getName();
    			methodname = this.currentMethod.getName();
    		}
    		
    		String res = this.findType(classname, methodname, ctx.ID().getText());
    		
        	if(res.equals("")) {
        		if(this.currentClass != null) {
        			if(this.currentClass.getExteneded_class() != null) {
        				res = this.findType(this.currentClass.getExteneded_class(), methodname, ctx.ID().getText());
        			}
        		}
        	}
        	return res;
        }
        if(ctx.THIS() != null) {
        	return this.currentClass.getName();
        }
        
        /**
         * Expression ( "&&" | "<" | "+" | "-" | "*" ) Expression
         */
        if(ctx.op() != null) {
        	if(ctx.op().getText().equals("&&")) {
        		if(this.exprCheck("boolean",this.visitExpr(ctx.expr(0)), ctx,true) && this.exprCheck("boolean", this.visitExpr(ctx.expr(1)), ctx,true)) {
        			return "boolean";
        		}
        		return "";
        		}else if(ctx.op().getText().equals("<")){
        			if(this.exprCheck("int", this.visitExpr(ctx.expr(0)), ctx,true) && this.exprCheck("int", this.visitExpr(ctx.expr(1)), ctx,true)) {
        			return "boolean";
        		}
        		}else{
        			if(this.exprCheck("int", this.visitExpr(ctx.expr(0)), ctx,true) && this.exprCheck("int", this.visitExpr(ctx.expr(1)), ctx,true)) {
        				return "int";
        			}
        		return "";
        	}
        }
        
        /**
         * case of Expression "[" Expression "]"
         * we only have int[], so both expression need to be int
         */
        if(ctx.expr().size() == 2 && ctx.LSQUARE() != null) {
        	if(this.exprCheck("int[]", this.visitExpr(ctx.expr(0)), ctx,true) && this.exprCheck("int", this.visitExpr(ctx.expr(1)), ctx,true)) {
    			return "int";
    		}
    		return "";
        }
        
        /**
         * Expression "." "length"
         * length is for int[]
         * expr must be int[] type
         */
        if(ctx.LENGTH() != null) {
        	if(this.exprCheck("int[]", this.visitExpr(ctx.expr(0)), ctx,true)) {
        		return "int";
        	}
        	return "";
        }
        
        /**
         * "new" "int" "[" Expression "]"
         * 	declaration of new	array, expression need to be int
         */
        if(ctx.INT() != null) {
        	if(this.exprCheck("int", this.visitExpr(ctx.expr(0)), ctx,true)) {
        		return "int[]";
        	}
        	return "";
        }
        
        /**
         * "!" Expression
         */
        if(ctx.NOT() != null) {
        	if(this.exprCheck("boolean", this.visitExpr(ctx.expr(0)), ctx,true)) {
        		return "boolean";
        	}
        	return "";
        }
        
        /**
         * Expression "." Identifier "(" ( Expression ( "," Expression )* )? ")"
         */
        if(ctx.expr().size() == 1 && ctx.ID() != null) {
        	String type = this.visitExpr(ctx.expr(0));
        	String id = ctx.ID().getText();
        	return this.findType(type, null, id);
        }
        
        
        /**
         * 	"new" Identifier "(" ")"
         */
        if(ctx.NEW() != null && ctx.ID() != null) {
        	return ctx.ID().getText();
        }
       
        
        //"(" Expression ")"
        if((ctx.LPAREN() != null && ctx.DOT() == null)&&ctx.expr()!= null) {
        	return this.visitExpr(ctx.expr(0));
        }
        return "";
    }



    @Override
    public String visitExprlist(MiniJavaGrammarParser.ExprlistContext ctx) {
    	
        if(ctx.exprrest().size() == 0) {
        	return this.visitExpr(ctx.expr());	
        }
        
        StringBuilder sb = new StringBuilder(this.visitExpr(ctx.expr()));
        for(int i = 0 ; i < ctx.exprrest().size() ; i++) {
        	sb.append(",");
        	sb.append(this.visitExprrest(ctx.exprrest(i)));
        }
        return sb.toString();
    }

    @Override
    public String visitExprrest(MiniJavaGrammarParser.ExprrestContext ctx) {
        return this.visitExpr(ctx.expr());
    }

    public void errorPrint(String location) {
    	System.out.println("---------- Mismatch Type ----------");
    	System.out.println("Problem with this expression: " + location);
    }
   
    
    /**
     * it check if the expr is expected type
     * if not return error message with empty string, meaning "undefined" type
     * 
     * @param expected
     * @param real
     * @param ctx
     * @return
     */
    public boolean exprCheck(String expected,String real,MiniJavaGrammarParser.ExprContext ctx,boolean report) {
    	if(!real.equals(expected)) {
    		if(report) {
    			System.out.println("exprcheck");
    			System.out.println("expected: " + expected + "||real: " + real);
    			System.out.println(ctx.getText());
				this.errorPrint(ctx.getText());
    		}
			return false;
		}
    	return true;
    }
    
    public Variable[] stringsToVariables(String in) {
    	String[] vars_str = in.split(" ");
		Variable[] vars = new Variable[vars_str.length];
		for(int i = 0 ; i < vars_str.length ; i++) {
			String[] var_str = vars_str[i].split(",");;
			vars[i] = new Variable(var_str[0],var_str[1]);
		}
		
		return vars;
    }
    
    
    /**
     * it used to find the type from the Program file, provided by listener
     * c and m determine the scope we want to find
     * if c and m all null value,
     * 			we only find for class name
     * 
     * if m is null only
     * 			we find for variables and methods declared by class
     * 
     * if c and m is all not null
     * 			we find for variables under the specific class and name
     * @param c
     * @param m
     * @param name	the identifier we want to find the type
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
