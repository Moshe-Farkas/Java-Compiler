package com.moshefarkas.javacompiler.symboltable;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Type;

import com.moshefarkas.javacompiler.VarInfo;

public class Scope {
    private Map<String, VarInfo> vars;
    private Scope parent;
    private int nextLocalIndex;
    
    public Scope(int localIndex, Scope parent) {
        this.nextLocalIndex = localIndex;
        this.parent = parent;

        vars = new HashMap<>();
    }

    public void addVar(VarInfo var) {
        var.localIndex = nextLocalIndex++;
        vars.put(var.name, var);
    }

    public VarInfo getVarInfo(String varName) {
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
        VarInfo v = getVarInfo(varName);
        return v == null ? null : v.type;
    }

    public int getNextLocalIndex() {
        return nextLocalIndex;
    }

    public Scope getParent() {
        return parent;
    }

    @Override 
    public String toString() {
        int parentHashCode = parent != null ? parent.hashCode() : -1;
        return "thisHashCode: " + hashCode() + " parent hashCode: " + parentHashCode + " nextLocalIndex: " + nextLocalIndex + ", vars: " + vars;
    }
}
