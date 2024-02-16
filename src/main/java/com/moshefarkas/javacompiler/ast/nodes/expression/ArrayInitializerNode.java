package com.moshefarkas.javacompiler.ast.nodes.expression;

import java.util.List;

import org.objectweb.asm.Type;

import com.moshefarkas.javacompiler.ast.AstVisitor;

public class ArrayInitializerNode extends ExpressionNode {

    public List<ArrayLiteralElement> vars;
    public List<ExpressionNode> arraySizes;
    public int dims;
    public Type type;

    public static class ArrayLiteralElement { }

    public static class ArrExprLitNode extends ArrayLiteralElement {
        public ExpressionNode expr;
    }

    public static class NestedArrNode extends ArrayLiteralElement {
        public ArrayLiteralElement nestedNode;
    }

    public void setVars(List<ArrayLiteralElement> vars) {
        this.vars = vars;
        throw new UnsupportedOperationException("inside array init class");
    }

    public void setDims(int dims) {
        this.dims = dims;
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
        v.visitArrayInitializer(this);
    }

    @Override
    public String toString() { 
        return "array init. dims: " + dims + " static sizes: " + arraySizes + ", type: " + exprType;
    }
}
