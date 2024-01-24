package com.moshefarkas.javacompiler.irgeneration;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import com.moshefarkas.generated.Java8Lexer;
import com.moshefarkas.generated.Java8Parser;

public class BaseMock {

    public ParseTree tree;
    private String startSource = "public class Test {\r\n" + //
            "\tpublic void method1() {";
    private String endSource = "\t}\r\n" + //
            "}";
        
    public void compile(String source) {
        source = startSource + source + endSource;
        CharStream input = CharStreams.fromString(source);
        Java8Lexer lexer = new Java8Lexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Java8Parser parser = new Java8Parser(tokens);
        tree = parser.compilationUnit(); 
        if (parser.getNumberOfSyntaxErrors() > 0) {
            return;
        }
    }
}
