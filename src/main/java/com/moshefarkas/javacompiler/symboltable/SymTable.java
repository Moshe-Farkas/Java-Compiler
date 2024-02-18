package com.moshefarkas.javacompiler.symboltable;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Type;

import com.moshefarkas.javacompiler.VarInfo;

public class SymTable {
    private static SymTable instance;
    private SymTable () {
        scopes = new HashMap<>();
        currScopeId = 0;
        // push a global scoop
        Scope globalScope = new Scope(0, null);
        scopes.put(currScopeId++, globalScope);
        currScope = globalScope;
    }

    public static SymTable getInstance() {
        if (instance == null) {
            instance = new SymTable();
        }
        return instance;
    }
    private Scope currScope;
    private Map<Integer, Scope> scopes;
    private int currScopeId;

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
        Scope newScope = new Scope(currScope.getNextLocalIndex(), currScope);
        currScope = newScope;
        scopes.put(currScopeId++, newScope);
    }

    public void exitScope() {
        currScope = currScope.getParent();
    }

    public void addVar(VarInfo var) {
        currScope.addVar(var);
    }

    public boolean hasVar(String name) {
        return currScope.hasVar(name);
    }

    public VarInfo getVarInfo(String name) {
        return currScope.getVarInfo(name);
    }

    public Type getVarType(String name) {
        return currScope.getVarType(name);
    }

    public void resetScopes() {
        currScopeId = 0;
        enterScope();
    }

    public void enterScope() {
        currScope = scopes.get(currScopeId++);
    }
}
