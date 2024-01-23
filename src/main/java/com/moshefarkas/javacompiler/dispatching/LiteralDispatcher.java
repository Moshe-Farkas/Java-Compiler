package com.moshefarkas.javacompiler.dispatching;

public class LiteralDispatcher extends OpTypeDispatch {

    static {
        // int
        addOp("iconst_m1", (byte)0x02);
        addOp("iconst_m1", (byte)0x02);
        addOp("iconst_0", (byte)0x03);
        addOp("iconst_1", (byte)0x04);
        addOp("iconst_2", (byte)0x05);
        addOp("iconst_3", (byte)0x06);
        addOp("iconst_4", (byte)0x07);
        addOp("iconst_5", (byte)0x08);

        addOp("sipush", (byte)0x11);
        addOp("bipush", (byte)0x10);
        // --------------------------------
        // float
        addOp("fconst_0", (byte)0x0b);
        addOp("fconst_1", (byte)0x0c);
        addOp("fconst_2", (byte)0x0d);
        
    }

    private final Object input;

    public LiteralDispatcher(Object input) {
        this.input = input;
    }

    @Override
    public byte[] dispatchForInt() {
        int intVal = (int)this.input;

        if (intVal == -1) {
            return new byte[] {getOp("iconst_m1")};
        } else if (intVal >= 0 && intVal <= 5) {
            String op = "iconst_" + intVal;
            return new byte[] {getOp(op)};
        } else if (intVal <= Byte.MAX_VALUE && intVal >= Byte.MIN_VALUE) {
            return new byte[] {getOp("bipush"), (byte)intVal};
        } else if (intVal <= Short.MAX_VALUE && intVal >= Short.MIN_VALUE) {
            byte highByte = (byte)((intVal >> 8) & 0xFF);
            byte lowByte = (byte)(intVal & 0xFF);
            return new byte[] {getOp("sipush"), highByte, lowByte};
        } else {
            // TODO: implement ldc and add intVal to constant pool
        }

        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'dispatchForInt'");
    }

    @Override
    public byte[] dispatchForFloat() {
        float floatVal = (float)this.input;

        if (floatVal >= 0.0 && floatVal <= 2.0) {
            String op = "fconst_" + (int)this.input;
            return new byte[] {getOp(op)};
        } else {
            // TODO: implement ldc and add floatVal to constant pool
        }

        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'dispatchForFloat'");
    }
}
