package com.moshefarkas.javacompiler;

import com.moshefarkas.javacompiler.ast.BaseAstVisitor;
import com.moshefarkas.javacompiler.ast.nodes.ClassNode;
import com.moshefarkas.javacompiler.ast.nodes.MethodNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.BinaryExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.CallExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.UnaryExprNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.IfStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.LocalVarDecStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.WhileStmtNode;

public class AstPrintVisitor extends BaseAstVisitor {

    @Override
    public void visitCallExprNode(CallExprNode node) {
        System.out.println("line: " + node.lineNum);
        System.out.println("call expr: ");    
        System.out.println("\tname: " + node.methodName);
        System.out.println("\targs: " + node.arguments);
    }

    // @Override
    // public void visitBinaryExprNode(BinaryExprNode node) {
    //     System.out.println("line: " + node.lineNum);
    //     System.out.println("bin expr: " + node.exprType);
    //     System.out.println("\t" + node.left);
    //     System.out.println("\t" + node.right);
    // }

    // @Override
    // public void visitIfStmtNode(IfStmtNode node) {
    //     System.out.println("line: " + node.lineNum);
    //     System.out.println("if: ");
    //     System.out.println("\t condition: " + node.condition);
    //     System.out.println("\tstatement: " + node.statement);
    // }

    // @Override
    // public void visitWhileStmtNode(WhileStmtNode node) {
    //     System.out.println("line: " + node.lineNum);
    //     System.out.println("while: ");
    //     System.out.println("\tcondition: " + node.condition);
    //     System.out.println("\tstatement: " + node.statement);
    //     super.visitWhileStmtNode(node);
    // }

    // @Override
    // public void visitAssignExprNode(AssignExprNode node) {
    //     System.out.println("line: " + node.lineNum);
    //     System.out.println("assignment: ");
    //     System.out.println("\t name: " + node.var);
    //     System.out.println("\t expression: " + node.assignmentValue);
    // }

    // @Override
    // public void visitMethodNode(MethodNode node) {
    //     System.out.println("line: " + node.lineNum);
    //     System.out.println("method: " + node.methodName);
    //     System.out.println("params: " + node.params);
    //     // System.out.println("[");
    //     // super.visitMethodNode(node);
    //     // System.out.println("]");
    // }

//     @Override
//     public void visitLocalVarDecStmtNode(LocalVarDecStmtNode node) {
//         System.out.println("line: " + node.lineNum);
//         System.out.println("local var decl: ");
//         System.out.println(" " + node.var.name + ", init: " + node.initializer);
        
//         super.visitLocalVarDecStmtNode(node);
//     }

//     @Override
//     public void visitUnaryExprNode(UnaryExprNode node) {
//         System.out.println("line: " + node.lineNum);
//         System.out.println("unary: "); 
//         System.out.println("\top: " + node.op + ", value: " + node.expr);
//     }
}
