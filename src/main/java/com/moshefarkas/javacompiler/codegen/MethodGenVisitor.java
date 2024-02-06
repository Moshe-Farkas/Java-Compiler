package com.moshefarkas.javacompiler.codegen;

import java.util.Stack;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.moshefarkas.javacompiler.SymbolTable;
import com.moshefarkas.javacompiler.VarInfo;
import com.moshefarkas.javacompiler.ast.BaseAstVisitor;
import com.moshefarkas.javacompiler.ast.nodes.MethodNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.AssignExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.BinaryExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.IdentifierExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.LiteralExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.UnaryExprNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.IfStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.LocalVarDecStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.WhileStmtNode;

public class MethodGenVisitor extends BaseAstVisitor {

    private MethodVisitor methodVisitor;
    private Stack<Label> labelStack = new Stack<>();

    public MethodGenVisitor(MethodVisitor methodVisitor) {
        this.methodVisitor = methodVisitor;
    }

    private void emitTypeCast(Type targetType, Type toCastType) {
        if (targetType == toCastType) 
            return;   // no need to cast to same type

        if (targetType == Type.FLOAT_TYPE) {
            methodVisitor.visitInsn(toCastType.getOpcode(Opcodes.I2F));
        } else if (targetType == Type.INT_TYPE) {
            methodVisitor.visitInsn(toCastType.getOpcode(Opcodes.F2I));
        }
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
            VarInfo var = SymbolTable.getInstance().getInfo(node.var.name);
            methodVisitor.visitVarInsn(var.type.getOpcode(Opcodes.ISTORE), var.localIndex);
        }
    }
    
    @Override
    public void visitAssignExprNode(AssignExprNode node) {
        super.visitAssignExprNode(node);
        VarInfo var = SymbolTable.getInstance().getInfo(node.varName);
        methodVisitor.visitVarInsn(var.type.getOpcode(Opcodes.ISTORE), var.localIndex);
    }

    @Override
    public void visitBinaryExprNode(BinaryExprNode node) {
        Type leftExprType = node.left.exprType;
        Type rightExprType = node.right.exprType;

        visit(node.left);
        emitTypeCast(node.exprType, leftExprType);
        visit(node.right);
        emitTypeCast(node.exprType, rightExprType);
        
        switch (node.op) {
            case PLUS:
                methodVisitor.visitInsn(node.exprType.getOpcode(Opcodes.IADD));
                break;
            case MINUS:
                methodVisitor.visitInsn(node.exprType.getOpcode(Opcodes.ISUB));
                break;
            case DIV:
                methodVisitor.visitInsn(node.exprType.getOpcode(Opcodes.IDIV));
                break;
            case MUL:
                methodVisitor.visitInsn(node.exprType.getOpcode(Opcodes.IMUL));
                break;
            case MOD:
                methodVisitor.visitInsn(Opcodes.IREM);
                break;
            case GT:
                methodVisitor.visitJumpInsn(Opcodes.IF_ICMPLE, labelStack.pop());
                break;
            case GT_EQ:
                methodVisitor.visitJumpInsn(Opcodes.IF_ICMPLT, labelStack.pop());
                break;
            case LT:
                methodVisitor.visitJumpInsn(Opcodes.IF_ICMPGE, labelStack.pop());
                break;
            case LT_EQ:
                methodVisitor.visitJumpInsn(Opcodes.IF_ICMPGT, labelStack.pop());
                break;
        }
    }

    @Override
    public void visitUnaryExprNode(UnaryExprNode node) {
        visit(node.expr);
        switch (node.op) {
            case MINUS:
                methodVisitor.visitInsn(Opcodes.INEG);
                break;
        }
    }

    @Override
    public void visitLiteralExprNode(LiteralExprNode node) {
        methodVisitor.visitLdcInsn(node.value);
    }

    @Override
    public void visitIdentifierExprNode(IdentifierExprNode node) {
        VarInfo var = SymbolTable.getInstance().getInfo(node.varName);
        int op = var.type.getOpcode(Opcodes.ILOAD);
        methodVisitor.visitVarInsn(op, var.localIndex);
    }

    @Override
    public void visitIfStmtNode(IfStmtNode node) {
        Label label = new Label();
        labelStack.push(label);

        visit(node.condition);
        visit(node.statement);
        
        methodVisitor.visitLabel(label);
    }

    @Override
    public void visitWhileStmtNode(WhileStmtNode node) {
        Label toEnd = new Label();
        labelStack.push(toEnd);

        Label jumpBack = new Label();
        methodVisitor.visitLabel(jumpBack);
        
        visit(node.condition);
        visit(node.statement);
        methodVisitor.visitJumpInsn(Opcodes.GOTO, jumpBack);

        methodVisitor.visitLabel(toEnd);
    }
}
