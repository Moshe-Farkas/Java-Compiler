package com.moshefarkas.javacompiler.dispatching;

import java.util.HashMap;
import java.util.Map;

public abstract class OpTypeDispatch {
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

    // all dispatchers dispatch different things based on wether it's an int or float.
    abstract public byte[] dispatchForInt();
    abstract public byte[] dispatchForFloat();
}
