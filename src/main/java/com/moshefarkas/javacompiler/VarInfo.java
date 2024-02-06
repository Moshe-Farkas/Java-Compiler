package com.moshefarkas.javacompiler;

import org.objectweb.asm.Type;

public class VarInfo {
    public Type type;
    public String name;
    public boolean initialized = false;
    public int localIndex;

    @Override
    public String toString() {
        return "Name: "          + name +
               ", Type: "        + type +
               ", Initialized: " + initialized + 
               ", Index: "       + localIndex;
    }
}
