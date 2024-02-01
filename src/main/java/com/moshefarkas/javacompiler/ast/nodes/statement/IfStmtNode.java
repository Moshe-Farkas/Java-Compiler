package com.moshefarkas.javacompiler.ast.nodes.statement;

import com.moshefarkas.javacompiler.ast.AstVisitor;
import com.moshefarkas.javacompiler.ast.nodes.expression.ExpressionNode;

public class IfStmtNode extends StatementNode {
    public ExpressionNode condition;

    public void setCondition(ExpressionNode condition) {
        this.condition = condition;
        addChild(condition); 
    }

    @Override
    public void accept(AstVisitor v) {
        v.visitIfStmtNode(this);
    }
}
