package com.moshefarkas.javacompiler.symboltable;

import java.util.HashMap;
import java.util.Map;

import com.moshefarkas.javacompiler.ast.nodes.FieldNode;

public class FieldManager {
    private Map<String, FieldNode> fields;

    public FieldManager() {
        fields = new HashMap<>();
    }
    
    public void addField(String fieldName, FieldNode node) {
        fields.put(fieldName, node);
    }
    
    public boolean hasField(String field) {
        return fields.containsKey(field);
    }

    public FieldNode getField(String field) {
        if (!hasField(field)) 
            return null;
        return fields.get(field);
    }

    @Override 
    public String toString() {
        return fields.toString();
    }
}
