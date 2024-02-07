package com.moshefarkas.javacompiler.ast.nodes.expression;

import org.objectweb.asm.Type;

import com.moshefarkas.javacompiler.ast.AstVisitor;

public class CastExprNode extends ExpressionNode {
    
    public Type targetCast;
    public ExpressionNode expression;

    public void setTargetCast(Type targetCast) {
        this.targetCast = targetCast;
    }

    public void setExpression(ExpressionNode expression) {
        this.expression = expression;
        addChild(expression);
    }

    @Override 
    public void accept(AstVisitor visitor) {
        visitor.visitCastExprNode(this);
    }
}
