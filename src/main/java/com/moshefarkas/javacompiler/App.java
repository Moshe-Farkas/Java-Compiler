package com.moshefarkas.javacompiler;

import java.io.FileInputStream;
import java.io.InputStream;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import com.moshefarkas.generated.JavaLexer;
import com.moshefarkas.generated.JavaParser;
import com.moshefarkas.javacompiler.codegen.CodeGenVisitor;
import com.moshefarkas.javacompiler.semanticanalysis.TypeCheckVisitor;
import com.moshefarkas.javacompiler.semanticanalysis.TypeCheckVisitor.Type;

public class App {
    public static void main( String[] args ) throws Exception {

        String inputFile = null;
        if ( args.length>0 ) inputFile = args[0];
        InputStream is = System.in;
        if ( inputFile!=null ) is = new FileInputStream(inputFile);
        CharStream input = CharStreams.fromStream(is);
        JavaLexer lexer = new JavaLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JavaParser parser = new JavaParser(tokens);
        ParseTree tree = parser.compilationUnit(); // parse; start at prog
        if (parser.getNumberOfSyntaxErrors() > 0) {
            return;
        }

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        typeCheckVisitor.visit(tree);

        System.out.println("-------------------------");
        SymbolTable.getInstance().debugPrintTable();

        System.out.println("\nDONE\n");
    }
}
