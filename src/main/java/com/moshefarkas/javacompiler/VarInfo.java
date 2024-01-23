package com.moshefarkas.javacompiler;

import com.moshefarkas.javacompiler.semanticanalysis.TypeCheckVisitor.Type;

public class VarInfo {
    public Type type;
    public String name;
    public boolean initialized = false;

    @Override
    public String toString() {
        return "Name: " + name +
               ", Type: " + type +
               ", Initialized: " + initialized;
    }
}
