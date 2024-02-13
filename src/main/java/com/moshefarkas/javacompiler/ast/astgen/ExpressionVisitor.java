package com.moshefarkas.javacompiler.ast.astgen;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Type;

import com.moshefarkas.generated.Java8Parser.AdditiveExpressionContext;
import com.moshefarkas.generated.Java8Parser.ArgumentListContext;
import com.moshefarkas.generated.Java8Parser.ArrayAccessContext;
import com.moshefarkas.generated.Java8Parser.ArrayAccess_lfno_primaryContext;
import com.moshefarkas.generated.Java8Parser.ArrayCreationExpressionContext;
import com.moshefarkas.generated.Java8Parser.AssignmentContext;
import com.moshefarkas.generated.Java8Parser.CastExpressionContext;
import com.moshefarkas.generated.Java8Parser.DimExprContext;
import com.moshefarkas.generated.Java8Parser.EqualityExpressionContext;
import com.moshefarkas.generated.Java8Parser.ExpressionContext;
import com.moshefarkas.generated.Java8Parser.ExpressionNameContext;
import com.moshefarkas.generated.Java8Parser.FloatingPointTypeContext;
import com.moshefarkas.generated.Java8Parser.IntegralTypeContext;
import com.moshefarkas.generated.Java8Parser.LiteralContext;
import com.moshefarkas.generated.Java8Parser.MethodInvocationContext;
import com.moshefarkas.generated.Java8Parser.MultiplicativeExpressionContext;
import com.moshefarkas.generated.Java8Parser.PrimitiveTypeContext;
import com.moshefarkas.generated.Java8Parser.RelationalExpressionContext;
import com.moshefarkas.generated.Java8Parser.UnaryExpressionContext;
import com.moshefarkas.generated.Java8Parser.UnaryExpressionNotPlusMinusContext;
import com.moshefarkas.generated.Java8ParserBaseVisitor;
import com.moshefarkas.javacompiler.ast.nodes.expression.ArrAccessExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.ArrayInitializer;
import com.moshefarkas.javacompiler.ast.nodes.expression.AssignExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.BinaryExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.BinaryExprNode.BinOp;
import com.moshefarkas.javacompiler.ast.nodes.expression.CallExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.CastExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.ExpressionNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.IdentifierExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.LiteralExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.UnaryExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.UnaryExprNode.UnaryOp;

public class ExpressionVisitor extends Java8ParserBaseVisitor<Object> {

    // only return binary expr and literal expresions

