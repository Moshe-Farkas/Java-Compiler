package com.moshefarkas.javacompiler.dispatching;

import com.moshefarkas.javacompiler.VarInfo;

public class LoadDispatcher extends OpTypeDispatch {
    static {
        // int
        addOp("iload", (byte)0x15);
        addOp("iload_0", (byte)0x1a);
        addOp("iload_1", (byte)0x1b);
        addOp("iload_2", (byte)0x1c);
        addOp("iload_3", (byte)0x1d);
        // -------------------------------
        addOp("fload", (byte)0x17);
    }

    private final VarInfo varInfo;

    public LoadDispatcher(VarInfo varInfo) {
        // need to dispatch load based on Value.type and lookup its index
        this.varInfo = varInfo;
    }

    @Override
    public byte[] dispatch() {
        switch (varInfo.type) {
            case INT:
                return dispatchForInt();
            case FLOAT:
                return dispatchForFloat();
            default: throw new UnsupportedOperationException(" ::::: " + varInfo.type);
        }
    }

    private byte[] dispatchForInt() {
        if (varInfo.localIndex >= 0 && varInfo.localIndex <= 3) {
            String op = "iload_" + varInfo.localIndex;
            return new byte[] {getOp(op)};
        }
        return new byte[] {getOp("iload"), (byte)varInfo.localIndex};
    }

    private byte[] dispatchForFloat() {
        throw new UnsupportedOperationException("Unimplemented method 'dispatchForFloat'");
    }
}
