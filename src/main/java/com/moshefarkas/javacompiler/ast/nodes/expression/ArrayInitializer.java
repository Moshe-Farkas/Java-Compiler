package com.moshefarkas.javacompiler.ast.nodes.expression;

import java.util.List;

import org.objectweb.asm.Type;

import com.moshefarkas.javacompiler.ast.AstVisitor;
import com.moshefarkas.javacompiler.ast.nodes.statement.LocalVarDecStmtNode;

public class ArrayInitializer extends ExpressionNode {

    public List<LocalVarDecStmtNode> vars;
    public ExpressionNode arraySize;
    public int dims;
    public Type type;

    public void setVars(List<LocalVarDecStmtNode> vars) {
        this.vars = vars;
        throw new UnsupportedOperationException("inside array init class");
    }

    public void setDims(int dims) {
        this.dims = dims;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setSetArraySize(ExpressionNode size) {
        this.arraySize = size;
        addChild(size);
    }

    @Override
    public void accept(AstVisitor v) {
        v.visitArrayInitializer(this);
    }

    @Override
    public String toString() { 
        return "array init. size: " + arraySize + ", type: " + type;
    }
}
