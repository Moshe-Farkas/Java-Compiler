package com.moshefarkas.javacompiler.ast.nodes.statement;

import java.util.ArrayList;
import java.util.List;

import com.moshefarkas.javacompiler.ast.AstVisitor;

public class BlockStmtNode extends StatementNode {
    public List<StatementNode> statements = new ArrayList<>();

    public void addStatement(StatementNode statement) {
        statements.add(statement);
        addChild(statement);
    }

    public void addParams(List<LocalVarDecStmtNode> params) {
        statements.addAll(0, params);
        this.children.addAll(0, params);
    }

    @Override 
    public String toString() {
        String res = "\n{\n";
        for (StatementNode stmt : statements) {
            res += stmt.toString() + "\n";
        }
        return res + "}";
    }

    @Override
    public void accept(AstVisitor v) {
        v.visitBlockStmtNode(this);
    }
}
