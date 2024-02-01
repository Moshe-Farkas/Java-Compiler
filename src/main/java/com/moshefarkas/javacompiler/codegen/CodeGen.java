package com.moshefarkas.javacompiler.codegen;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Stack;

import com.moshefarkas.javacompiler.Value.Type;
import com.moshefarkas.javacompiler.VarInfo;
import com.moshefarkas.javacompiler.dispatching.BinaryOpDispatch;
import com.moshefarkas.javacompiler.dispatching.LiteralDispatcher;
import com.moshefarkas.javacompiler.dispatching.LoadDispatcher;
import com.moshefarkas.javacompiler.dispatching.NegateDispatcher;
import com.moshefarkas.javacompiler.dispatching.StoreDispatcher;
import com.moshefarkas.javacompiler.irgeneration.IR;
import com.moshefarkas.javacompiler.irgeneration.IR.Op;
import com.moshefarkas.javacompiler.irgeneration.IrEntry;

public class CodeGen {
    // code gen needs access to the symbol table and the IR
    private final IR code; 
    private final DataOutputStream dos;
    // private DataOutputStream method;

    public CodeGen(IR code, FileOutputStream fos) throws IOException {
        this.code = code;
        this.dos = new DataOutputStream(fos);

        ByteArrayOutputStream method = compileMethod();

        // temp remove
        ConstantPool.getInstance().insertClass("Demo");
        ConstantPool.getInstance().insertClass("java/lang/Object");


        aggregateFile(method);
    }

    private void aggregateFile(
                                ByteArrayOutputStream method
                               ) throws IOException {

        // u4             magic;
        // u2             minor_version;
        // u2             major_version;
        // u2             constant_pool_count;
        // cp_info        constant_pool[constant_pool_count-1];
        // u2             access_flags;
        // u2             this_class;
        // u2             super_class;
        // u2             interfaces_count;
        // u2             interfaces[interfaces_count];
        // u2             fields_count;
        // field_info     fields[fields_count];
        // u2             methods_count;
        // method_info    methods[methods_count];
        // u2             attributes_count;
        // attribute_info attributes[attributes_count];

        boilerPlate();
        // write cp
        dos.write(ConstantPool.getInstance().toByteArray());

        // access flags
        dos.writeShort(33);

        // this_class change 
        dos.writeShort(1);  // class info index for Demo
        // super_class 
        dos.writeShort(2);  // java.lang.object

        // interface count
        dos.writeShort(0);

        // fields count
        dos.writeShort(0);

        // methods count
        dos.writeShort(1);

        // write method
        dos.write(method.toByteArray());

        // attr count
        dos.writeShort(0);
    }

    private ByteArrayOutputStream compileMethod() throws IOException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final DataOutputStream method = new DataOutputStream(bos);

        List<IrEntry> entries = code.code;
        Stack<Type> typeStack = new Stack<>();
        System.out.println("-------");
        System.out.println("\tIR CODE:");
        code.debugPrintCode();
        System.out.println("-------");
        for (IrEntry entry : entries) {
            Op op = entry.op;
            byte[] code = null;
            switch (op) {
                case LITERAL:
                    System.out.print("literal: ");
                    typeStack.push(entry.operands[0].valueType);
                    code = new LiteralDispatcher(entry.operands[0].value).dispatch();
                    break;

                case STORE:
                    System.out.print("store: ");
                    code = new StoreDispatcher((VarInfo)entry.operands[0].value).dispatch();
                    break;

                case LOAD:
                    System.out.print("load: ");
                    typeStack.push(entry.operands[0].valueType);
                    code = new LoadDispatcher((VarInfo)entry.operands[0].value).dispatch();
                    break;
                
                case NEGATE:
                    System.out.print("negate: ");
                    Type type = typeStack.pop();
                    code = new NegateDispatcher(type).dispatch();
                    break;
                
                case ADD:
                case DIV:
                case MUL:
                    String operator = "";
                    switch (op) {
                        case ADD:
                            operator = "+";
                            break;
                        case DIV:
                            operator = "/";
                            break;
                        case SUB:
                            operator = "-";
                            break;
                        case MUL:
                            operator = "*";
                            break;
                    }
                    System.out.print("add: ");
                    code = new BinaryOpDispatch(typeStack.pop(), operator).dispatch();
                    break;
            }
            printBytes(code);
            method.write(code);
        }

        return bos;
    }

    private void boilerPlate() throws IOException {
        dos.writeInt(0xcafebabe);
        // major and minor
        dos.writeShort(0); 
        dos.writeShort(52); 
        int cpCount = ConstantPool.getInstance().getCount();
        dos.writeShort(cpCount);
    }

    private void printBytes(byte[] bytes) {
        for (byte b : bytes) {
            System.out.print(Integer.toHexString(b & 0xff) + " ");
        }
        System.out.println();
    }
}
