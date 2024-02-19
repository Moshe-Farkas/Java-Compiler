package com.moshefarkas.javacompiler.semanticanalysis;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import com.moshefarkas.generated.Java8Lexer;
import com.moshefarkas.generated.Java8Parser;
import com.moshefarkas.javacompiler.ast.astgen.ClassVisitor;
import com.moshefarkas.javacompiler.ast.nodes.ClassNode;

public class BaseSemanticAnalysis {

    protected ClassNode ast;
    private String startSource = "public class Test {\r\n" + //
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
    
    private String startSourceMethods = "public class Test {";
        
    protected void compile(String source) {
        source = startSource + source + endSource;
        CharStream input = CharStreams.fromString(source);
        Java8Lexer lexer = new Java8Lexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Java8Parser parser = new Java8Parser(tokens);
        ParseTree tree = parser.compilationUnit(); 
        if (parser.getNumberOfSyntaxErrors() > 0) {
            System.exit(1);
        }
        ClassVisitor astGen = new ClassVisitor();
        astGen.visit(tree);
        ast = astGen.currentClass;
    }

    protected void compileMethodModifiers(String modifers) {
        String source = startSourceMethods + modifers + " void meth(){}" + "}";
        CharStream input = CharStreams.fromString(source);
        Java8Lexer lexer = new Java8Lexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Java8Parser parser = new Java8Parser(tokens);
        ParseTree tree = parser.compilationUnit(); 
        if (parser.getNumberOfSyntaxErrors() > 0) {
            System.exit(1);
        }
        ClassVisitor astGen = new ClassVisitor();
        astGen.visit(tree);
        ast = astGen.currentClass;
    }

    protected void compileMethodDecl(String methodDecl) {
        String source = startSourceMethods + methodDecl + " {}" + "}";
        CharStream input = CharStreams.fromString(source);
        Java8Lexer lexer = new Java8Lexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Java8Parser parser = new Java8Parser(tokens);
        ParseTree tree = parser.compilationUnit(); 
        if (parser.getNumberOfSyntaxErrors() > 0) {
            System.exit(1);
        }
        ClassVisitor astGen = new ClassVisitor();
        astGen.visit(tree);
        ast = astGen.currentClass;
    }

    protected void compileMethod(String method) {
        String source = "public class test {" + method + "}";
        CharStream input = CharStreams.fromString(source);
        Java8Lexer lexer = new Java8Lexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Java8Parser parser = new Java8Parser(tokens);
        ParseTree tree = parser.compilationUnit(); 
        if (parser.getNumberOfSyntaxErrors() > 0) {
            System.exit(1);
        }
        ClassVisitor astGen = new ClassVisitor();
        astGen.visit(tree);
        ast = astGen.currentClass;
    }
}
