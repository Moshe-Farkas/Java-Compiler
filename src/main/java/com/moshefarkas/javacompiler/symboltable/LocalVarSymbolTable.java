package com.moshefarkas.javacompiler.symboltable;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Type;

import com.moshefarkas.javacompiler.ast.nodes.statement.LocalVarDecStmtNode;

public class LocalVarSymbolTable {
    
    private LocalVarScope currScope;
    private Map<Integer, LocalVarScope> scopes;
    private int currScopeLevel;
    private int localIndexStartingPoint;

    public LocalVarSymbolTable (int localIndexStartingPoint) {
        this.localIndexStartingPoint = localIndexStartingPoint;
        scopes = new HashMap<>();
        currScopeLevel = 0;
    }

    public void printScope() {
        System.out.println(currScope);
    }

    public void createNewScope() {
        LocalVarScope newScope;
        if (currScope == null) {
            newScope = new LocalVarScope(localIndexStartingPoint, null);
        } else {
            newScope = new LocalVarScope(currScope.getNextLocalIndex(), currScope);
        }
        currScope = newScope;
        scopes.put(currScopeLevel++, newScope);
    }

    public void exitScope() {
        currScope = currScope.getParent();
    }

    public void addLocal(LocalVarDecStmtNode var) {
        currScope.addVar(var);
    }

    public boolean hasVar(String name) {
        return currScope.hasVar(name);
    }          
               
    public void resetScopes() {
        currScopeLevel = 0;
    }          

    public void enterScope() {
        currScope = scopes.get(currScopeLevel++);
    }

    public LocalVarDecStmtNode getVarDeclNode(String name) {
        return currScope.getVarInfo(name);
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        for (Map.Entry<Integer, LocalVarScope> scope : scopes.entrySet()) {
            res.append("\nscope id: " + scope.getKey());
            res.append("\n " + scope.getValue());
        }
        return res.toString();
    }

    public void debugPrintTable() {
        System.out.println("-----------------");
        for (Map.Entry<Integer, LocalVarScope> scope : scopes.entrySet()) {
            System.out.println("scope id: " + scope.getKey());
            System.out.println("  " + scope.getValue());
        }
        System.out.println("-----------------");
    }
}
