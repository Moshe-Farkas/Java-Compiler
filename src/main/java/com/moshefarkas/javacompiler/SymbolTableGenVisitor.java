package com.moshefarkas.javacompiler;

import com.moshefarkas.javacompiler.ast.BaseAstVisitor;
import com.moshefarkas.javacompiler.ast.nodes.AstNode;
import com.moshefarkas.javacompiler.ast.nodes.ClassNode;
import com.moshefarkas.javacompiler.ast.nodes.MethodNode;

public class SymbolTableGenVisitor extends BaseAstVisitor {

    @Override
    public AstNode visitClassNode(ClassNode node) {
        System.out.println("class name: ");
        System.out.println(" " + node.className);
        return super.visitClassNode(node);
    }

    @Override
    public AstNode visitMethodNode(MethodNode node) {
        System.out.println("  method name: " + node.methodName);
        System.out.println("   ret type: " + node.returnType);
        System.out.println("   access mods: " + node.accessModifiers);
        System.out.println("    statements: " + node.statements);
        return super.visitMethodNode(node);
    }
}
