package com.moshefarkas.javacompiler;

import java.util.Stack;

import com.moshefarkas.javacompiler.Value.Type;
import com.moshefarkas.javacompiler.ast.BaseAstVisitor;
import com.moshefarkas.javacompiler.ast.nodes.expression.BinaryExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.LiteralExprNode;

public class SemanticAnalysisVisitor extends BaseAstVisitor {

    public enum ErrorType {
        MISMATCHED_TYPE,
        MISMATCHED_ASSIGNMENT_TYPE,
        UNDEFINED_VAR,
        DUPLICATE_VAR,
        UNINITIALIZED_VAR,
        INVALID_LVALUE,
    } 

    public ErrorType test_error;

    private Stack<Type> typeStack = new Stack<>();

    private void error(ErrorType errType, String errMsg) {
        System.err.println("\u001B[31m" + errMsg + "\u001B[0m");
        test_error = errType;
    }

    private void checkTypes(Type a, Type b) {
        if (a != b) {
            error(ErrorType.MISMATCHED_TYPE, String.format("cannot operate type `%s` with type `%s`.", a, b));
        } 
    }

    @Override
    public void visitBinaryExprNode(BinaryExprNode node) {
        super.visitBinaryExprNode(node);
        // visited its children
        Type b = typeStack.pop();
        Type a = typeStack.pop();
        checkTypes(a, b);
    }

    @Override
    public void visitLiteralExprNode(LiteralExprNode node) {
        typeStack.push(node.type);
    }
}
