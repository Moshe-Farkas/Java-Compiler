package com.moshefarkas.javacompiler.ast.nodes.statement;

import com.moshefarkas.javacompiler.ast.AstVisitor;
import com.moshefarkas.javacompiler.ast.nodes.expression.ExpressionNode;

public class IfStmtNode extends StatementNode {
    public ExpressionNode condition;
    public StatementNode statement;

    public void setCondition(ExpressionNode condition) {
        this.condition = condition;
        addChild(condition); 
    }

    public void setStatement(StatementNode statement) {
        this.statement = statement;
        addChild(statement);
    }

    @Override
    public void accept(AstVisitor v) {
        v.visitIfStmtNode(this);
    }
}
