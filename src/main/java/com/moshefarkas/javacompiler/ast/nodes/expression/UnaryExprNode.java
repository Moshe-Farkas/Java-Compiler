package com.moshefarkas.javacompiler.ast.nodes.expression;

import com.moshefarkas.javacompiler.ast.AstVisitor;

public class UnaryExprNode extends ExpressionNode {
    
    public enum UnaryOp {
        MINUS,
        NOT,
        TILDE,
    }
    
    public ExpressionNode expr;
    public UnaryOp op;

    public void setExpr(ExpressionNode expr) {
        this.expr = expr;
        addChild(expr);
    }

    public void setOp(UnaryOp op) {
        this.op = op;
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.visitUnaryExprNode(this);
    }
}
