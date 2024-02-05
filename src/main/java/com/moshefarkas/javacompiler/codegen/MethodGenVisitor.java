package com.moshefarkas.javacompiler.codegen;

import org.antlr.v4.parse.ANTLRParser.parserRule_return;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.TraceClassVisitor;

import com.moshefarkas.javacompiler.SymbolTable;
import com.moshefarkas.javacompiler.ast.BaseAstVisitor;
import com.moshefarkas.javacompiler.ast.nodes.MethodNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.AssignExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.BinaryExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.LiteralExprNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.LocalVarDecStmtNode;

public class MethodGenVisitor extends BaseAstVisitor {

    private MethodVisitor methodVisitor;

    public MethodGenVisitor(MethodVisitor methodVisitor) {
        this.methodVisitor = methodVisitor;
    }

    @Override
    public void visitMethodNode(MethodNode node) {
        methodVisitor.visitCode();
        super.visitMethodNode(node);

        // // temp sout
        // methodVisitor.visitFieldInsn(Opcodes.GETSTATIC, 
        //     "java/lang/System", 
        //     "out", 
        //     "Ljava/io/PrintStream;"
        // );

        // methodVisitor.visitVarInsn(Opcodes.ILOAD, 0);
    
        // methodVisitor.visitMethodInsn(
        //     Opcodes.INVOKEVIRTUAL, 
        //     "java/io/PrintStream", 
        //     "println", 
        //     "(I)V", 
        //     false
        // );

        methodVisitor.visitInsn(Opcodes.RETURN);
        methodVisitor.visitMaxs(-1, -1);
        methodVisitor.visitEnd();
    }

    @Override
    public void visitLocalVarDecStmtNode(LocalVarDecStmtNode node) {
        if (node.var.initialized) {
            super.visitLocalVarDecStmtNode(node);
            // placed init one stack
            methodVisitor.visitVarInsn(Opcodes.ISTORE, node.var.localIndex);
        }
    }
    
    @Override
    public void visitAssignExprNode(AssignExprNode node) {
        super.visitAssignExprNode(node);

        int localIndex = SymbolTable.getInstance().getInfo(node.var.varName).localIndex;
        methodVisitor.visitVarInsn(Opcodes.ISTORE, localIndex);
    }

    @Override
    public void visitBinaryExprNode(BinaryExprNode node) {
        // need to emit code based on node's op type
        // visitExpressionNode(node.left);
        // visitExpressionNode(node.right);
        super.visitBinaryExprNode(node);

        switch (node.op) {
            case PLUS:
                methodVisitor.visitInsn(Opcodes.IADD);
                break;
            case MINUS:
                methodVisitor.visitInsn(Opcodes.ISUB);
                break;
            case DIV:
                methodVisitor.visitInsn(Opcodes.IDIV);
                break;
            case MUL:
                methodVisitor.visitInsn(Opcodes.IMUL);
                break;
            case MOD:
                methodVisitor.visitInsn(Opcodes.IREM);
                break;
        }
    }

    @Override
    public void visitLiteralExprNode(LiteralExprNode node) {
        methodVisitor.visitLdcInsn(node.value);
    }
}
