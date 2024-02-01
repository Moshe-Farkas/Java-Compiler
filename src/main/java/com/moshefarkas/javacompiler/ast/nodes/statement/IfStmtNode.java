package com.moshefarkas.javacompiler.ast.nodes.statement;

import com.moshefarkas.javacompiler.ast.nodes.expression.ExpressionNode;

public class IfStmtNode extends StatementNode {
    public ExpressionNode condition;

    public void setCondition(ExpressionNode condition) {
        this.condition = condition;
        addChild(condition); 
    }
}
