package com.moshefarkas.javacompiler.ast.nodes.statement;

import com.moshefarkas.javacompiler.VarInfo;
import com.moshefarkas.javacompiler.ast.AstVisitor;
import com.moshefarkas.javacompiler.ast.nodes.expression.ExpressionNode;

public class LocalVarDecStmtNode extends StatementNode {
    
    public ExpressionNode initializer; 
    public VarInfo var;

    public void setInitializer(ExpressionNode initializer) {
        this.initializer = initializer;
        addChild(initializer);
    }

    public void setVar(VarInfo var) {
        this.var = var;
    }

    @Override
    public void accept(AstVisitor v) {
        v.visitLocalVarDecStmtNode(this);
    }
}