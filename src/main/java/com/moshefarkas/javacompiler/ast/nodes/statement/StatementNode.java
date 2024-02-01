package com.moshefarkas.javacompiler.ast.nodes.statement;

import com.moshefarkas.javacompiler.ast.AstVisitor;
import com.moshefarkas.javacompiler.ast.nodes.AstNode;

public class StatementNode extends AstNode {
    @Override
    public void accept(AstVisitor v) {
        v.visitStatementNode(this);
    }
} 