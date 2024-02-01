package com.moshefarkas.javacompiler.ast.nodes.statement;

import java.util.ArrayList;
import java.util.List;

public class BlockStmtNode extends StatementNode {
    public List<StatementNode> statements = new ArrayList<>();

    public void addStatement(StatementNode statement) {
        statements.add(statement);
        addChild(statement);
    }

    @Override 
    public String toString() {
        String res = "\n{\n";
        for (StatementNode stmt : statements) {
            res += stmt.toString() + "\n";
        }
        return res + "}";
    }
}
