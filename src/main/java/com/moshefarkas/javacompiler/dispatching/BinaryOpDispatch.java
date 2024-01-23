package com.moshefarkas.javacompiler.dispatching;

import java.util.HashMap;
import java.util.Map;

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

    public BinaryOpDispatch(String op) {
        this.op = op;
    }

    @Override
    public byte[] dispatchForInt() {
        switch (op) {
            case "+":
                return new byte[] {getOp("iadd")};
            case "-":
                return new byte[] {getOp("isub")};
        }
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'dispatchForInt'");
    }

    @Override
    public byte[] dispatchForFloat() {
        switch (op) {
            case "+":
                return new byte[] {getOp("fadd")};
            case "-":
                return new byte[] {getOp("fsub")};
        }
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'dispatchForFloat'");
    }
}
