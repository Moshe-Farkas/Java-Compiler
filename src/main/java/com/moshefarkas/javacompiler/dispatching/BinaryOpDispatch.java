package com.moshefarkas.javacompiler.dispatching;

import com.moshefarkas.javacompiler.Value.Type;

public class BinaryOpDispatch extends OpTypeDispatch {
    static {
        // add
        addOp("iadd", (byte)0x60);
        addOp("fadd", (byte)0x62);

        addOp("fsub", (byte)0x66);
        addOp("isub", (byte)0x64);

        
        // -------------------------

        // // mul
        // ops.put("iadd", (byte)0x60);
        // ops.put("dadd", (byte)0x63);
        // ops.put("fadd", (byte)0x62);
        // // -------------------------
        // // div
        // ops.put("iadd", (byte)0x60);
        // ops.put("dadd", (byte)0x63);
        // ops.put("fadd", (byte)0x62);
        // // -------------------------
    }

    private String op;
    private Type type;

    public BinaryOpDispatch(Type type, String op) {
        this.op = op;
    }

    @Override
    public byte[] dispatch() {
        switch (type) {
            case INT:
                return dispatchForInt();
            case FLOAT:
                return dispatchForFloat();
            default: throw new UnsupportedOperationException("::: "+  type);
        }
    }

    private byte[] dispatchForInt() {
        switch (op) {
            case "+":
                return new byte[] {getOp("iadd")};
            case "-":
                return new byte[] {getOp("isub")};
        }
        throw new UnsupportedOperationException("Unimplemented method 'dispatchForInt'");
    }

    private byte[] dispatchForFloat() {
        switch (op) {
            case "+":
                return new byte[] {getOp("fadd")};
            case "-":
                return new byte[] {getOp("fsub")};
        }
        throw new UnsupportedOperationException("Unimplemented method 'dispatchForFloat'");
    }
}
