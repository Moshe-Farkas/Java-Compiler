package com.moshefarkas.javacompiler.ast.astgen;

import com.moshefarkas.generated.Java8Parser.AdditiveExpressionContext;
import com.moshefarkas.generated.Java8Parser.ExpressionNameContext;
import com.moshefarkas.generated.Java8Parser.LiteralContext;
import com.moshefarkas.generated.Java8Parser.MultiplicativeExpressionContext;
import com.moshefarkas.generated.Java8ParserBaseVisitor;
import com.moshefarkas.javacompiler.Value.Type;
import com.moshefarkas.javacompiler.ast.nodes.expression.BinaryExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.ExpressionNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.IdentifierExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.LiteralExprNode;

public class ExpressionVisitor extends Java8ParserBaseVisitor<ExpressionNode> {

    // only return binary expr and literal expresions

    @Override
    public ExpressionNode visitAdditiveExpression(AdditiveExpressionContext ctx) {
        // additiveExpression
        //     : multiplicativeExpression
        //     | additiveExpression '+' multiplicativeExpression
        //     | additiveExpression '-' multiplicativeExpression
        //     ;
        ExpressionNode expr = null;

        if (ctx.additiveExpression() != null) {
            // aka in a binary + expression
            ExpressionNode left = visit(ctx.additiveExpression());
            ExpressionNode right = visit(ctx.multiplicativeExpression());
            BinaryExprNode binExpr = new BinaryExprNode();
            binExpr.setLeft(left);
            binExpr.setRight(right);
            
            if (ctx.ADD() != null) {
                binExpr.setOp("+");
            } else {
                binExpr.setOp("-");
            }

            expr = binExpr;
        } else {
            expr = visit(ctx.multiplicativeExpression());
        }

        return expr;
    }

    @Override
    public ExpressionNode visitMultiplicativeExpression(MultiplicativeExpressionContext ctx) {
        // multiplicativeExpression
        //     : unaryExpression
        //     | multiplicativeExpression '*' unaryExpression
        //     | multiplicativeExpression '/' unaryExpression
        //     | multiplicativeExpression '%' unaryExpression
        //     ;

        ExpressionNode expr = null;

        if (ctx.multiplicativeExpression() != null) {
            // aka in a binary + expression
            ExpressionNode left = visit(ctx.multiplicativeExpression());
            ExpressionNode right = visit(ctx.unaryExpression());
            BinaryExprNode binExpr = new BinaryExprNode();
            binExpr.setLeft(left);
            binExpr.setRight(right);
            
            if (ctx.DIV() != null) {
                binExpr.setOp("/");
            } else if (ctx.MUL() != null) {
                binExpr.setOp("*");
            } else {
                binExpr.setOp("%");
            }

            expr = binExpr;
        } else {
            expr = visit(ctx.unaryExpression());
        }

        return expr;
    }

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

    @Override
    public ExpressionNode visitExpressionName(ExpressionNameContext ctx) {
        // expressionName
        //     : Identifier
        //     | ambiguousName '.' Identifier
        //     ;
        if (ctx.ambiguousName() != null) {
            throw new UnsupportedOperationException("inside visit epxression name");
        }
        
        IdentifierExprNode iden = new IdentifierExprNode();
        iden.setVarName(ctx.Identifier().getText());
    
        return iden;
    }
}
