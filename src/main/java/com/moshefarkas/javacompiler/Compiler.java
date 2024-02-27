package com.moshefarkas.javacompiler;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.moshefarkas.javacompiler.ast.nodes.ClassNode;
import com.moshefarkas.javacompiler.codegen.ClassGenVisitor;
import com.moshefarkas.javacompiler.parsing.FileParser;
import com.moshefarkas.javacompiler.semanticanalysis.IdentifierUsageVisitor;
import com.moshefarkas.javacompiler.semanticanalysis.SemanticAnalysis;
import com.moshefarkas.javacompiler.semanticanalysis.SymbolTableGenVisitor;
import com.moshefarkas.javacompiler.semanticanalysis.TypeCheckVisitor;
import com.moshefarkas.javacompiler.symboltable.ClassManager;
import com.moshefarkas.javacompiler.symboltable.Clazz;

public class Compiler {
    private static String[] classNames;

    public static void compileFiles(String[] sourceFiles) {
        FileParser fileParser = new FileParser();
        ClassNode[] asts = new ClassNode[sourceFiles.length];
        for (int i = 0; i < sourceFiles.length; i++) {
            try {
                asts[i] = fileParser.parse(sourceFiles[i]);
            } catch (Exception e) {
                System.exit(0);
            }
        }

        classNames = new String[asts.length];
        boolean hadSymbolTableErr = false;
        for (int i = 0; i < asts.length; i++) {
            ClassNode ast = asts[i];
            classNames[i] = ast.className;
            Clazz classSymbolTable = SymbolTableGenVisitor.createSymbolTable(ast);
            if (SymbolTableGenVisitor.hadErr) {
                hadSymbolTableErr = true;
            }
            ClassManager.getIntsance().addNewClass(ast.className, classSymbolTable);
        }
        if (hadSymbolTableErr) {
            System.exit(1);
        }
        semanticanalysis();

        printClasses();
        genCode();
    }

    private static void semanticanalysis() {
        for (String className : classNames) {
            IdentifierUsageVisitor identifierUsageVisitor = new IdentifierUsageVisitor(className);
            identifierUsageVisitor.visit(ClassManager.getIntsance().getClass(className).classNode);
            if (SemanticAnalysis.hadErr) {
                System.exit(0);
            }
            TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor(className);
            typeCheckVisitor.visit(ClassManager.getIntsance().getClass(className).classNode);
            if (SemanticAnalysis.hadErr) {
                System.exit(0);
            }
        }
    }

    private static void genCode() {
        for (String className : classNames) {
            ClassGenVisitor classGenVisitor = new ClassGenVisitor(className);
            classGenVisitor.visit(ClassManager.getIntsance().getClass(className).classNode);
            writeToFile(className + ".class", classGenVisitor.toByteArray());
        }
    }

    private static void writeToFile(String filePath, byte[] classData) {
        try {
            FileOutputStream dos = new FileOutputStream(filePath);
            dos.write(classData);
            dos.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private static void printClassSymbols() {
        for (String className : classNames) {
            System.out.println();
            System.out.println(className);
            System.out.println("*******************************");
            System.out.println(ClassManager.getIntsance().getClass(className));
            System.out.println("*******************************");
            System.out.println();
        }
    }

    private static void printClasses() {
        for (String className : classNames) {
            System.out.println();
            System.out.println(className);
            System.out.println("*******************************");
            AstPrintVisitor astPrintVisitor = new AstPrintVisitor();
            astPrintVisitor.visit(ClassManager.getIntsance().getClass(className).classNode);
            System.out.println("*******************************");
            System.out.println();
        }
    }
}
