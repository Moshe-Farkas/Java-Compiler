package com.moshefarkas.javacompiler.ast;

import com.moshefarkas.javacompiler.ast.nodes.AstNode;
import com.moshefarkas.javacompiler.ast.nodes.ClassNode;
import com.moshefarkas.javacompiler.ast.nodes.MethodNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.AssignExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.BinaryExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.CallExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.ExpressionNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.IdentifierExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.LiteralExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.UnaryExprNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.BlockStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.ExprStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.IfStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.LocalVarDecStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.StatementNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.WhileStmtNode;

public class BaseAstVisitor implements AstVisitor {

    @Override
    public void visitAssignExprNode(AssignExprNode node) {
        node.visitChildren(this);
    }

    @Override
    public void visitBinaryExprNode(BinaryExprNode node) {
        node.visitChildren(this);
    }

    @Override
    public void visitBlockStmtNode(BlockStmtNode node) {
        node.visitChildren(this);
    }

    @Override
    public void visitCallExprNode(CallExprNode node) {
        node.visitChildren(this);
    }

    @Override
    public void visitExpressionNode(ExpressionNode node) {
        node.visitChildren(this);
    }

    @Override
    public void visitIfStmtNode(IfStmtNode node) {
        node.visitChildren(this);
    }

    @Override
    public void visitExprStmtNode(ExprStmtNode node) {
        node.visitChildren(this);
    }

    @Override
    public void visitLiteralExprNode(LiteralExprNode node) {
        node.visitChildren(this);
    }

    @Override
    public void visitLocalVarDecStmtNode(LocalVarDecStmtNode node) {
        node.visitChildren(this);
    }

    @Override
    public void visitWhileStmtNode(WhileStmtNode node) {
        node.visitChildren(this);
    }

    @Override
    public void visitClassNode(ClassNode node) {
        node.visitChildren(this);
    }

    @Override
    public void visitIdentifierExprNode(IdentifierExprNode node) {
        node.visitChildren(this);
    }

    @Override
    public void visitMethodNode(MethodNode node) {
        node.visitChildren(this);
    }

    @Override
    public void visitStatementNode(StatementNode node) {
        node.visitChildren(this);
    }

    @Override
    public void visitUnaryExprNode(UnaryExprNode node) {
        node.visitChildren(this);
    }
    
    public void visit(AstNode node) {
        node.accept(this);
    }
}
