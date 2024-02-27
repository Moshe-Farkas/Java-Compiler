package com.moshefarkas.javacompiler.parsing;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import com.moshefarkas.generated.Java8Lexer;
import com.moshefarkas.generated.Java8Parser;
import com.moshefarkas.javacompiler.ast.astgen.ClassVisitor;
import com.moshefarkas.javacompiler.ast.nodes.ClassNode;

public class StringParser implements IParser {
    @Override 
    public ClassNode parse(String classData) throws Exception {
        CharStream cStream = CharStreams.fromString(classData);
        Java8Lexer lexer = new Java8Lexer(cStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Java8Parser parser = new Java8Parser(tokens);
        ParseTree tree = parser.compilationUnit(); 
        if (parser.getNumberOfSyntaxErrors() > 0) {
            throw new Exception();
        }
        ClassVisitor visitor = new ClassVisitor();
        visitor.visit(tree);
        return visitor.currentClass;
    }
}
