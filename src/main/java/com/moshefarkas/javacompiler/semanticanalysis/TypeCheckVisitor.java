package com.moshefarkas.javacompiler.semanticanalysis;

import com.moshefarkas.javacompiler.Value.Type;
import com.moshefarkas.javacompiler.ast.nodes.expression.BinaryExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.LiteralExprNode;

public class TypeCheckVisitor extends SemanticAnalysis {

    // called after the symbol table is filled and checked for duplicate/uninit vars
    
    // float + int -> float
    // float + byte -> float

    @Override
    public void visitBinaryExprNode(BinaryExprNode node) {
        super.visitBinaryExprNode(node);
        // visited its children
        Type b = typeStack.pop();
        Type a = typeStack.pop();
        checkTypes(a, b, node.lineNum);
    }

    @Override
    public void visitLiteralExprNode(LiteralExprNode node) {
        typeStack.push(node.type);
    }
}
