package com.moshefarkas.javacompiler.dispatching;

import com.moshefarkas.javacompiler.VarInfo;

public class StoreDispatcher extends OpTypeDispatch {

    static {
        addOp("istore", (byte)0x36);
        addOp("istore_0", (byte)0x3b);
        addOp("istore_1", (byte)0x3c);
        addOp("istore_2", (byte)0x3d);
        addOp("istore_3", (byte)0x3e);
    }

    // the varInfo to know which index the var belongs to.
    private final VarInfo var;

    public StoreDispatcher(VarInfo var) {
        this.var = var;
    }

    @Override
    public byte[] dispatch() {
        switch (var.type) {
            case INT:
                return dispatchForInt();
        }
        throw new UnsupportedOperationException(":: " + var.type);
    }

    private byte[] dispatchForInt() {
        // istore
        // need to lookup the index
        if (var.localIndex >= 0 && var.localIndex <= 3) {
            String op = "istore_" + var.localIndex;
            return new byte[] {getOp(op)};
        } else {
            return new byte[] {getOp("istore"), (byte)var.localIndex};
        }
    }
}
