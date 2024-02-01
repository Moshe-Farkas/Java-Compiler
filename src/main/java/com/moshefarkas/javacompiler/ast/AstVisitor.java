package com.moshefarkas.javacompiler.ast;

import com.moshefarkas.javacompiler.ast.nodes.ClassNode;
import com.moshefarkas.javacompiler.ast.nodes.MethodNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.ExpressionNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.StatementNode;

public interface AstVisitor {
    // public T visit(AstNode node);
    public void visitClassNode(ClassNode node);
    public void visitMethodNode(MethodNode node);
    public void visitExpressionNode(ExpressionNode node);
    public void visitStatementNode(StatementNode node);
}
