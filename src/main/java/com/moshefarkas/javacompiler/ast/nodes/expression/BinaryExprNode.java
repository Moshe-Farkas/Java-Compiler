package com.moshefarkas.javacompiler.ast.nodes.expression;

import com.moshefarkas.javacompiler.ast.AstVisitor;

public class BinaryExprNode extends ExpressionNode {
    public String op;
    public ExpressionNode right;
    public ExpressionNode left;
    
    public void setOp(String op) {
        this.op = op;
    }

    public void setRight(ExpressionNode right) {
        this.right = right;
        addChild(right);
    }

    public void setLeft(ExpressionNode left) {
        this.left = left;
        addChild(left);
    }

    @Override
    public void accept(AstVisitor v) {
        v.visitBinaryExprNode(this);
    }

    @Override
    public String toString() {
        return "op: " + op + " left: " + left + " right: " + right;
    }
}
