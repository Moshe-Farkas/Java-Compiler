package com.moshefarkas.javacompiler.ast.nodes.statement;

import com.moshefarkas.javacompiler.ast.AstVisitor;
import com.moshefarkas.javacompiler.ast.nodes.expression.ExpressionNode;

public class IfStmtNode extends StatementNode {
    public ExpressionNode condition;
    public StatementNode ifStatement;

    public StatementNode elseStatement;

    public void setCondition(ExpressionNode condition) {
        this.condition = condition;
        addChild(condition); 
    }

    public void setIfStatement(StatementNode statement) {
        this.ifStatement = statement;
        addChild(statement);
    }

    public void setElseStatement(StatementNode statement) {
        this.elseStatement = statement;
        addChild(statement);
    }

    @Override
    public void accept(AstVisitor v) {
        v.visitIfStmtNode(this);
    }
}
