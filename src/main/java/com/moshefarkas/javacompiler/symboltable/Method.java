package com.moshefarkas.javacompiler.symboltable;

import com.moshefarkas.javacompiler.ast.nodes.MethodNode;

public class Method {
    public LocalVarSymbolTable symbolTable;
    public MethodNode methodNode;

    public Method(MethodNode methodNode, boolean isStaticMethod) {
        this.methodNode = methodNode;
        symbolTable = new LocalVarSymbolTable(isStaticMethod ? 0 : 1);
    }

    @Override
    public String toString() {
        return methodNode + ", " + symbolTable;
    }
}
