package com.moshefarkas.javacompiler.dispatching;

import com.moshefarkas.javacompiler.Value.Type;

public class NegateDispatcher extends OpTypeDispatch {
    
    static {
        addOp("ineg", (byte)0x74);
    }    


    private Type type;
    
    public NegateDispatcher(Type type) {
        this.type = type;
    }

    @Override
    public byte[] dispatch() {
        switch (type) {
            case INT:
                return dispatchForInt(); 
            default:
                throw new UnsupportedOperationException("::: " + type);
        }
    }

    private byte[] dispatchForInt() {
        return new byte[] {getOp("ineg")};
    }
}
