package com.moshefarkas.javacompiler.ast.nodes.expression;

import com.moshefarkas.javacompiler.ast.AstVisitor;

public class AssignExprNode extends ExpressionNode {
    @Override
    public void accept(AstVisitor v) {
        v.visitAssignExprNode(this);
    }
}
