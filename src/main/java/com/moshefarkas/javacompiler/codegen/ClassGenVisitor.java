package com.moshefarkas.javacompiler.codegen;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.moshefarkas.javacompiler.ast.BaseAstVisitor;
import com.moshefarkas.javacompiler.ast.nodes.ClassNode;
import com.moshefarkas.javacompiler.ast.nodes.MethodNode;

public class ClassGenVisitor extends BaseAstVisitor {
    
    public ClassWriter classWriter;

    @Override
    public void visitClassNode(ClassNode node) {
        classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

        classWriter.visit(
            Opcodes.V1_8,   // class format version
            Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER,  
            node.className, // class name 
            null, // 
            "java/lang/Object", // super name
            null // interfaces
        ); 

        super.visitClassNode(node);


        // temp remove 
        emitMain();
        classWriter.visitEnd();
    }

    @Override
    public void visitMethodNode(MethodNode node) {
        int accessMod = Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC;
        String descriptor = "()V";
        String signature = "()V";
        MethodVisitor method = classWriter.visitMethod(
            accessMod,
            node.methodName, 
            descriptor, 
            signature, 
            null
        );
        new MethodGenVisitor(method).visitMethodNode(node);
    }

    private void emitMain() {
        MethodVisitor main = classWriter.visitMethod(
            Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC,
            "main",
            "([Ljava/lang/String;)V", 
            "([Ljava/lang/String;)V",  // string[] args
            null
        );
        main.visitCode();

        // main.visitMethodInsn(
        //     Opcodes.INVOKESTATIC, 
        //     "Demo", 
        //     "meth", 
        //     "()V", 
        //     false
        // );

        main.visitInsn(Opcodes.RETURN);
    }
}
