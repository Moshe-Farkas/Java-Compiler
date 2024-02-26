package com.moshefarkas.javacompiler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import com.moshefarkas.generated.Java8Lexer;
import com.moshefarkas.generated.Java8Parser;
import com.moshefarkas.javacompiler.ast.astgen.ClassVisitor;
import com.moshefarkas.javacompiler.ast.nodes.ClassNode;
import com.moshefarkas.javacompiler.codegen.CodeGen;
import com.moshefarkas.javacompiler.semanticanalysis.SemanticAnalysis;
import com.moshefarkas.javacompiler.semanticanalysis.SemanticAnalysis.SemanticError;
import com.moshefarkas.javacompiler.semanticanalysis.SymbolTableGenVisitor;
import com.moshefarkas.javacompiler.symboltable.ClassManager;

public class Compiler {
    private static String[] classNames;

    public static void compileFiles(String[] sourceFiles) {
        classNames = new String[sourceFiles.length];
        for (int i = 0; i < sourceFiles.length; i++) {
            compileFile(sourceFiles[i]);
            classNames[i] = sourceFiles[i].replace(".java", "");
        }
        generateSymbolTables();
        printClassSymbols();
        semanticanalysis();

        if (!SemanticAnalysis.hadErr) {
            genCode();
        }
    }

    private static void semanticanalysis() {
        for (String className : classNames) {
            try {
                new SemanticAnalysis(className);
            } catch (SemanticError e) {

            }
        }
    }

    private static void genCode() {
        for (String className : classNames) {
            try {
                CodeGen codeGen = new CodeGen(className);
                FileOutputStream fos = new FileOutputStream(className + ".class");
                fos.write(codeGen.toByteArray());
                fos.close();
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    private static void generateSymbolTables() {
        for (String className : classNames) {
            SymbolTableGenVisitor symbolTableGenVisitor = new SymbolTableGenVisitor(className);
            ClassNode classNode = ClassManager.getIntsance().getClass(className).classNode;
            symbolTableGenVisitor.visit(classNode);
        }
    }

    private static void compileFile(String fileName) {
        try {
            FileInputStream fis = new FileInputStream(fileName);
            CharStream input = CharStreams.fromStream(fis);
            Java8Lexer lexer = new Java8Lexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            Java8Parser parser = new Java8Parser(tokens);
            ParseTree tree = parser.compilationUnit(); 
            if (parser.getNumberOfSyntaxErrors() > 0) {
                return;
            }
            ClassVisitor visitor = new ClassVisitor();
            visitor.visit(tree);

            addNewClass(visitor.currentClass); 
        } catch (FileNotFoundException e) {
            System.out.printf("error: file `%s` not found.\n", fileName);
            System.exit(1);
        } catch (IOException e) {
            System.out.printf("error: file `%s` can't be opened.\n", fileName);
            System.exit(1);
        }
    }

    private static void addNewClass(ClassNode node) {
        ClassManager.getIntsance().createNewClass(node);
    }

    private static void printClasses() {
        AstPrintVisitor astPrintVisitor = new AstPrintVisitor();
        for (String className : classNames) {
            ClassNode cn = ClassManager.getIntsance().getClass(className).classNode;
            System.out.println();
            System.out.println("*******************************");
            System.out.println("class: " + cn.className);
            astPrintVisitor.visit(cn); 
            System.out.println("*******************************");
            System.out.println();
        }
    }

    private static void printClassSymbols() {
    
        for (String className : classNames) {
            System.out.println();
            System.out.println("*******************************");
            System.out.println(ClassManager.getIntsance().getClass(className));
            System.out.println("*******************************");
            System.out.println();
        }
    }
}
