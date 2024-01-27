package com.moshefarkas.javacompiler.irgeneration;

import com.moshefarkas.javacompiler.Value;
import com.moshefarkas.javacompiler.irgeneration.IR.Op;

public class IrEntry {
    public Op op;    
    public Value[] operands;
    public IrEntry(Op op, Value[] operands) {
        this.op = op;
        this.operands = operands;
    }

    public IrEntry(Op op) {
        this.op = op;
    }

    @Override
    public String toString() {
        if (operands != null && operands.length > 0) {
            String ops = " ";
            for (Value oper : operands) {
                ops += oper + ", ";
            }
            ops = ops.substring(0, ops.length() - 2);
            return op + ops;
        }
        return ""+op;
    }
}
