package com.moshefarkas.javacompiler.ast.nodes.expression;

import com.moshefarkas.javacompiler.Value.Type;
import com.moshefarkas.javacompiler.ast.AstVisitor;

public class LiteralExprNode extends ExpressionNode {
    public Type type;
    public Object value;

    public void setType(Type type) {
        this.type = type;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override 
    public String toString() {
        return "Type: " + type + ", value: " + value;
    }

    @Override
    public void accept(AstVisitor v) {
        v.visitLiteralExprNode(this);
    }
}