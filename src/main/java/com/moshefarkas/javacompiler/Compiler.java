package com.moshefarkas.javacompiler;

import com.moshefarkas.javacompiler.ast.nodes.ClassNode;
import com.moshefarkas.javacompiler.parsing.FileParser;
import com.moshefarkas.javacompiler.semanticanalysis.SymbolTableGenVisitor;
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
        for (int i = 0; i < asts.length; i++) {
            ClassNode ast = asts[i];
            classNames[i] = ast.className;
            Clazz classSymbolTable = SymbolTableGenVisitor.createSymbolTable(ast);
            ClassManager.getIntsance().addNewClass(ast.className, classSymbolTable);
        }

        // printClasses();
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
