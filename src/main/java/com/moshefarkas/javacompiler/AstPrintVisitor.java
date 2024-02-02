package com.moshefarkas.javacompiler;

import com.moshefarkas.javacompiler.ast.BaseAstVisitor;
import com.moshefarkas.javacompiler.ast.nodes.expression.AssignExprNode;

public class AstPrintVisitor extends BaseAstVisitor {

    @Override
    public void visitAssignExprNode(AssignExprNode node) {
        System.out.println("line: " + node.lineNum);
        System.out.println("assignment: ");
        System.out.println("\t name: " + node.var);
        System.out.println("\t expression: " + node.assignmentValue);
    }

    // @Override
    // public void visitMethodNode(MethodNode node) {
    //     System.out.println("line: " + node.lineNum);
    //     System.out.println("method: " + node.methodName);
    //     System.out.println("[");
    //     super.visitMethodNode(node);
    //     System.out.println("]");
    // }

    // @Override
    // public void visitLocalVarDecStmtNode(LocalVarDecStmtNode node) {
    //     System.out.println("line: " + node.lineNum);
    //     System.out.println("local var decl: ");
    //     System.out.println(" " + node.var + ", init: " + node.initializer);
        
    //     super.visitLocalVarDecStmtNode(node);
    // }
    
    
}
