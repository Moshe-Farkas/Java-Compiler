package com.moshefarkas.javacompiler.ast.nodes.expression;

import org.objectweb.asm.Type;

import com.moshefarkas.javacompiler.ast.AstVisitor;
import com.moshefarkas.javacompiler.ast.nodes.AstNode;

public class ExpressionNode extends AstNode {

    public Type exprType;
    public void setExprType(Type type) {
        this.exprType = type;
    }

    @Override
    public void accept(AstVisitor v) {
        v.visitExpressionNode(this);
    }
}
