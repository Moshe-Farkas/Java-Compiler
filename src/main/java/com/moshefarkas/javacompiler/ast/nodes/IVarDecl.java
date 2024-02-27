package com.moshefarkas.javacompiler.ast.nodes;

import org.objectweb.asm.Type;

import com.moshefarkas.javacompiler.ast.nodes.expression.ExpressionNode;

public interface IVarDecl {
    public String getName();
    public Type getType();
    public boolean hasValue();
    public ExpressionNode getInitializerNode();

    public void setName(String name);
    public void setType(Type type);
    public void setHasValue(boolean hasValue);
    public void setInitializerNode(ExpressionNode initializer);
} 