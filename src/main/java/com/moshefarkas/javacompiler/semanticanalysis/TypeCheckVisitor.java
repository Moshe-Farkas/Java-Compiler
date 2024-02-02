package com.moshefarkas.javacompiler.semanticanalysis;

import java.util.Stack;

import com.moshefarkas.javacompiler.Value.Type;
import com.moshefarkas.javacompiler.ast.nodes.expression.BinaryExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.LiteralExprNode;

public class TypeCheckVisitor extends SemanticAnalysis {

    // called after the symbol table is filled and checked for duplicate/uninit vars
    
    // float + int -> float
    // float + byte -> float

    // need to check if valid assignment type


    protected Stack<Type> typeStack = new Stack<>();

    private void checkTypes(int lineNum) {
        Type b = typeStack.pop();
        Type a = typeStack.pop();
        if (a != b) {
            error(ErrorType.MISMATCHED_TYPE, lineNum, String.format("cannot operate type `%s` with type `%s`.", a, b));
        } 
    }

    @Override
    public void visitBinaryExprNode(BinaryExprNode node) {
        super.visitBinaryExprNode(node);
        // visited its children
        checkTypes(node.lineNum);
    }

    @Override
    public void visitLiteralExprNode(LiteralExprNode node) {
        typeStack.push(node.type);
    }
}
