package com.moshefarkas.javacompiler.semanticanalysis;

import com.moshefarkas.javacompiler.ast.nodes.ClassNode;
import com.moshefarkas.javacompiler.parsing.StringParser;
import com.moshefarkas.javacompiler.symboltable.ClassManager;

public class BaseSemanticAnalysis {

    protected ClassNode ast;
    private String startSource = "public class Demo {\r\n" + //
            "\tpublic void method1() {";
    private String endSource = "\t}\r\n" + 
                             "public void intMeth(int a2){}"          +
                             "public void floatMeth(float b2){}"      + 
                             "public void charMeth(char c2){}"        + 
                             "public void byteMeth(byte d2){}"        +
                             "public void emptyMeth(){}"              + 
                             "public void intArr1Dim(int[] e2){}"     +
                             "public void intArr2Dim(int[][] e2){}"   +
                             "public void floatArr1Dim(float[] e2){}" +
                             "}";
    
    private String startSourceMethods = "public class Demo {";
        
    private void compile(String source) {
        ClassManager.getIntsance().test_reset();
        StringParser stringParser = new StringParser();
        try {
             ast = stringParser.parse(source);
        } catch (Exception e) {
            System.out.println("\n\nhad parse error\n\n");
            System.exit(99);
        }
    }

    protected void compileInsctructions(String source) {
        source = "public class Demo { public void meth() {" + source + "}}";
        compile(source);
    }

    protected void compileMethodModifiers(String modifers) {
        String source = startSourceMethods + modifers + " void meth(){}" + "}";
        compile(source);
    }

    protected void compileMethodDecl(String methodDecl) {
        String source = startSourceMethods + methodDecl + " {}" + "}";
        compile(source);
    }

    protected void compileMethod(String method) {
        String source = "public class Demo {" + method + "}";
        compile(source);
    }
}
