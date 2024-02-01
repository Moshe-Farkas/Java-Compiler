package com.moshefarkas.javacompiler.ast.nodes;

import java.util.List;

import com.moshefarkas.javacompiler.ast.AstVisitor;

public class ClassNode extends AstNode {
    public String className;
    public int accessModifier;
    public List<MethodNode> methods;

    public ClassNode(String className, int accessModifier, List<MethodNode> methods) {
        this.className = className;
        this.accessModifier = accessModifier;
        this.methods = methods;

        for (AstNode child : methods) {
            addChild(child);
        }
    }

    @Override 
    public String toString() {
        return className + methods;
    }

    @Override
    public void accept(AstVisitor<? extends AstNode> v) {
        v.visitClassNode(this);
    }
}
