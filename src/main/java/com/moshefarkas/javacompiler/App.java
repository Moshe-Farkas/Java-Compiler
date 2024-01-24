package com.moshefarkas.javacompiler;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import com.moshefarkas.generated.Java8Lexer;
import com.moshefarkas.generated.Java8Parser;
import com.moshefarkas.javacompiler.irgeneration.IrGeneratorVisitor;

public class App {
    public static void main( String[] args ) throws Exception {
        String inputFile = null;
        if ( args.length>0 ) inputFile = args[0];
        InputStream is = System.in;
        if ( inputFile!=null ) is = new FileInputStream(inputFile);
        CharStream input = CharStreams.fromStream(is);
        Java8Lexer lexer = new Java8Lexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Java8Parser parser = new Java8Parser(tokens);
        ParseTree tree = parser.compilationUnit(); 
        if (parser.getNumberOfSyntaxErrors() > 0) {
            return;
        }

        IrGeneratorVisitor visitor = new IrGeneratorVisitor();
        visitor.visit(tree);
        visitor.ir.debugPrintCode();
        SymbolTable.getInstance().debugPrintTable();

        // SemanticAnalysisVisitor semAnal = new SemanticAnalysisVisitor();
        // semAnal.visit(tree);

        // System.out.println("-------------------------");
        // SymbolTable.getInstance().debugPrintTable();

        System.out.println("\nDONE\n");
    }

}
