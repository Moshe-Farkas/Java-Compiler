package com.moshefarkas.javacompiler.codegen;

import org.objectweb.asm.ClassWriter;

import com.moshefarkas.javacompiler.ast.BaseAstVisitor;
import com.moshefarkas.javacompiler.symboltable.ClassManager;
import com.moshefarkas.javacompiler.symboltable.Clazz;
import com.moshefarkas.javacompiler.symboltable.LocalVarSymbolTable;

public class CodeGen extends BaseAstVisitor {

    protected static Clazz currentClass;
    protected static ClassWriter classWriter;

    public CodeGen(String className) {
        currentClass = ClassManager.getIntsance().getClass(className);
    }

    protected CodeGen() {}

    protected LocalVarSymbolTable currentMethodSymbolTable(String currMethod) {
        return currentClass.methodManager.getSymbolTable(currMethod);
    }

    public byte[] toByteArray() {
        return classWriter.toByteArray();
    }
}
