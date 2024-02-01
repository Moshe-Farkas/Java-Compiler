package com.moshefarkas.javacompiler.ast.nodes.expression;

import com.moshefarkas.javacompiler.ast.AstVisitor;
import com.moshefarkas.javacompiler.ast.nodes.AstNode;

public class ExpressionNode extends AstNode {

    @Override
    public void accept(AstVisitor v) {
        v.visitExpressionNode(this);
    }
}
