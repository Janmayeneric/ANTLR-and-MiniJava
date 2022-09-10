package com.hsinyu.main;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.InputStream;

import com.hsinyu.minijava.MiniJavaGrammarLexer;
import com.hsinyu.minijava.MiniJavaGrammarParser;



public class MiniJavaMain {

    public static void main(String[] args) throws Exception {
    	
    	
    	ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    	
    	/**
    	 * root dictionary is in src/main/resources
    	 * if you want to input more file for testing
    	 * store in that file 
    	 */
    	InputStream is = classloader.getResourceAsStream("BubbleSort.java");
    	
        ANTLRInputStream input = new ANTLRInputStream(is);

        MiniJavaGrammarLexer lexer = new MiniJavaGrammarLexer(input);

        CommonTokenStream tokens = new CommonTokenStream(lexer);

        MiniJavaGrammarParser parser = new MiniJavaGrammarParser(tokens);
        
        
        ParseTree tree = parser.program();

        
        ParseTreeWalker walker = new ParseTreeWalker();

        MiniJavaListener scope = new MiniJavaListener();

        walker.walk(scope, tree);
        
        
        Typecheck s = new Typecheck(scope.getProgram(), scope);
        
        s.programTable(); // print the scope table
        
        MiniJavaVisitor visitor = new MiniJavaVisitor(scope.getProgram());
        
        
        visitor.visitProgram(scope.end); // do the type checking
        
    }


}
