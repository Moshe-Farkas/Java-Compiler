package com.moshefarkas.javacompiler.symboltable;

import com.moshefarkas.javacompiler.MethodInfo;

public class Method {
    public SymbolTable symbolTable;
    public MethodInfo methodInfo;

    public Method(MethodInfo methodInfo) {
        this.methodInfo = methodInfo;
        symbolTable = new SymbolTable();
    }

    @Override
    public String toString() {
        return methodInfo + ", " + symbolTable;
    }
}
