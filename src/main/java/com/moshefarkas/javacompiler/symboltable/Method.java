package com.moshefarkas.javacompiler.symboltable;

import com.moshefarkas.javacompiler.MethodInfo;

public class Method {
    public LocalVarSymbolTable symbolTable;
    public MethodInfo methodInfo;

    public Method(MethodInfo methodInfo) {
        this.methodInfo = methodInfo;
        symbolTable = new LocalVarSymbolTable();
    }

    @Override
    public String toString() {
        return methodInfo + ", " + symbolTable;
    }
}
