package com.moshefarkas.javacompiler.ast.nodes;

import java.util.List;

import com.moshefarkas.javacompiler.VarInfo;
import com.moshefarkas.javacompiler.ast.AstVisitor;
import com.moshefarkas.javacompiler.ast.nodes.expression.ExpressionNode;

public class FieldNode extends AstNode {

    public List<Integer> fieldModifiers;
    public VarInfo fieldInfo;
    public ExpressionNode initializer;

    public void setFieldModifiers(List<Integer> fieldModifiers) {
        this.fieldModifiers = fieldModifiers;
    }

    public void setFieldInfo(VarInfo fieldInfo) {
        this.fieldInfo = fieldInfo;
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
