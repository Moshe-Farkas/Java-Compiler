package com.moshefarkas.javacompiler.codegen;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.moshefarkas.javacompiler.MethodInfo;
import com.moshefarkas.javacompiler.ast.BaseAstVisitor;
import com.moshefarkas.javacompiler.ast.nodes.ClassNode;
import com.moshefarkas.javacompiler.ast.nodes.MethodNode;
import com.moshefarkas.javacompiler.symboltable.MethodManager;
import com.moshefarkas.javacompiler.symboltable.SymbolTable;

public class ClassGenVisitor extends BaseAstVisitor {
    
    public ClassWriter classWriter;

    private String currMethod;

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
        currMethod = node.methodName;
        MethodManager.getInstance().getSymbolTable(currMethod).resetScopes();
        int accessMod = 0;
        for (int modifer : node.methodModifiers) {
            accessMod += modifer;
        }
        String descriptor = MethodManager.getInstance().getMethodDescriptor(currMethod);

        MethodVisitor method = classWriter.visitMethod(
            accessMod,
            node.methodName, 
            descriptor, 
            descriptor, 
            null
        );
        new MethodGenVisitor(method, currMethod).visitMethodNode(node);
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
        main.visitInsn(Opcodes.RETURN);
    }
}
