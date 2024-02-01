package com.moshefarkas.javacompiler;

import com.moshefarkas.javacompiler.ast.BaseAstVisitor;
import com.moshefarkas.javacompiler.ast.nodes.expression.LiteralExprNode;

public class SemanticAnalysisVisitor extends BaseAstVisitor {

    @Override
    public void visitLiteralExprNode(LiteralExprNode node) {
        System.out.println(node);
        super.visitLiteralExprNode(node);
    }

}
