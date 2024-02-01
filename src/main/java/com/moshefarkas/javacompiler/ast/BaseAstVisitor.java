package com.moshefarkas.javacompiler.ast;

import com.moshefarkas.javacompiler.ast.nodes.AstNode;
import com.moshefarkas.javacompiler.ast.nodes.ClassNode;
import com.moshefarkas.javacompiler.ast.nodes.MethodNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.ExpressionNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.StatementNode;

public class BaseAstVisitor<T> implements AstVisitor<T> {

    @Override
    public AstNode visitClassNode(ClassNode node) {
        // AstNodeODO Auto-generated method stub
        node.visitChildren(this);
        return null;
    }

    @Override
    public AstNode visitExpressionNode(ExpressionNode node) {
        // AstNodeODO Auto-generated method stub
        node.visitChildren(this);
        return null;
    }

    @Override
    public AstNode visitMethodNode(MethodNode node) {
        // AstNodeODO Auto-generated method stub
        node.visitChildren(this);
        return null;
    }

    @Override
    public AstNode visitStatementNode(StatementNode node) {
        // AstNodeODO Auto-generated method stub
        node.visitChildren(this);
        return null;
    }
    
}
