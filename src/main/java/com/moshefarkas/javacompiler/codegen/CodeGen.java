package com.moshefarkas.javacompiler.codegen;

import java.util.List;
import java.util.Stack;

import com.moshefarkas.javacompiler.Value.Type;
import com.moshefarkas.javacompiler.VarInfo;
import com.moshefarkas.javacompiler.dispatching.LiteralDispatcher;
import com.moshefarkas.javacompiler.dispatching.NegateDispatcher;
import com.moshefarkas.javacompiler.dispatching.StoreDispatcher;
import com.moshefarkas.javacompiler.irgeneration.IR;
import com.moshefarkas.javacompiler.irgeneration.IR.Op;
import com.moshefarkas.javacompiler.irgeneration.IrEntry;

public class CodeGen {
    // code gen needs access to the symbol table and the IR
    private final IR code; 

    public CodeGen(IR code) {
        this.code = code;
    }

    public void run() {
        List<IrEntry> entries = code.code;
        Stack<Type> typeStack = new Stack<>();
        System.out.println("-------");
        code.debugPrintCode();
        System.out.println("-------");
        for (IrEntry entry : entries) {
            Op op = entry.op;
            switch (op) {
                case LITERAL:
                    System.out.print("literal: ");
                    typeStack.push(entry.operands[0].valueType);
                    byte[] code = new LiteralDispatcher(entry.operands[0].value).dispatch();
                    printBytes(code);

                    break;

                case STORE:
                    System.out.print("store: ");
                    printBytes(new StoreDispatcher((VarInfo)entry.operands[0].value).dispatch());
                    break;

                case LOAD:
                    System.out.println("load: " + entry.operands[0]);
                    typeStack.push(entry.operands[0].valueType);
                    break;
                
                case NEGATE:
                    System.out.print("negate: ");
                    Type type = typeStack.pop();
                    printBytes(new NegateDispatcher(type).dispatch());
                    break;
            }
        }
    }

    private void printBytes(byte[] bytes) {
        for (byte b : bytes) {
            System.out.print(Integer.toHexString(b) + " ");
        }
        System.out.println();
    }
}
