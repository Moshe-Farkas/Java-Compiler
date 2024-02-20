package com.moshefarkas.javacompiler.ast.nodes.statement;

import com.moshefarkas.javacompiler.ast.AstVisitor;

public class ControlFlowStmt extends StatementNode {
    // can be either continue or break
    public boolean isContinue = false;
    public boolean isBreak = false;

    public void setContinue(boolean isContinue) {
        this.isContinue = isContinue;
    }

    public void setBreak(boolean isBreak) {
        this.isBreak = isBreak;
    }

    @Override
    public void accept(AstVisitor v) {
        v.visitControlFlowStmt(this);
    }
}
