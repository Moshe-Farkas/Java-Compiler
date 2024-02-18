package com.moshefarkas.javacompiler;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.objectweb.asm.tree.VarInsnNode;

import com.moshefarkas.generated.Java8Lexer;
import com.moshefarkas.generated.Java8Parser;
import com.moshefarkas.javacompiler.ast.astgen.ClassVisitor;
import com.moshefarkas.javacompiler.ast.nodes.ClassNode;
import com.moshefarkas.javacompiler.codegen.ClassGenVisitor;
import com.moshefarkas.javacompiler.semanticanalysis.SemanticAnalysis;
import com.moshefarkas.javacompiler.symboltable.SymTable;
import com.moshefarkas.javacompiler.symboltable.SymbolTable;

public class App {
    public static void main( String[] args ) throws Exception {
        
        // int[][] a = new int[][] {{}, {}};
        // System.out.println(a.getClass().getName());
    
        // int a[][];
        // a = new int[5];

        // char a = 'p';
        // char b = 'r';
        // Object c = a + b;
        // System.out.println(c.getClass());

        // int a = Math.sqrt('f', "s") + Math.sqrt("fk");

        // int a = true + true;
        // int a = true + 5 / 6;

        // if ((true == 6) == 6 == false) {

        // }

        // int a = 45;
        // float b = 2f;
        // Object c = a == b;

        // Stack<Integer> stack = new Stack<>();
        // stack.push(0);
        // stack.push(1);
        // stack.push(2);
        // stack.push(3);
        // stack.push(4);
        
        // int a = 56;
        // if (true) {
        //     int b = a;
        // } else {
        //     int b = a;
        // }



        // SymTable table = SymTable.getInstance();
        // table.createNewScope();
        //     VarInfo a = new VarInfo();
        //     a.name = "a";
        //     table.addVar(a);

        //     table.createNewScope();
        //         // if then statement
        //         // VarInfo b = new VarInfo();
        //         // b.name = "b";
        //         // table.
        //         if (!table.hasVar("a")) {
        //             System.out.println("does not have the var a in scope (else)");
        //             System.exit(0);
        //         } 
        //         VarInfo ifVar = new VarInfo();
        //         ifVar.name = "ifVar";
        //         table.addVar(ifVar);
        //     table.exitScope();

        //     table.createNewScope();
        //         if (!table.hasVar(a.name)) {
        //             System.out.println("does not have the var" + a.name + " in scope (else)");
        //             System.exit(0);
        //         } 
        //         VarInfo elseVar = new VarInfo();
        //         elseVar.name = "elseVar";
        //         table.addVar(elseVar);
        //     table.exitScope();


        // VarInfo c = new VarInfo();
        // c.name = "c";
        // table.addVar(c);
        // table.exitScope();
        

        // table.resetScopes();
        // // enter method scope
        // table.enterScope();
        // String varCheck = "c";
        // if (!table.hasVar(varCheck)) {
        //     System.out.println("does not have var in method scope: " + varCheck);
        //     System.exit(0);
        // }
        
        // // enter if-then scope
        // table.enterScope();
        // // should have a, and ifVar in scope
        // if (!table.hasVar(a.name)) {
        //     System.out.println("does not have var in if-then scope: " + a.name);
        //     System.exit(0);
        // }
        // if (!table.hasVar(ifVar.name)) {
        //     System.out.println("does not have var in if-then scope: " + ifVar.name);
        //     System.exit(0);
        // }

        // // exit if-then scope
        // table.exitScope();

        // // enter else scope
        // table.enterScope();
        
        // if (!table.hasVar(elseVar.name)) {
        //     System.out.println("does not have var in else scope: " + elseVar.name);
        //     System.exit(0);
        // }

        // if (!table.hasVar(a.name)) {
        //     System.out.println("does not have var in else scope: " + a.name);
        //     System.exit(0);
        // }
        // if (table.hasVar(ifVar.name)) {
        //     System.out.println("has var from if-then in else scope: " + ifVar.name);
        //     System.exit(0);
        // }
        // // exit else scope
        // table.exitScope();
        // // should only have a, c in scope
        // if (table.hasVar(ifVar.name)) {
        //     System.out.println("has var from if-then in method scope: " + ifVar.name);
        //     System.exit(0);
        // }
        // if (table.hasVar(elseVar.name)) {
        //     System.out.println("has var from else in method scope: " + elseVar.name);
        //     System.exit(0);
        // }
        // if (!table.hasVar(a.name)) {
        //     System.out.println("does not have var in method scope: " + a.name);
        //     System.exit(0);
        // }
        // if (!table.hasVar(c.name)) {
        //     System.out.println("does not have var in method scope: " + c.name);
        //     System.exit(0);
        // }
        
        // // exit method-scope
        // table.exitScope();

        // SymTable table = SymTable.getInstance();
        // int a = 5;
        // c = 4;
        // if (true) {
        //     int b = 4;
        // }
        // if (a) {
        //     int b = 56;
        // }
        // int c = 4;

        // SymTable table = SymTable.getInstance();
        // create method scope
        // table.createNewScope();
        // VarInfo a = new VarInfo();
        // a.name = "a";
        // table.addVar(a);
        // // if (!table.hasVar("c")) {
        // //     System.out.println("method scope does not have var `c`.");
        // //     System.exit(0);
        // // }
        // // create if-then scope
        // table.createNewScope();
        // VarInfo b = new VarInfo();
        // b.name = "b";
        // table.addVar(b);
        // // exit if-then scope
        // table.printScope();
        // table.exitScope();

        // // create second if-then scope
        // table.createNewScope();
        // table.addVar(b);
        // // exit second if-then scope
        // table.printScope();
        // table.exitScope();

        // // exit method scope
        // table.printScope();
        // table.exitScope();

        // table.printSymTable();

        // SymTable table = SymTable.getInstance();
        // table.createNewScope();
        //     VarInfo a = new VarInfo();
        //     a.name = "a";
        //     table.addVar(a);
        //     if (!table.hasVar(a.name)) {
        //         System.out.println("method scope does not have var: " + a.name);
        //         System.exit(0);
        //     }

        //     VarInfo var = table.getVarInfo(a.name);
        //     System.out.println(var);


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
