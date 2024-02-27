package com.moshefarkas.javacompiler.symboltable;

import org.objectweb.asm.Opcodes;

import com.moshefarkas.javacompiler.ast.nodes.ClassNode;
import com.moshefarkas.javacompiler.ast.nodes.FieldNode;
import com.moshefarkas.javacompiler.ast.nodes.IVarDecl;

public class Clazz {
    public MethodManager methodManager;
    public Scope<String, FieldNode> fields;
    public String className;
    public ClassNode classNode;

    public Clazz(ClassNode classNode) {
        this.classNode = classNode;
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

    public boolean hasField(String varName) {
        return fields.hasElement(varName);
    }

    public boolean hasLocalVar(String methodName, String varName) {
        return methodManager.getMethod(methodName).symbolTable.hasVar(varName);
    }

    @Override
    public String toString() {
        // methodManager.debug_print_methods();
        return "methods: " + methodManager + "\n fields: " + fields;
    }
}
