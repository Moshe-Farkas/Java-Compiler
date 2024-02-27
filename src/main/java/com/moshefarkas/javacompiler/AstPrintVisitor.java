package com.moshefarkas.javacompiler;

import com.moshefarkas.javacompiler.ast.BaseAstVisitor;
import com.moshefarkas.javacompiler.ast.nodes.ClassNode;
import com.moshefarkas.javacompiler.ast.nodes.ConstructorNode;
import com.moshefarkas.javacompiler.ast.nodes.FieldNode;
import com.moshefarkas.javacompiler.ast.nodes.MethodNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.LocalVarDecStmtNode;

public class AstPrintVisitor extends BaseAstVisitor {

    @Override
    public void visitClassNode(ClassNode node) {
        System.out.println("line: " + node.lineNum);
        System.out.println("fields:");
        for (FieldNode f : node.fields) {
            visit(f);
        }
        System.out.println();
        System.out.println("constructors:");
        for (ConstructorNode c : node.constuctors) {
            visit(c);
        }
        System.out.println("methods:");
        for (MethodNode m : node.methods) {
            visit(m);
        }

    }

    @Override
    public void visitFieldNode(FieldNode node) {
        System.out.println("line: " + node.lineNum);
        System.out.println("\t" + node);
        System.out.println("\t" + node.fieldModifiers);
        System.out.println("\t" + node.initializer);
    }

    @Override
    public void visitMethodNode(MethodNode node) {
        System.out.println("line: " + node.lineNum);
        System.out.println("\tmethod modifiers: " + node.methodModifiers);
        System.out.println("\tmethod: " + node.methodName);
        System.out.println("\tparams: " + node.params);
        System.out.println("\t" + node.statements);
        super.visitMethodNode(node);
    }

    @Override
    public void visitConstructorNode(ConstructorNode node) {
        System.out.println("line: " + node.lineNum);
        System.out.println("\tconstructor modifiers: " + node.methodModifiers);
        System.out.println("\tconstructor: " + node.methodName);
        System.out.println("\tparams: " + node.params);
    }

    // @Override
    // public void visitArrayLiteralNode(ArrayLiteralNode node) {
    //     System.out.println("line: " + node.lineNum);
    //     System.out.println(node);    
    // }

    // @Override
    // public void visitControlFlowStmt(ControlFlowStmt node) {
    //     System.out.println("line: " + node.lineNum);
    //     if (node.isBreak) {
    //         System.out.println("break stmt.");
    //     } else {
    //         System.out.println("continue stmt.");
    //     }
    // }

    // @Override
    // public void visitReturnStmt(ReturnStmt node) {
    //     System.out.println("line: " + node.lineNum);
    //     System.out.println(node);
    // }

    // @Override
    // public void visitLiteralExprNode(LiteralExprNode node) {
    //     System.out.println("line: " + node.lineNum);
    //     System.out.println(node.exprType);
    //     System.out.println("value: " + node.value);
    // }

    // @Override
    // public void visitArrAccessExprNode(ArrAccessExprNode node) {
    //     System.out.println("line: " + node.lineNum);
    //     System.out.println(node);
    // }

    // @Override
    // public void visitArrayInitializer(ArrayInitializer node) {
    //     System.out.println("line: " + node.lineNum);
    //     System.out.println(node);
    // }

    // @Override
    // public void visitCastExprNode(CastExprNode node) {
    //     System.out.println("line: " + node.lineNum);
    //     System.out.println("\tcast expression: ");
    //     System.out.println("\tTarget cast: " + node.targetCast);
    //     System.out.println("\tExpression: " + node.expression);
    // }

    // @Override
    // public void visitCallExprNode(CallExprNode node) {
    //     System.out.println("line: " + node.lineNum);
    //     System.out.println("call expr: ");    
    //     System.out.println("\tname: " + node.methodName);
    //     System.out.println("\targs: " + node.arguments);
    // }

    // @Override
    // public void visitBinaryExprNode(BinaryExprNode node) {
    //     System.out.println("line: " + node.lineNum);
    //     System.out.println("bin op: " + node.op);
    //     System.out.println("\texpr type: " + node.exprType);
    //     System.out.println("\t" + node.left);
    //     System.out.println("\t" + node.right);
    // }

    // @Override
    // public void visitIfStmtNode(IfStmtNode node) {
    //     System.out.println("line: " + node.lineNum);
    //     System.out.println("if: ");
    //     System.out.println("condition: " + node.condition);
    //     System.out.println("if statement: " + node.ifStatement);
    //     System.out.println("else statement: " + node.elseStatement);
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
    //     System.out.println("\tassignment: " + node.assignmentValue);
    //     System.out.println("\tnode r-value: " + node.identifier);
    // }

    // @Override
    // public void visitLocalVarDecStmtNode(LocalVarDecStmtNode node) {
    //     System.out.println("line: " + node.lineNum);
    //     System.out.println(node);
    //     // System.out.println("local var decl: ");
    //     // System.out.println(node.var);
    //     // System.out.println(" " + node.var.name + ", init: " + node.initializer);
    // }

//     @Override
//     public void visitUnaryExprNode(UnaryExprNode node) {
//         System.out.println("line: " + node.lineNum);
//         System.out.println("unary: "); 
//         System.out.println("\top: " + node.op + ", value: " + node.expr);
//     }
}
