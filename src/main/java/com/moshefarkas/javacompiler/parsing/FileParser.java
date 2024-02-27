package com.moshefarkas.javacompiler.parsing;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InputMismatchException;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import com.moshefarkas.generated.Java8Lexer;
import com.moshefarkas.generated.Java8Parser;
import com.moshefarkas.javacompiler.ast.astgen.ClassVisitor;
import com.moshefarkas.javacompiler.ast.nodes.ClassNode;

public class FileParser implements IParser {
    @Override
    public ClassNode parse(String fileName) throws Exception {
        if (!fileName.endsWith(".java")) {
            throw new Exception();
        }
        FileInputStream fis = new FileInputStream(fileName);
        CharStream input = CharStreams.fromStream(fis);
        Java8Lexer lexer = new Java8Lexer(input);
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
