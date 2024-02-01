package com.moshefarkas.javacompiler.ast.nodes.expression;

import com.moshefarkas.javacompiler.ast.AstVisitor;

public class IdentifierExprNode extends ExpressionNode {
    public String varName;

    @Override 
    public void accept(AstVisitor v) {
        v.visitIdentifierExprNode(this);
    }

    @Override 
    public String toString() {
        return varName;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }
}
