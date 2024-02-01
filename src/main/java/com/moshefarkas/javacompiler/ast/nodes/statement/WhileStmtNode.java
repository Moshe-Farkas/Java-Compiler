package com.moshefarkas.javacompiler.ast.nodes.statement;

import com.moshefarkas.javacompiler.ast.nodes.expression.ExpressionNode;

public class WhileStmtNode extends StatementNode {
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
    public String toString() {
        String res = "";
        res += "whileStmt: ";
        res += " condition: " + condition;
        return res;
    }
}
