package com.moshefarkas.javacompiler.symboltable;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Type;

import com.moshefarkas.javacompiler.MethodInfo;
import com.moshefarkas.javacompiler.VarInfo;

public class MethodManager {
    private static MethodManager instance;
    private MethodManager() {
        methods = new HashMap<>();
    }

    public static MethodManager getInstance() {
        if (instance == null)
            instance = new MethodManager();
        return instance;
    }

    public void debug_print_methods() { 
        System.out.println("methods: ");
        System.out.println("/////////////////////////////////////////");
        for (Map.Entry<String, Method> m : methods.entrySet()) {
            System.out.println("------------------");
            System.out.println(m.getValue());
            System.out.println("------------------");
        }
        System.out.println("/////////////////////////////////////////");
    }

    public void debug_print_method() {
        System.out.println("++++++++++++++++++");
        System.out.println(currMethod);
        System.out.println("++++++++++++++++++");
    }

    public void test_reset() {
        instance = new MethodManager();
    }

    private Map<String, Method> methods;
    private Method currMethod;
    
    public void enterMethod(String methodName) {
        currMethod = methods.get(methodName);
        currMethod.symbolTable.resetScopes();
    }

    public void createNewMethod(String methodName, MethodInfo methodInfo) {
        methods.put(methodName, new Method(methodInfo));
    }

    public SymbolTable getSymbolTable() {
        return currMethod.symbolTable;
    }

    public Type getReturnType() {
        return currMethod.methodInfo.returnType;
    }

    public String methodName() {
        return currMethod.methodInfo.methodName;
    }

    public boolean hasMethod(String methodName) {
        return methods.containsKey(methodName);
    }

    public Type[] getParamTypes(String methodName) {
        Method calle = methods.get(methodName);
        Type[] types = new Type[calle.methodInfo.parameters.size()];
        for (int i = 0; i < types.length; i++) {
            types[i] = calle.methodInfo.parameters.get(i).var.type;
        }
        return types;
    }

    public String getMethodDescriptor() {
        return Type.getMethodDescriptor(
            getReturnType(), 
            getParamTypes(currMethod.methodInfo.methodName)
        );
    }

    public VarInfo getVarInfo(String varName) {
        return currMethod.symbolTable.getVarInfo(varName);
    }
}
