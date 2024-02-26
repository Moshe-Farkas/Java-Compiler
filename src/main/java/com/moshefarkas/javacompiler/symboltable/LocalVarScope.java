package com.moshefarkas.javacompiler.symboltable;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Type;

import com.moshefarkas.javacompiler.ast.nodes.statement.LocalVarDecStmtNode;

public class LocalVarScope {
    private Map<String, LocalVarDecStmtNode> vars;
    private LocalVarScope parent;
    private int nextLocalIndex;
    
    public LocalVarScope(int localIndex, LocalVarScope parent) {
        this.nextLocalIndex = localIndex;
        this.parent = parent;

        vars = new HashMap<>();
    }

    public void addVar(LocalVarDecStmtNode node) {
        node.localIndex = nextLocalIndex++;
        vars.put(node.varName, node);
    }

    public LocalVarDecStmtNode getVarInfo(String varName) {
        if (vars.containsKey(varName)) {
            return vars.get(varName);
        }
        if (parent == null) {
            return null;
        }
        return parent.getVarInfo(varName);
    }

    public boolean hasVar(String varName) {
        return getVarInfo(varName) != null;
    }

    public Type getVarType(String varName) {
        LocalVarDecStmtNode v = getVarInfo(varName);
        return v == null ? null : v.varType;
    }

    public int getNextLocalIndex() {
        return nextLocalIndex;
    }

    public LocalVarScope getParent() {
        return parent;
    }

    @Override 
    public String toString() {
        int parentHashCode = parent != null ? parent.hashCode() : -1;
        return "thisHashCode: " + hashCode() + " parent hashCode: " + parentHashCode + " nextLocalIndex: " + nextLocalIndex + ", vars: " + vars;
    }
}
