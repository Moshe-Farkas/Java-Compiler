package com.moshefarkas.javacompiler;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.antlr.v4.parse.ANTLRParser.optionValue_return;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.moshefarkas.generated.Java8Lexer;
import com.moshefarkas.generated.Java8Parser;
import com.moshefarkas.javacompiler.ast.astgen.ClassVisitor;
import com.moshefarkas.javacompiler.ast.nodes.ClassNode;
import com.moshefarkas.javacompiler.codegen.ClassGenVisitor;
import com.moshefarkas.javacompiler.semanticanalysis.SemanticAnalysis;

public class App {
    public static void main( String[] args ) throws Exception {
        
        // int[][] a = new int[][] {{}, {}};
        // System.out.println(a.getClass().getName());
    
        // int a[][];
        // a = new int[5];
        

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
        SymbolTable.getInstance().debugPrintTable();
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
