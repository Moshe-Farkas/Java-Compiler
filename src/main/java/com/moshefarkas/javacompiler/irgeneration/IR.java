package com.moshefarkas.javacompiler.irgeneration;

import java.util.ArrayList;
import java.util.List;

public class IR {

    private final List<IrEntry> code = new ArrayList<>();
    public static enum Op {
        LITERAL,
        STORE,
        LOAD,
        ADD, SUB, MUL, DIV
    }

    public void debugPrintCode() {
        for (IrEntry irEntry : code) {
            System.out.println(irEntry);
        }
    }

    public void addOP(Op operation, Object... operands) {
        if (operands != null) {
            code.add(new IrEntry(operation, operands));
        } else {
            code.add(new IrEntry(operation));
        }
    }

    private class IrEntry {
        public Op op;    
        public Object[] operands;
        public IrEntry(Op op, Object[] operands) {
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
                for (Object oper : operands) {
                    ops += oper + ", ";
                }
                ops = ops.substring(0, ops.length() - 2);
                return op + ops;
            }
            return ""+op;
        }
    }
}
