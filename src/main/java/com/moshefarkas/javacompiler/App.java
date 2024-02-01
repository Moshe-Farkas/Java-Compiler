package com.moshefarkas.javacompiler;

import java.io.FileInputStream;
import java.io.InputStream;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import com.moshefarkas.generated.Java8Lexer;
import com.moshefarkas.generated.Java8Parser;
import com.moshefarkas.javacompiler.ast.astgen.ClassVisitor;
import com.moshefarkas.javacompiler.ast.nodes.ClassNode;

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

        // this is the ast
        ClassVisitor visitor = new ClassVisitor();
        visitor.visit(tree);
        
        ClassNode ast = visitor.currentClass;
        SymbolTableGenVisitor sv = new SymbolTableGenVisitor();
        sv.visitClassNode(ast);
        System.out.println("-------------------------------------------");
        SemanticAnalysisVisitor s = new SemanticAnalysisVisitor();
        s.visitClassNode(ast);
        System.out.println("-------------------------------------------");
        AstPrintVisitor printVisitor = new AstPrintVisitor();
        printVisitor.visitClassNode(ast);
        System.out.println("-------------------------------------------");

        SymbolTable.getInstance().debugPrintTable();

        System.out.println("\nDONE\n");

    }

}
