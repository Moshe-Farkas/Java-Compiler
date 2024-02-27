package com.moshefarkas.javacompiler.ast.nodes;

import java.util.List;

import org.objectweb.asm.Type;

import com.moshefarkas.javacompiler.ast.AstVisitor;
import com.moshefarkas.javacompiler.ast.nodes.expression.ExpressionNode;

public class FieldNode extends AstNode implements IVarDecl {

    public List<Integer> fieldModifiers;
    public String fieldName;
    public Type fieldType;
    public boolean hasValue = false;
    public ExpressionNode initializer;

    public void setFieldModifiers(List<Integer> fieldModifiers) {
        this.fieldModifiers = fieldModifiers;
    }

    @Override
    public void accept(AstVisitor v) {
        v.visitFieldNode(this);
    }

    @Override
    public String getName() {
        return fieldName;
    }

    @Override
    public Type getType() {
        return fieldType;
    }

    @Override
    public boolean hasValue() {
        return hasValue;
    }

    @Override
    public void setName(String name) {
        this.fieldName = name;
    }

    @Override
    public void setType(Type type) {
        this.fieldType = type;
    }

    @Override
    public void setHasValue(boolean hasValue) {
        this.hasValue = hasValue;
    }

    @Override
    public ExpressionNode getInitializerNode() {
        return initializer;
    }

    @Override
    public void setInitializerNode(ExpressionNode initializer) {
        this.initializer = initializer;
    }
}
