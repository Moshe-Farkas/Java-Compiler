package com.moshefarkas.javacompiler;

import com.moshefarkas.javacompiler.ast.BaseAstVisitor;
import com.moshefarkas.javacompiler.ast.nodes.MethodNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.LocalVarDecStmtNode;

public class AstPrintVisitor extends BaseAstVisitor {

    @Override
    public void visitMethodNode(MethodNode node) {
        System.out.println("line: " + node.lineNum);
        System.out.println("method: " + node.methodName);
        System.out.println("[");
        super.visitMethodNode(node);
        System.out.println("]");
    }

    @Override
    public void visitLocalVarDecStmtNode(LocalVarDecStmtNode node) {
        System.out.println("line: " + node.lineNum);
        System.out.println("local var decl: ");
        System.out.println(" " + node.var + ", init: " + node.initializer);
        
        super.visitLocalVarDecStmtNode(node);
    }
}
