package com.moshefarkas.javacompiler;

import java.util.Stack;

import com.moshefarkas.javacompiler.Value.Type;
import com.moshefarkas.javacompiler.ast.BaseAstVisitor;
import com.moshefarkas.javacompiler.ast.nodes.expression.BinaryExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.LiteralExprNode;

public class SemanticAnalysisVisitor extends BaseAstVisitor {

    private Stack<Type> typeStack = new Stack<>();
    
    @Override
    public void visitBinaryExprNode(BinaryExprNode node) {
        super.visitBinaryExprNode(node);
        // visited its children
        Type b = typeStack.pop();
        Type a = typeStack.pop();
        if (a != b) {
            System.out.println("semantic error");
        }
    }

    @Override
    public void visitLiteralExprNode(LiteralExprNode node) {
        typeStack.push(node.type);
    }
}
