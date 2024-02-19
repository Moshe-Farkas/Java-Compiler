package com.moshefarkas.javacompiler;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import com.moshefarkas.generated.Java8Lexer;
import com.moshefarkas.generated.Java8Parser;
import com.moshefarkas.javacompiler.ast.astgen.ClassVisitor;
import com.moshefarkas.javacompiler.ast.nodes.ClassNode;
import com.moshefarkas.javacompiler.codegen.ClassGenVisitor;
import com.moshefarkas.javacompiler.semanticanalysis.SemanticAnalysis;
import com.moshefarkas.javacompiler.symboltable.MethodManager;
import com.moshefarkas.javacompiler.symboltable.SymbolTable;

public class App {
    public static void main( String[] args ) throws Exception {
        // table.exitScope();
        // System.exit(0);


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
        System.out.println("-------------------------------------------");


        // AstPrintVisitor oktemp = new AstPrintVisitor();
        // oktemp.visitClassNode(ast);


        // semantic analysis
        new SemanticAnalysis(ast);
        if (SemanticAnalysis.hadErr) {
            return;
        }

        // end semantic analysis
        System.out.println("-------------------------------------------");
        AstPrintVisitor printVisitor = new AstPrintVisitor();
        printVisitor.visitClassNode(ast);
        System.out.println("-------------------------------------------");
        MethodManager.getInstance().debug_print_methods();
        System.out.println("-------------------------------------------");
        // code gen
        ClassGenVisitor classGen = new ClassGenVisitor();
        classGen.visitClassNode(ast);

        FileOutputStream fos = new FileOutputStream("Demo.class");
        fos.write(classGen.classWriter.toByteArray());
        fos.close();
        System.out.println("\nDONE\n");
    }
}
