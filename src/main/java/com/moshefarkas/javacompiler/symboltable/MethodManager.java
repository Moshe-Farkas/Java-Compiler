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

    public void debug_print_method(String methodName) {
        System.out.println("++++++++++++++++++");
        System.out.println(getMethod(methodName));
        System.out.println("++++++++++++++++++");
    }

    public Method getMethod(String methodName) {
        return methods.get(methodName);
    }

    public void test_reset() {
        instance = new MethodManager();
    }

    private Map<String, Method> methods;
   
    public void createNewMethod(String methodName, MethodInfo methodInfo) {
        methods.put(methodName, new Method(methodInfo));
    }

    public SymbolTable getSymbolTable(String methodName) {
        return methods.get(methodName).symbolTable;
    }

    public Type getReturnType(String methodName) {
        return methods.get(methodName).methodInfo.returnType;
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

    public String getMethodDescriptor(String methodName) {
        return Type.getMethodDescriptor(
            getReturnType(methodName), 
            getParamTypes(methodName)
        );
    }
}