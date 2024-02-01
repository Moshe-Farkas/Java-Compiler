package com.moshefarkas.javacompiler.ast.astgen;

import com.moshefarkas.generated.Java8Parser.LiteralContext;
import com.moshefarkas.generated.Java8ParserBaseVisitor;
import com.moshefarkas.javacompiler.Value.Type;
import com.moshefarkas.javacompiler.ast.nodes.expression.ExpressionNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.LiteralExprNode;

public class ExpressionVisitor extends Java8ParserBaseVisitor<ExpressionNode> {

    // assignment expression is the starting point for the expression rule tree

    @Override
    public ExpressionNode visitLiteral(LiteralContext ctx) {
        // literal
        //     : IntegerLiteral
        //     | FloatingPointLiteral
        //     | BooleanLiteral
        //     | CharacterLiteral
        //     | StringLiteral
        //     | NullLiteral
        //     ;
        LiteralExprNode lit = new LiteralExprNode();
        Type type = null;
        if (ctx.IntegerLiteral() != null) {
            type = Type.INT;
        } else if (ctx.FloatingPointLiteral() != null) {
            type = Type.FLOAT;
        } else if (ctx.BooleanLiteral() != null) {
            type = Type.BOOL;
        } else if (ctx.CharacterLiteral() != null) {
            type = Type.CHAR;
        } else if (ctx.StringLiteral() != null) {
            type = Type.STRING;
        } else if (ctx.NullLiteral() != null) {
            type = Type.NULL;
        }

        lit.type = type;
        lit.value = ctx.getText();

        return lit;
    }
}
