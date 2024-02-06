package com.moshefarkas.javacompiler.ast.nodes.expression;

import com.moshefarkas.javacompiler.ast.AstVisitor;

public class LiteralExprNode extends ExpressionNode {
    public Object value;

    public void setValue(Object value) {
        this.value = value;
    }

    @Override 
    public String toString() {
        return "Type: " + exprType + ", value: " + value;
    }

    @Override
    public void accept(AstVisitor v) {
        v.visitLiteralExprNode(this);
    }
}
