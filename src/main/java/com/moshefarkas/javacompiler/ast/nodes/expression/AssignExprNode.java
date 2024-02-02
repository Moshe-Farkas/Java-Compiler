package com.moshefarkas.javacompiler.ast.nodes.expression;

import com.moshefarkas.javacompiler.ast.AstVisitor;

public class AssignExprNode extends ExpressionNode {

    public IdentifierExprNode var;
    public ExpressionNode assignmentValue;

    public void setVar(IdentifierExprNode var) {
        this.var = var;
        addChild(var);
    }

    public void setAssignmentValue(ExpressionNode assignmentValue) {
        this.assignmentValue = assignmentValue;
        addChild(assignmentValue);
    }

    @Override
    public void accept(AstVisitor v) {
        v.visitAssignExprNode(this);
    }
}
