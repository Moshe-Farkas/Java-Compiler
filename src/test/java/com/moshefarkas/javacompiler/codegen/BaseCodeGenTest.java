package com.moshefarkas.javacompiler.codegen;

import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.moshefarkas.generated.Java8Lexer;
import com.moshefarkas.generated.Java8Parser;
import com.moshefarkas.javacompiler.ast.astgen.ClassVisitor;
import com.moshefarkas.javacompiler.ast.nodes.ClassNode;
import com.moshefarkas.javacompiler.semanticanalysis.SemanticAnalysis;
import com.moshefarkas.javacompiler.symboltable.SymbolTable;

public class BaseCodeGenTest {

    private String startSource = "public class Test {\r\n" + //
            "\tpublic void method() {";
    private String endSource = "\t}\r\n" + //
            "}";

    private List<Integer> byteCode;
        
    protected void compile(String source) {
        SymbolTable.getInstance().test_reset();
        byteCode = new ArrayList<>();
        source = startSource + source + endSource;
        CharStream input = CharStreams.fromString(source);
        Java8Lexer lexer = new Java8Lexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Java8Parser parser = new Java8Parser(tokens);
        ParseTree tree = parser.compilationUnit(); 
        if (parser.getNumberOfSyntaxErrors() > 0) {
            return;
        }
        ClassVisitor astGen = new ClassVisitor();
        astGen.visit(tree);
        ClassNode ast = astGen.currentClass;
        new SemanticAnalysis(ast);
        if (SemanticAnalysis.hadErr) {
            System.out.println("seman had error. cannot tets code gen");
            System.exit(1);
            return;
        }
        ClassGenVisitor cgv = new ClassGenVisitor();
        cgv.visit(ast);
        ClassReader classReader = new ClassReader(cgv.classWriter.toByteArray());

        org.objectweb.asm.ClassVisitor classVisitor = new org.objectweb.asm.ClassVisitor(Opcodes.ASM8) {

            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
                    String[] exceptions) {

                return new MethodVisitor(Opcodes.ASM8) {
                    @Override
                    public void visitInsn(int opcode) {
                        if (opcode != Opcodes.RETURN)   
                            // byteCode.write(opcode);
                            byteCode.add(opcode);
                    }

                    @Override
                    public void visitVarInsn(int opcode, int varIndex) {
                        super.visitVarInsn(opcode, varIndex);
                    }
                };
            }
        };
        classReader.accept(classVisitor, 0);
    }

    protected void compareOps(byte[] ops) {
        byte[] bytes = new byte[byteCode.size()];
        for (int i = 0; i < bytes.length; i++) {
            int k = byteCode.get(i);
            bytes[i] = (byte)k;
        }

        // printByteCode();

        assertArrayEquals(ops, bytes);
    }

    private void printByteCode() {
        for (int op : byteCode) {
            System.out.print(getOpcodeName(op) + " ");
        }
        System.out.println();
    }

    protected byte[] byteArray(int ...opcodes) {
        ByteArrayOutputStream bArr = new ByteArrayOutputStream();
        for (int op : opcodes) {
            bArr.write(op);
        }
        return bArr.toByteArray();
    }

    public static String getOpcodeName(int opcode) {
        try {
            // Get all fields from the Opcodes class
            Field[] fields = Opcodes.class.getFields();
            // Iterate through the fields
            for (Field field : fields) {
                // Ensure we can access private fields
                // field.setAccessible(true);
                // If the field's value matches the opcode
                if (field.getType() == int.class) {
                    if (field.getInt(null) == opcode) {
                        return field.getName();
                    }
                }
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
        // Return "UNKNOWN" if opcode not found
        return "UNKNOWN";
    }
}
