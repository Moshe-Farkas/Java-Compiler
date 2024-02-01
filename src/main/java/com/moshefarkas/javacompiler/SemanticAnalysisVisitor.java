package com.moshefarkas.javacompiler;

import com.moshefarkas.javacompiler.ast.BaseAstVisitor;
import com.moshefarkas.javacompiler.ast.nodes.expression.ExpressionNode;

public class SemanticAnalysisVisitor extends BaseAstVisitor {

    @Override
    public void visitExpressionNode(ExpressionNode node) {
        System.out.println("should be here twice: " + node);
        // super.visitExpressionNode(node);
    }
}
