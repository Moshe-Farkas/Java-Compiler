package com.moshefarkas.javacompiler.ast.nodes;

import java.util.ArrayList;
import java.util.List;

import com.moshefarkas.javacompiler.ast.AstVisitor;

public abstract class AstNode {
    public abstract void accept(AstVisitor v);
    protected List<AstNode> children = new ArrayList<>();
    protected void addChild(AstNode childNode) {
        if (childNode != null) {
            children.add(childNode);
        }
    }

    public void visitChildren(AstVisitor v) {
        for (AstNode child : children) {
            child.accept(v);
        }
    }
}
