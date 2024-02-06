package com.moshefarkas.javacompiler.ast.nodes.expression;

import com.moshefarkas.javacompiler.ast.AstVisitor;

public class AssignExprNode extends ExpressionNode {

    public String varName;
    public ExpressionNode assignmentValue;

    public void setVar(String varName) {
        this.varName = varName;
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
