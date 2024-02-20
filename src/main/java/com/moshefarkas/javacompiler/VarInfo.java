package com.moshefarkas.javacompiler;

import org.objectweb.asm.Type;

public class VarInfo {
    public Type type;
    public String name;
    public int localIndex = -1;
    public boolean hasValue = false;

    @Override
    public String toString() {
        return "Name: "          + name +
               ", Type: "        + type +
               ", HasValue: "    + hasValue + 
               ", Index: "       + localIndex;
    }
}
