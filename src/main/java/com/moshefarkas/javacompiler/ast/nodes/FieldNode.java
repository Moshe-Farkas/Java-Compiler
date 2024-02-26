package com.moshefarkas.javacompiler.ast.nodes;

import java.util.List;

import org.objectweb.asm.Type;

import com.moshefarkas.javacompiler.ast.AstVisitor;
import com.moshefarkas.javacompiler.ast.nodes.expression.ExpressionNode;

public class FieldNode extends AstNode {

    public List<Integer> fieldModifiers;
    public String fieldName;
    public Type fieldType;
    public boolean hasValue = false;
    public ExpressionNode initializer;

    public void setFieldModifiers(List<Integer> fieldModifiers) {
        this.fieldModifiers = fieldModifiers;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setFieldType(Type fieldType) {
        this.fieldType = fieldType;
    }

    public void setInitializer(ExpressionNode initializer) {
        this.initializer = initializer;
        addChild(initializer);
    }

    public boolean hasInitializer() {
        return initializer != null;
    }

    @Override
    public void accept(AstVisitor v) {
        v.visitFieldNode(this);
    }
}
