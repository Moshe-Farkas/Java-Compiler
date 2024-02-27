package com.moshefarkas.javacompiler.semanticanalysis;

import com.moshefarkas.javacompiler.ast.nodes.ClassNode;
import com.moshefarkas.javacompiler.parsing.StringParser;
import com.moshefarkas.javacompiler.symboltable.ClassManager;

public class BaseSemanticAnalysis {

    protected ClassNode ast;
    private String methods =  "public void intMeth(int a2){}"          +
                                "public void floatMeth(float b2){}"      + 
                                "public void charMeth(char c2){}"        + 
                                "public void byteMeth(byte d2){}"        +
                                "public void emptyMeth(){}"              + 
                                "public void intArr1Dim(int[] e2){}"     +
                                "public void intArr2Dim(int[][] e2){}"   +
                                "public void floatArr1Dim(float[] e2){}";
        
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

    protected void compileInstructions(String customSource) {
        customSource = "public class Demo { public void meth() {" 
            + customSource + "}" + 
            methods + "}";
        compile(customSource);
    }

    protected void compileMethodDecl(String methodDecl) {
        String source = "public class Demo { " + methodDecl + "{} }";
        compile(source);
    }

    protected void compileMethod(String method) {
        String source = "public class Demo {" + method + "}";
        compile(source);
    }

    protected void compileFields(String[] fields) {
        String temp = "";
        for (String field : fields) {
            temp += field;
        }
        String source = "public class Demo {" + temp + "}";
        compile(source);
    }

    protected void compileClass(String classData) {
        compile(classData);
    }
}
