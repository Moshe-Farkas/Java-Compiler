package com.moshefarkas.javacompiler.codegen;

import org.objectweb.asm.ClassWriter;

import com.moshefarkas.javacompiler.ast.BaseAstVisitor;
import com.moshefarkas.javacompiler.symboltable.ClassManager;
import com.moshefarkas.javacompiler.symboltable.Clazz;
import com.moshefarkas.javacompiler.symboltable.SymbolTable;

public class CodeGen extends BaseAstVisitor {

    protected static Clazz currentClass;
    protected static ClassWriter classWriter;

    public CodeGen(String className) {
        currentClass = ClassManager.getIntsance().getClass(className);
        ClassGenVisitor classGenVisitor = new ClassGenVisitor();
        classGenVisitor.visit(currentClass.classNode);
    }

    protected CodeGen() {}

    protected SymbolTable currentMethodSymbolTable(String currMethod) {
        return currentClass.methodManager.getSymbolTable(currMethod);
    }

    public byte[] toByteArray() {
        return classWriter.toByteArray();
    }
}
