package com.moshefarkas.javacompiler.symboltable;

import org.objectweb.asm.Opcodes;

import com.moshefarkas.javacompiler.ast.nodes.ClassNode;
import com.moshefarkas.javacompiler.ast.nodes.FieldNode;

public class Clazz {
    public MethodManager methodManager;
    public Scope<String, FieldNode> fields;
    public ClassNode classNode;
    public String className;

    public Clazz(ClassNode node) {
        this.classNode = node;
        className = classNode.className;
        methodManager = new MethodManager();
        fields = new Scope<>(null);
    }

    public FieldNode getPublicField(String field) {
        if (!hasPublicField(field))
            return null;
        return fields.getElement(field);
    }
    
    public boolean hasPublicField(String field) {
        if (!fields.hasElement(field))
            return false;
        FieldNode fieldNode = fields.getElement(field);
        for (int modifer : fieldNode.fieldModifiers) {
            if (modifer == Opcodes.ACC_PUBLIC)
                return true;
        }
        return false; 
    }

    public boolean hasPublicMethod(String methodName) {
        if (!methodManager.hasMethod(methodName)) {
            return false; 
        }
        Method method = methodManager.getMethod(methodName);
        for (int modifer : method.methodNode.methodModifiers) {
            if (modifer == Opcodes.ACC_PUBLIC)
                return true;
        }
        return false;
    }

    public Method getPublicMethod(String methodName) {
        if (!hasPublicMethod(methodName)) {
            return null;
        }
        return methodManager.getMethod(methodName);
    }

    @Override
    public String toString() {
        // methodManager.debug_print_methods();
        return "methods: " + methodManager + "\n fields: " + fields;
    }
}
