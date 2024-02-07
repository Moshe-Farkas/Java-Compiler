package com.moshefarkas.javacompiler;

import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.parse.ANTLRParser.parserRule_return;
import org.objectweb.asm.Type;

public class SymbolTable {
    // singleton of symbol table
    private SymbolTable() {}
    private static SymbolTable instance;
    public static SymbolTable getInstance() {
        if (instance == null)
            instance = new SymbolTable();
        return instance;
    }

    public void debugPrintTable() {
        System.out.println("symbol table");
        System.out.println("vars: ");
        for (Map.Entry<String, VarInfo> entry : vars.entrySet()) {
            System.out.println("\t" + entry.getValue());
        }
        System.out.println("methods: ");
        for (Map.Entry<String, MethodInfo> entry : methods.entrySet()) {
            System.out.println("\t" + entry.getValue());
        }
    }

    public void test_reset() {
        instance = new SymbolTable();
    }

    private final Map<String, VarInfo> vars = new HashMap<>();
    private final Map<String, MethodInfo> methods = new HashMap<>();

    public void addLocal(String name, VarInfo varInfo) {
        vars.put(name, varInfo);
    }

    public boolean hasVar(String name) {
        return vars.containsKey(name);
    }

    public Type getVarType(String name) {
        return vars.get(name).type;
    }

    public VarInfo getVarInfo(String name) {
        return vars.get(name);
    }

    // methods 
    public void addMethod(String methodName, MethodInfo methodInfo) {
        methods.put(methodName, methodInfo);
    }

    public boolean hasMethod(String methodName) {
        return methods.containsKey(methodName);
    }

    public Type getReturnType(String methodName) {
        return methods.get(methodName).returnType;
    }

    public MethodInfo getMethodInfo(String methodName) {
        return methods.get(methodName);
    }

    public String getMethodDescriptor(String methodName) {
        return Type.getMethodDescriptor(
            getReturnType(methodName), 
            getParamTypes(methodName)
        );
    }

    public Type[] getParamTypes(String methodName) {
        Type[] types = new Type[methods.get(methodName).parameters.size()];
        for (int i = 0; i < types.length; i++) {
            types[i] = methods.get(methodName).parameters.get(i).var.type;
        }
        return types;
    }
}
