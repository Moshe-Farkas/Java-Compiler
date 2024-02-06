package com.moshefarkas.javacompiler.ast.astgen;

import org.objectweb.asm.Type;

import com.moshefarkas.generated.Java8Parser.AdditiveExpressionContext;
import com.moshefarkas.generated.Java8Parser.AssignmentContext;
import com.moshefarkas.generated.Java8Parser.ExpressionNameContext;
import com.moshefarkas.generated.Java8Parser.LiteralContext;
import com.moshefarkas.generated.Java8Parser.MultiplicativeExpressionContext;
import com.moshefarkas.generated.Java8Parser.RelationalExpressionContext;
import com.moshefarkas.generated.Java8Parser.UnaryExpressionContext;
import com.moshefarkas.generated.Java8Parser.UnaryExpressionNotPlusMinusContext;
import com.moshefarkas.generated.Java8ParserBaseVisitor;
import com.moshefarkas.javacompiler.ast.nodes.expression.AssignExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.BinaryExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.BinaryExprNode.BinOp;
import com.moshefarkas.javacompiler.ast.nodes.expression.ExpressionNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.IdentifierExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.LiteralExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.UnaryExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.UnaryExprNode.UnaryOp;

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
                binExpr.setOp(BinOp.PLUS);
            } else {
                binExpr.setOp(BinOp.MINUS);
            }

            expr = binExpr;
        } else {
            expr = visit(ctx.multiplicativeExpression());
        }

        expr.lineNum = ctx.getStart().getLine();
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
                binExpr.setOp(BinOp.DIV);
            } else if (ctx.MUL() != null) {
                binExpr.setOp(BinOp.MUL);
            } else {
                binExpr.setOp(BinOp.MOD);
            }

            expr = binExpr;
        } else {
            expr = visit(ctx.unaryExpression());
        }

        expr.lineNum = ctx.getStart().getLine();
        return expr;
    }

    @Override
    public ExpressionNode visitUnaryExpression(UnaryExpressionContext ctx) {
        // unaryExpression
        //     : preIncrementExpression
        //     | preDecrementExpression
        //     | '+' unaryExpression
        //     | '-' unaryExpression
        //     | unaryExpressionNotPlusMinus
        //     ;
        ExpressionNode expr = null;
        if (ctx.SUB() != null) {
            UnaryExprNode unaryExpr = new UnaryExprNode();
            unaryExpr.expr = visit(ctx.unaryExpression());
            unaryExpr.op = UnaryOp.MINUS;
            expr = unaryExpr;
        } else {
            expr = super.visitUnaryExpression(ctx);
        }

        expr.lineNum = ctx.getStart().getLine();
        return expr;
    }

    @Override
    public ExpressionNode visitUnaryExpressionNotPlusMinus(UnaryExpressionNotPlusMinusContext ctx) {
        // unaryExpressionNotPlusMinus
        //     : postfixExpression
        //     | '~' unaryExpression
        //     | '!' unaryExpression
        //     | castExpression
        //     ;
        ExpressionNode expr = null;
        if (ctx.TILDE() != null) {
            UnaryExprNode unaryExpr = new UnaryExprNode();
            unaryExpr.expr = visit(ctx.unaryExpression());
            unaryExpr.op = UnaryOp.TILDE;
        } else if (ctx.BANG() != null) {
            UnaryExprNode unaryExpr = new UnaryExprNode();
            unaryExpr.expr = visit(ctx.unaryExpression());
            unaryExpr.op = UnaryOp.NOT;
        } else {
            expr = super.visitUnaryExpressionNotPlusMinus(ctx);
        }

        expr.lineNum = ctx.getStart().getLine();
        return expr;
    }

    @Override
    public ExpressionNode visitRelationalExpression(RelationalExpressionContext ctx) {
        // relationalExpression
        //     : shiftExpression
        //     | relationalExpression '<' shiftExpression
        //     | relationalExpression '>' shiftExpression
        //     | relationalExpression '<=' shiftExpression
        //     | relationalExpression '>=' shiftExpression
        //     | relationalExpression 'instanceof' referenceType
        //     ;

        ExpressionNode expr = null;

        if (ctx.relationalExpression() != null) {
            ExpressionNode left = visit(ctx.relationalExpression());
            ExpressionNode right = visit(ctx.shiftExpression());
            BinaryExprNode binExpr = new BinaryExprNode();
            binExpr.setLeft(left);
            binExpr.setRight(right);
            if (ctx.LT() != null) {
                binExpr.setOp(BinOp.LT);
            } else if (ctx.GT() != null) {
                binExpr.setOp(BinOp.GT);
            } else if (ctx.LE() != null) {
                binExpr.setOp(BinOp.LT_EQ);
            } else if (ctx.GE() != null) {
                binExpr.setOp(BinOp.GT_EQ);
            }

            expr = binExpr;
        } else {
            expr = visit(ctx.shiftExpression());
        }
        
        expr.lineNum = ctx.getStart().getLine();
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
            type = Type.INT_TYPE;
            lit.value = Integer.valueOf(ctx.getText());
        } else if (ctx.FloatingPointLiteral() != null) {
            type = Type.FLOAT_TYPE;
            lit.value = Float.valueOf(ctx.getText());
        } else if (ctx.BooleanLiteral() != null) {
            type = Type.BOOLEAN_TYPE;
            lit.value = Boolean.valueOf(ctx.getText());
        } else if (ctx.CharacterLiteral() != null) {
            type = Type.CHAR_TYPE;
            lit.value = ctx.getText();
        } 
        // else if (ctx.StringLiteral() != null) {
        //     type = Type.OBJECT_TYPE;
        //     lit.value = ctx.getText();
        // } else if (ctx.NullLiteral() != null) {
        //     type = Type.;
        //     lit.value = null;
        // }

        lit.type = type;

        lit.lineNum = ctx.getStart().getLine();
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

        iden.lineNum = ctx.getStart().getLine();
        return iden;
    }

    @Override
    public ExpressionNode visitAssignment(AssignmentContext ctx) {
        // assignment
        //     : leftHandSide assignmentOperator expression
        //     ;
        
        IdentifierExprNode iden = (IdentifierExprNode)visit(ctx.leftHandSide());
        ExpressionNode assignmentVal = visit(ctx.expression());

        AssignExprNode assignment = new AssignExprNode();
        assignment.setVar(iden.varName);
        assignment.setAssignmentValue(assignmentVal);
        assignment.lineNum = ctx.getStart().getLine();
        return assignment;
    }
}
