package com.moshefarkas.javacompiler.ast.nodes.expression;

import com.moshefarkas.javacompiler.ast.AstVisitor;

public class BinaryExprNode extends ExpressionNode {
    public String op;
    public ExpressionNode right;
    public ExpressionNode left;

    public BinaryExprNode(ExpressionNode left, ExpressionNode right, String op) {
        addChild(right);
        addChild(left);
    }
    
    @Override
    public void accept(AstVisitor v) {
        v.visitBinaryExprNode(this);
    }
}
