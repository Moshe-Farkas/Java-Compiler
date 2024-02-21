package com.moshefarkas.javacompiler.ast.nodes.expression;

import java.util.List;

import org.objectweb.asm.Type;

import com.moshefarkas.javacompiler.ast.AstVisitor;

public class ArrayInitializerNode extends ExpressionNode {

    public ArrayLiteralNode arrayLiteral;
    public List<ExpressionNode> arraySizes;
    public int dims;
    public Type type;

    public void setArrayLiteral(ArrayLiteralNode arrayLiteral) {
        this.arrayLiteral = arrayLiteral;
        addChild(arrayLiteral);
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setSetArraySizes(List<ExpressionNode> sizes) {
        this.arraySizes = sizes;
        for (ExpressionNode n : sizes)
            addChild(n);
    }

    @Override
    public void accept(AstVisitor v) {
        v.visitArrayInitializerNode(this);
    }

    @Override
    public String toString() { 
        return "array init. "                      + 
                    "dims: " + dims                + 
                    " static sizes: " + arraySizes + 
                    ", type: " + exprType          + 
                    ", literal: " + arrayLiteral;
    }
}
