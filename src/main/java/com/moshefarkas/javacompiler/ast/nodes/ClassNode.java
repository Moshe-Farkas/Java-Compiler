package com.moshefarkas.javacompiler.ast.nodes;

import java.util.List;

import com.moshefarkas.javacompiler.ast.AstVisitor;

public class ClassNode extends AstNode {
    public String className;
    public List<Integer> accessModifiers;
    public List<MethodNode> methods;
    public List<FieldNode> fields;
    public List<ConstructorNode> constuctors;

    public void setClassName(String className) {
        this.className = className;
    }

    public void setAccessModifiers(List<Integer> accessModifiers) {
        this.accessModifiers = accessModifiers;
    }

    public void setMethods(List<MethodNode> methods) {
        this.methods = methods;
        for (AstNode child : methods)
            addChild(child);
    }

    public void setFields(List<FieldNode> fields) {
        this.fields = fields;
        for (AstNode child : fields)
            addChild(child);
    }

    public void setConstrcutors(List<ConstructorNode> constructors) {
        this.constuctors = constructors;
        for (AstNode child : constructors)
            addChild(child);
    }

    @Override 
    public String toString() {
        return className + methods;
    }

    @Override
    public void accept(AstVisitor v) {
        v.visitClassNode(this);
    }
}
