package com.moshefarkas.javacompiler.semanticanalysis;

import com.moshefarkas.javacompiler.Value.Type;
import com.moshefarkas.javacompiler.ast.nodes.expression.BinaryExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.LiteralExprNode;

public class TypeCheckVisitor extends SemanticAnalysis {

    // called after the symbol table is filled

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
