package com.moshefarkas.javacompiler;

import java.util.HashMap;
import java.util.Map;

import com.moshefarkas.javacompiler.Value.Type;

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
        for (Map.Entry<String, VarInfo> entry : vars.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    public void test_reset() {
        instance = new SymbolTable();
    }

    private final Map<String, VarInfo> vars = new HashMap<>();

    public void addLocal(String name, VarInfo varInfo) {
        vars.put(name, varInfo);
    }

    public boolean hasVar(String name) {
        return vars.containsKey(name);
    }

    public Type getType(String name) {
        return vars.get(name).type;
    }

    public VarInfo getInfo(String name) {
        return vars.get(name);
    }
}
