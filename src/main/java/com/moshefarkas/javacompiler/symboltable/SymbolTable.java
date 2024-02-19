package com.moshefarkas.javacompiler.symboltable;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Type;

import com.moshefarkas.javacompiler.MethodInfo;
import com.moshefarkas.javacompiler.VarInfo;

public class SymbolTable {
    
    private Scope currScope;
    private Map<Integer, Scope> scopes;
    private int currScopeLevel;

    public void printSymTable() {
        System.out.println("-----------------");
        for (Map.Entry<Integer, Scope> scope : scopes.entrySet()) {
            System.out.println("scope id: " + scope.getKey());
            System.out.println("  " + scope.getValue());
        }
        System.out.println("-----------------");
    }

    public void printScope() {
        System.out.println(currScope);
    }

    public void createNewScope() {
        Scope newScope;
        if (currScope == null) {
            newScope = new Scope(0, null);
        } else {
            newScope = new Scope(currScope.getNextLocalIndex(), currScope);
        }
        currScope = newScope;
        scopes.put(currScopeLevel++, newScope);
    }

    public void exitScope() {
        currScope = currScope.getParent();
    }

    public void NewaddVar(VarInfo var) {
        currScope.addVar(var);
    }

    public boolean NewhasVar(String name) {
        return currScope.hasVar(name);
    }          
               
    public void resetScopes() {
        currScopeLevel = 0;
        enterScope();
    }          

    public void enterScope() {
        currScope = scopes.get(currScopeLevel++);
    }

    public SymbolTable () {
        scopes = new HashMap<>();
        currScopeLevel = 0;
        // push a global scoop
        // Scope globalScope = new Scope(0, null);
        // scopes.put(currScopeLevel++, globalScope);
        // currScope = globalScope;
    }

    public VarInfo NewgetVarInfo(String name) {
        return currScope.getVarInfo(name);
    }

    public Type NewgetVarType(String name) {
        return currScope.getVarType(name);
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        for (Map.Entry<Integer, Scope> scope : scopes.entrySet()) {
            res.append("\nscope id: " + scope.getKey());
            res.append("\n " + scope.getValue());
        }
        return res.toString();
    }

// --------------------------------------------------------------------------------------------------------------------------------
    // singleton of symbol table
    // private SymbolTable() {}
    // private static SymbolTable instance;

    // public static SymbolTable getInstance() {
    //     if (instance == null)
    //         instance = new SymbolTable();
    //     return instance;
    // }

    public void debugPrintTable() {
        printSymTable();


        // System.out.println("symbol table");
        // System.out.println("vars: ");
        // for (Map.Entry<String, VarInfo> entry : vars.entrySet()) {
        //     System.out.println("\t" + entry.getValue());
        // }
        // System.out.println("methods: ");
        // for (Map.Entry<String, MethodInfo> entry : methods.entrySet()) {
        //     System.out.println("\t" + entry.getValue());
        // }
    }

    // public void test_reset() {
    //     instance = new SymbolTable();
    // }

    private final Map<String, VarInfo> vars = new HashMap<>();
    private final Map<String, MethodInfo> methods = new HashMap<>();

    public void addLocal(String name, VarInfo varInfo) {
        NewaddVar(varInfo);
        // vars.put(name, varInfo);
    }

    public boolean hasVar(String name) {
        return NewhasVar(name);
        // return vars.containsKey(name);
    }

    public Type getVarType(String name) {
        return NewgetVarType(name);
        // return vars.get(name).type;
    }

    public VarInfo getVarInfo(String name) {
        return NewgetVarInfo(name);
        // return vars.get(name);
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

    // public String getMethodDescriptor(String methodName) {
    //     return Type.getMethodDescriptor(
    //         getReturnType(methodName), 
    //         getParamTypes(methodName)
    //     );
    // }

    // public Type[] getParamTypes(String methodName) {
    //     Type[] types = new Type[methods.get(methodName).parameters.size()];
    //     for (int i = 0; i < types.length; i++) {
    //         types[i] = methods.get(methodName).parameters.get(i).var.type;
    //     }
    //     return types;
    // }
}
