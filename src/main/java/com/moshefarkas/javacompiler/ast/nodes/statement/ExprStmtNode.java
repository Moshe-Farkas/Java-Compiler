package com.moshefarkas.javacompiler.ast.nodes.statement;

import com.moshefarkas.javacompiler.ast.AstVisitor;
import com.moshefarkas.javacompiler.ast.nodes.expression.ExpressionNode;

public class ExprStmtNode extends StatementNode {

    public ExpressionNode expression;
    
    public void setExpression(ExpressionNode expression) {
        this.expression = expression;
        addChild(expression);
    }

    @Override 
    public String toString() {
        return expression.toString();
    }

    @Override
    public void accept(AstVisitor v) {
        super.accept(v);
    }
}
