package com.moshefarkas.javacompiler.ast;

import com.moshefarkas.javacompiler.ast.nodes.ClassNode;
import com.moshefarkas.javacompiler.ast.nodes.MethodNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.ExpressionNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.StatementNode;

public class BaseAstVisitor implements AstVisitor {

    @Override
    public void visitClassNode(ClassNode node) {
        node.visitChildren(this);
    }

    @Override
    public void visitExpressionNode(ExpressionNode node) {
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
    
}
