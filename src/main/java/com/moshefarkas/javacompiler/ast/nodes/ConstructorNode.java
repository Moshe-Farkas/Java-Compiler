package com.moshefarkas.javacompiler.ast.nodes;

import com.moshefarkas.javacompiler.ast.AstVisitor;

public class ConstructorNode extends MethodNode {

    @Override
    public void accept(AstVisitor v) {
        v.visitConstructorNode(this);
    }

    @Override 
    public String toString() {
        return "(constructor) " + super.toString();
    }
}
