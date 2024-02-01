package com.moshefarkas.javacompiler.ast.nodes;

import java.util.ArrayList;
import java.util.List;

import com.moshefarkas.javacompiler.ast.AstVisitor;

public abstract class AstNode {
    public abstract void accept(AstVisitor<? extends AstNode> v);
    protected List<AstNode> children = new ArrayList<>();
    protected void addChild(AstNode childNode) {
        children.add(childNode);
    }

    public void visitChildren(AstVisitor<AstNode> v) {
        for (AstNode child : children) {
            child.accept(v);
        }
    }

    public void seechildCount() {
        System.out.println(children.size());
    }
}