    @Override
    public Object visitEqualityExpression(EqualityExpressionContext ctx) {
        // equalityExpression
        //     : relationalExpression
        //     | equalityExpression '==' relationalExpression
        //     | equalityExpression '!=' relationalExpression
        //     ;
        ExpressionNode expr = null;        
        if (ctx.equalityExpression() != null) {
            ExpressionNode left = (ExpressionNode)visitEqualityExpression(ctx.equalityExpression());
            ExpressionNode right = (ExpressionNode)visitRelationalExpression(ctx.relationalExpression());
            BinaryExprNode binaryExpr = new BinaryExprNode();
            binaryExpr.setLeft(left);
            binaryExpr.setRight(right);
            if (ctx.EQUAL() != null) {
                binaryExpr.setOp(BinOp.EQ_EQ);
            } else {
                binaryExpr.setOp(BinOp.NOT_EQ);
            }
            expr = binaryExpr;
        } else {
            expr = (ExpressionNode)visitRelationalExpression(ctx.relationalExpression());
        }

        expr.lineNum = ctx.getStart().getLine();
        return expr;
    }
        
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
            ExpressionNode left = (ExpressionNode)visitAdditiveExpression(ctx.additiveExpression());
            ExpressionNode right = (ExpressionNode)visitMultiplicativeExpression(ctx.multiplicativeExpression());
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
            expr = (ExpressionNode)visitMultiplicativeExpression(ctx.multiplicativeExpression());
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
            ExpressionNode left = (ExpressionNode)visitMultiplicativeExpression(ctx.multiplicativeExpression());
            ExpressionNode right = (ExpressionNode)visitUnaryExpression(ctx.unaryExpression());
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
            expr = (ExpressionNode)visitUnaryExpression(ctx.unaryExpression());
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
            unaryExpr.expr = (ExpressionNode)visitUnaryExpression(ctx.unaryExpression());
            unaryExpr.op = UnaryOp.MINUS;
            expr = unaryExpr;
        } else {
            expr = (ExpressionNode)super.visitUnaryExpression(ctx);
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
            unaryExpr.expr = (ExpressionNode)visitUnaryExpression(ctx.unaryExpression());
            unaryExpr.op = UnaryOp.TILDE;
        } else if (ctx.BANG() != null) {
            UnaryExprNode unaryExpr = new UnaryExprNode();
            unaryExpr.expr = (ExpressionNode)visitUnaryExpression(ctx.unaryExpression());
            unaryExpr.op = UnaryOp.NOT;
        } else if (ctx.postfixExpression() != null) {
            expr = (ExpressionNode)visitPostfixExpression(ctx.postfixExpression());
        } else if (ctx.castExpression() != null) {
            expr = (ExpressionNode)visitCastExpression(ctx.castExpression());
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
            ExpressionNode left = (ExpressionNode)visitRelationalExpression(ctx.relationalExpression());
            ExpressionNode right = (ExpressionNode)visitShiftExpression(ctx.shiftExpression());
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
            expr = (ExpressionNode)visitShiftExpression(ctx.shiftExpression());
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
            lit.value = ctx.getText().charAt(1);
        } else {
            throw new UnsupportedOperationException("inside visitLiteral in expr visitor");
        }

        lit.setExprType(type);
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
        // IdentifierExprNode iden = new VarIdenExprNode();

        iden.lineNum = ctx.getStart().getLine();
        return iden;
    }

    @Override
    public ExpressionNode visitAssignment(AssignmentContext ctx) {
        // assignment
        //     : leftHandSide assignmentOperator expression
        //     ;
        
        IdentifierExprNode iden = (IdentifierExprNode)visitLeftHandSide(ctx.leftHandSide());
        ExpressionNode assignmentVal = (ExpressionNode)visitExpression(ctx.expression());

        AssignExprNode assignment = new AssignExprNode();
        assignment.setIden(iden);
        assignment.setAssignmentValue(assignmentVal);
        assignment.lineNum = ctx.getStart().getLine();
        return assignment;
    }


    @Override
    public ExpressionNode visitMethodInvocation(MethodInvocationContext ctx) {
        // methodInvocation
        //     : methodName '(' argumentList? ')'
        //     | typeName '.' typeArguments? Identifier '(' argumentList? ')'
        //     | expressionName '.' typeArguments? Identifier '(' argumentList? ')'
        //     | primary '.' typeArguments? Identifier '(' argumentList? ')'
        //     | 'super' '.' typeArguments? Identifier '(' argumentList? ')'
        //     | typeName '.' 'super' '.' typeArguments? Identifier '(' argumentList? ')'
        //     ;
        String methodName = ctx.methodName().Identifier().getText();
        CallExprNode callExpr = new CallExprNode();
        callExpr.setMethodName(methodName);
        if (ctx.argumentList() != null) {
            callExpr.setArguments(visitArgumentList(ctx.argumentList()));
        }
        callExpr.lineNum = ctx.getStart().getLine();
        return callExpr;
    }

    @Override
    public List<ExpressionNode> visitArgumentList(ArgumentListContext ctx) {
        // argumentList
        //     : expression (',' expression)*
        //     ;
        List<ExpressionNode> arguments = new ArrayList<>();
        for (ExpressionContext exprCtx : ctx.expression()) {
            arguments.add((ExpressionNode)visitExpression(exprCtx));
        }
        return arguments;
    }

    @Override
    public Object visitCastExpression(CastExpressionContext ctx) {
        // castExpression
        //     : '(' primitiveType ')' unaryExpression
        //     | '(' referenceType additionalBound* ')' unaryExpressionNotPlusMinus
        //     | '(' referenceType additionalBound* ')' lambdaExpression
        //     ;

        CastExprNode castExprNode = new CastExprNode();
        if (ctx.primitiveType() != null) {
            Type targetCast = (Type)visitPrimitiveType(ctx.primitiveType());
            ExpressionNode expr = (ExpressionNode)visitUnaryExpression(ctx.unaryExpression());

            castExprNode.setExpression(expr);
            castExprNode.setTargetCast(targetCast);
        } else {
            throw new UnsupportedOperationException("inside cast expr in expr visitor.");
        }

        return castExprNode;
    }

    @Override
    public Object visitFloatingPointType(FloatingPointTypeContext ctx) {
        if (ctx.FLOAT() != null) {
            return Type.FLOAT_TYPE;
        } else {
            throw new UnsupportedOperationException("inside floating point type expr visitor");
        }
    }

    @Override
    public Object visitIntegralType(IntegralTypeContext ctx) {
        // integralType
        //     : 'byte'
        //     | 'short'
        //     | 'int'
        //     | 'long'
        //     | 'char'
        //     ;
        if (ctx.BYTE() != null) {
            return Type.BYTE_TYPE;
        } else if (ctx.SHORT() != null) {
            return Type.SHORT_TYPE;
        } else if (ctx.INT() != null) {
            return Type.INT_TYPE;
        } else if (ctx.CHAR() != null) {
            return Type.CHAR_TYPE;
        } else {
            throw new UnsupportedOperationException("inside floating point type expr visitor");
        }
    }

    @Override
    public Object visitPrimitiveType(PrimitiveTypeContext ctx) {
        // primitiveType
        //     : annotation* numericType
        //     | annotation* 'boolean'
        //     ;
        if (ctx.BOOLEAN() != null) { 
            return Type.BOOLEAN_TYPE;
        } else {
            return visit(ctx.numericType());
        }
    }

    @Override
    public Object visitArrayAccess_lfno_primary(ArrayAccess_lfno_primaryContext ctx) {
        // arrayAccess_lfno_primary
        //     : (
        //         expressionName '[' expression ']'
        //         | primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary '[' expression ']'
        //     ) (primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary '[' expression ']')*
        //     ;
        return rvalurArrayAccess(
            ctx.expressionName(), 
            ctx.expression(), 
            ctx.getStart().getLine()
        );
    }

    @Override
    public ArrAccessExprNode visitArrayAccess(ArrayAccessContext ctx) {
        // arrayaccess
        //     : (expressionname '[' expression ']' | primarynonewarray_lfno_arrayaccess '[' expression ']') (
        //         primarynonewarray_lf_arrayaccess '[' expression ']'
        //     )*
        //     ;
        // needs expressionName, epxression list, 
        return rvalurArrayAccess(
            ctx.expressionName(), 
            ctx.expression(), 
            ctx.getStart().getLine()
        );
    }

    private ArrAccessExprNode rvalurArrayAccess(
        ExpressionNameContext exprName,
        List<ExpressionContext> epxressions,
        int lineNum
        ) 
    {

        IdentifierExprNode iden = (IdentifierExprNode)visitExpressionName(exprName);
        ExpressionNode index = (ExpressionNode)visitExpression(epxressions.get(0));
        ArrAccessExprNode accessExprNode = new ArrAccessExprNode();
        accessExprNode.setIdentifer(iden);
        accessExprNode.setIndex(index);
        accessExprNode.setVarName(iden.varName);
        
        for (int i = 1; i < epxressions.size(); i++) {
            ArrAccessExprNode temp = new ArrAccessExprNode();
            temp.setIdentifer(accessExprNode);
            temp.setVarName(iden.varName);
            temp.setIndex((ExpressionNode)visitExpression(epxressions.get(i)));
            temp.lineNum = lineNum;
            accessExprNode = temp;            
        }

        accessExprNode.lineNum = lineNum;
        accessExprNode.setVarName(iden.varName);
        return accessExprNode;
    }

    @Override
    public ArrayInitializer visitArrayCreationExpression(ArrayCreationExpressionContext ctx) {
        // arrayCreationExpression
        //     : 'new' primitiveType dimExprs dims?
        //     | 'new' classOrInterfaceType dimExprs dims?
        //     | 'new' primitiveType dims arrayInitializer
        //     | 'new' classOrInterfaceType dims arrayInitializer
        //     ;
        // array initilizer: list of varDecls.
        // for empty array creation this list will be empty.
        
        ArrayInitializer arrayInitializer = new ArrayInitializer();
        List<ExpressionNode> sizes = new ArrayList<>();
        String dimsStr = "";
        for (DimExprContext dexc : ctx.dimExprs().dimExpr()) {
            sizes.add((ExpressionNode)visitExpression(dexc.expression()));
            dimsStr += "[";
        }
        arrayInitializer.setSetArraySizes(sizes);
        int dims = sizes.size();
        if (ctx.dims() != null)
            for (int i = 0; i < ctx.dims().LBRACK().size(); i++) {
                dims++;
                dimsStr += "[";
            }
        arrayInitializer.setDims(dims);

        Type arrayInitType = (Type)visitPrimitiveType(ctx.primitiveType());
        arrayInitializer.setType(Type.getType(dimsStr + arrayInitType.getDescriptor()));

        return  arrayInitializer;
    }
}
