package com.moshefarkas.javacompiler.irgeneration;

import java.util.ArrayList;
import java.util.List;

import com.moshefarkas.javacompiler.Value;

public class IR {

    public final List<IrEntry> code = new ArrayList<>();

    public static enum Op {
        LITERAL,
        STORE,
        LOAD,
        ADD, SUB, MUL, DIV,
        NEGATE,
    }

    public void debugPrintCode() {
        for (IrEntry irEntry : code) {
            System.out.println(irEntry);
        }
    }

    public void addOP(Op operation, Value... operands) {
        if (operands != null) {
            code.add(new IrEntry(operation, operands));
        } else {
            code.add(new IrEntry(operation));
        }
    }
}
