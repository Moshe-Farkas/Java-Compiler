package com.moshefarkas.javacompiler.dispatching;

import java.util.HashMap;
import java.util.Map;

public abstract class OpTypeDispatch {
    // needs a method to dipatch
    protected static Map<String, Byte> ops;
    protected static void addOp(String name, byte code) {
        if (ops == null) {
            ops = new HashMap<>();
        }
        ops.put(name, code);
    }

    protected static byte getOp(String name) {
        if (!ops.containsKey(name))
            throw new RuntimeException("gave wrong name for an opcode. " + name + " does not exists");
        return ops.get(name);
    }

    abstract public byte[] dispatch();
}
