package com.moshefarkas.javacompiler.ast.nodes.expression;

import java.util.ArrayList;
import java.util.List;

import com.moshefarkas.javacompiler.ast.AstVisitor;

public class ArrayLiteralNode extends ExpressionNode {
    public List<ExpressionNode> elements = new ArrayList<>();

    public void setElements(List<ExpressionNode> elements) {
        this.elements = elements;
        for (ExpressionNode child : elements)
            addChild(child);
    }

    @Override 
    public String toString() {
        return elements.toString();
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.visitArrayLiteralNode(this);
    }
}
