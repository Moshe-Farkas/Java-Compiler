package com.moshefarkas.javacompiler.dispatching;

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

    private final int varIndex;

    public LoadDispatcher(int index) {
        this.varIndex = index;
    }

    @Override
    public byte[] dispatchForInt() {
        if (varIndex >= 0 && varIndex <= 3) {
            return new byte[] {getOp("iload_" + varIndex)};
        }
        // TODO: dispatch based on varName index and type
        throw new UnsupportedOperationException("Unimplemented method 'dispatchForInt'");
    }

    @Override
    public byte[] dispatchForFloat() {
        // TODO: dispatch based on varName index and type
        throw new UnsupportedOperationException("Unimplemented method 'dispatchForFloat'");
    }
}
