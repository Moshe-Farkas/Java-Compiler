package com.moshefarkas.javacompiler.ast;

import com.moshefarkas.javacompiler.ast.nodes.ClassNode;
import com.moshefarkas.javacompiler.ast.nodes.MethodNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.ArrAccessExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.ArrayInitializerNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.AssignExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.BinaryExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.CallExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.CastExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.ExpressionNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.IdentifierExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.LiteralExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.UnaryExprNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.BlockStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.ExprStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.IfStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.LocalVarDecStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.ReturnStmt;
import com.moshefarkas.javacompiler.ast.nodes.statement.StatementNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.WhileStmtNode;

public interface AstVisitor {
    public void visitClassNode(ClassNode node);
    public void visitMethodNode(MethodNode node);
    public void visitStatementNode(StatementNode node);
    public void visitExprStmtNode(ExprStmtNode node);
    public void visitWhileStmtNode(WhileStmtNode node);
    public void visitBlockStmtNode(BlockStmtNode node);
    public void visitIfStmtNode(IfStmtNode node);
    public void visitLocalVarDecStmtNode(LocalVarDecStmtNode node);
    public void visitReturnStmt(ReturnStmt node);
    public void visitExpressionNode(ExpressionNode node);
    public void visitAssignExprNode(AssignExprNode node);
    public void visitBinaryExprNode(BinaryExprNode node);
    public void visitCallExprNode(CallExprNode node);
    public void visitLiteralExprNode(LiteralExprNode node);
    public void visitUnaryExprNode(UnaryExprNode node);
    public void visitCastExprNode(CastExprNode node);
    public void visitIdentifierExprNode(IdentifierExprNode node);
    public void visitArrAccessExprNode(ArrAccessExprNode node);
    public void visitArrayInitializer(ArrayInitializerNode node);
}
