package com.moshefarkas.javacompiler.ast.nodes.expression;

public class BinaryExprNode extends ExpressionNode {
    public String op;
    public ExpressionNode right;
    public ExpressionNode left;

    public BinaryExprNode(ExpressionNode left, ExpressionNode right, String op) {
        addChild(right);
        addChild(left);
    }
}
