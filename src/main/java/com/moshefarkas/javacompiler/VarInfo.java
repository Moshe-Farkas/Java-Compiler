package com.moshefarkas.javacompiler;

import org.objectweb.asm.Type;

public class VarInfo {
    public Type type;
    public String name;
    public boolean initialized = false;
    public int localIndex;
    // array info
    public boolean isArray = false;
    public int dims;

    @Override
    public String toString() {
        return "Name: "          + name +
               ", Type: "        + type +
               ", Initialized: " + initialized + 
               ", Index: "       + localIndex + 
               ", isArray: "     + isArray    +
               ", dims: "        + dims;
    }
}
