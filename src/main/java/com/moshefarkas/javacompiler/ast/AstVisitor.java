package com.moshefarkas.javacompiler.ast;

import com.moshefarkas.javacompiler.ast.nodes.ClassNode;
import com.moshefarkas.javacompiler.ast.nodes.MethodNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.ExpressionNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.StatementNode;

public interface AstVisitor<T> {
    // public T visit(AstNode node);
    public T visitClassNode(ClassNode node);
    public T visitMethodNode(MethodNode node);
    public T visitExpressionNode(ExpressionNode node);
    public T visitStatementNode(StatementNode node);
}
