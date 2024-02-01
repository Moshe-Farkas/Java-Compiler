package com.moshefarkas.javacompiler.ast;

import com.moshefarkas.javacompiler.ast.nodes.ClassNode;
import com.moshefarkas.javacompiler.ast.nodes.MethodNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.AssignExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.BinaryExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.CallExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.ExpressionNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.IdentifierExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.LiteralExprNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.BlockStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.IfStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.LocalVarDecStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.StatementNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.WhileStmtNode;

public interface AstVisitor {
    // public T visit(AstNode node);
    public void visitClassNode(ClassNode node);
    public void visitMethodNode(MethodNode node);
    public void visitStatementNode(StatementNode node);
    public void visitWhileStmtNode(WhileStmtNode node);
    public void visitBlockStmtNode(BlockStmtNode node);
    public void visitIfStmtNode(IfStmtNode node);
    public void visitLocalVarDecStmtNode(LocalVarDecStmtNode node);
    public void visitExpressionNode(ExpressionNode node);
    public void visitAssignExprNode(AssignExprNode node);
    public void visitBinaryExprNode(BinaryExprNode node);
    public void visitCallExprNode(CallExprNode node);
    public void visitLiteralExprNode(LiteralExprNode node);
    public void visitIdentifierExprNode(IdentifierExprNode node);
}
