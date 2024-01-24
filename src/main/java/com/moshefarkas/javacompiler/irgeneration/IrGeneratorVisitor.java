package com.moshefarkas.javacompiler.irgeneration;

import java.util.Stack;

import com.moshefarkas.generated.Java8ParserBaseVisitor;
import com.moshefarkas.generated.Java8Parser.AdditiveExpressionContext;
import com.moshefarkas.generated.Java8Parser.AssignmentContext;
import com.moshefarkas.generated.Java8Parser.BlockStatementContext;
import com.moshefarkas.generated.Java8Parser.ExpressionNameContext;
import com.moshefarkas.generated.Java8Parser.FloatingPointTypeContext;
import com.moshefarkas.generated.Java8Parser.IntegralTypeContext;
import com.moshefarkas.generated.Java8Parser.LiteralContext;
import com.moshefarkas.generated.Java8Parser.LocalVariableDeclarationContext;
import com.moshefarkas.generated.Java8Parser.MultiplicativeExpressionContext;
import com.moshefarkas.generated.Java8Parser.VariableDeclaratorContext;
import com.moshefarkas.generated.Java8Parser.VariableDeclaratorIdContext;
import com.moshefarkas.javacompiler.SymbolTable;
import com.moshefarkas.javacompiler.VarInfo;
import com.moshefarkas.javacompiler.irgeneration.IR.Op;

public class IrGeneratorVisitor extends Java8ParserBaseVisitor<Void> {

    public enum Type {
        ERROR,
        INT,
        FLOAT,
        CHAR,
        STRING,
        OBJECT,
        BOOL,
        BYTE, 
        SHORT,
    }

    public enum ErrorType {
        MISMATCHED_TYPE,
        MISMATCHED_ASSIGNMENT_TYPE,
        UNDEFINED_VAR,
        DUPLICATE_VAR,
        UNINITIALIZED_VAR,
        INVALID_LVALUE,
    } 

    private final Stack<Type> typeStack = new Stack<>();
    private final Stack<VarInfo> declaredVarsStack = new Stack<>();
    public final IR ir = new IR();
    public ErrorType test_error = null;

    private void error(ErrorType errType, String errMsg) {
        System.err.println("\u001B[31m" + errType + ": " + errMsg + "\u001B[0m");
        typeStack.push(Type.ERROR);
        test_error = errType;
    }

    private boolean checkTypes(ErrorType errType, Type a, Type b) {
        if (a == Type.ERROR || b == Type.ERROR) {
            typeStack.push(Type.ERROR);
            return false; // an error occured beforehand
        }
        if (a != b) {
            typeStack.push(Type.ERROR);
            error(errType, 
           String.format("type `%s` and type `%s`.", a, b));
           return false;
        } else {
            typeStack.push(a);
            return true;
        }
    }

    @Override
    public Void visitBlockStatement(BlockStatementContext ctx) {
        // blockStatement
        //     : localVariableDeclarationStatement
        //     | classDeclaration
        //     | statement
        //     ;
        typeStack.clear();
        return super.visitBlockStatement(ctx);
    }

    @Override
    public Void visitLocalVariableDeclaration(LocalVariableDeclarationContext ctx) {
        // localVariableDeclaration
        //     : variableModifier* unannType variableDeclaratorList
        //     ;

        visit(ctx.unannType());
        Type type = typeStack.pop();
        visit(ctx.variableDeclaratorList());
        VarInfo declaredVar = declaredVarsStack.pop();
        declaredVar.type = type;
        if (alreadyDefined(declaredVar.name)) {
            error(ErrorType.DUPLICATE_VAR, 
                  String.format("Duplicate local variable `%s`.", declaredVar.name));
        } else {
            SymbolTable.getInstance().addLocal(declaredVar.name, declaredVar);
        }

        if (declaredVar.initialized) {
            Type initializerType = typeStack.pop();
            checkTypes(ErrorType.MISMATCHED_ASSIGNMENT_TYPE, initializerType, type);
        }

        ir.addOP(Op.STORE, declaredVar.name);

        return null; 
    }

    private boolean alreadyDefined(String name) {
        return SymbolTable.getInstance().hasVar(name);
    }
    
    @Override
    public Void visitVariableDeclarator(VariableDeclaratorContext ctx) {
        // variableDeclarator
        //     : variableDeclaratorId ('=' variableInitializer)?
        //     ;
        visit(ctx.variableDeclaratorId());
        VarInfo var = declaredVarsStack.pop();
        if (ctx.variableInitializer() != null) {
            visit(ctx.variableInitializer());
            var.initialized = true;
        }
        declaredVarsStack.push(var);
        return null;
    }

    @Override
    public Void visitVariableDeclaratorId(VariableDeclaratorIdContext ctx) {
        VarInfo var = new VarInfo();
        var.name = ctx.Identifier().getText();
        declaredVarsStack.push(var);
        return null;
    }
    
    @Override
    public Void visitAdditiveExpression(AdditiveExpressionContext ctx) {
        // additiveExpression
        //     : multiplicativeExpression
        //     | additiveExpression '+' multiplicativeExpression
        //     | additiveExpression '-' multiplicativeExpression
        //     ;
        visitChildren(ctx);
        if (typeStack.size() >= 2) {  // if a single type is on the stack there's no need to check
            Type b = typeStack.pop();
            Type a = typeStack.pop();
            checkTypes(ErrorType.MISMATCHED_TYPE, a, b);
            typeStack.push(a);
        }
        // need to check the types
        if (ctx.ADD() != null) {
            ir.addOP(Op.ADD);
        } else if (ctx.SUB() != null) {
            ir.addOP(Op.SUB);
        }
        return null;
    }

    @Override
    public Void visitMultiplicativeExpression(MultiplicativeExpressionContext ctx) {
        // multiplicativeExpression
        //     : unaryExpression
        //     | multiplicativeExpression '*' unaryExpression
        //     | multiplicativeExpression '/' unaryExpression
        //     | multiplicativeExpression '%' unaryExpression
        //     ;
        visitChildren(ctx);
        if (typeStack.size() >= 2) {  // if a single type is on the stack there's no need to check
            Type b = typeStack.pop();
            Type a = typeStack.pop();
            checkTypes(ErrorType.MISMATCHED_TYPE, a, b);
            typeStack.push(a);
        }

        if (ctx.MUL() != null) {
            ir.addOP(Op.MUL);
        } else if (ctx.DIV() != null) {
            ir.addOP(Op.DIV);
        }
        return null;
    }

    @Override
    public Void visitIntegralType(IntegralTypeContext ctx) {
        // integralType
        // : 'byte'
        // | 'short'
        // | 'int'
        // | 'long'
        // | 'char'
        // ;
        switch (ctx.getText()) {
            case "int":
                typeStack.push(Type.INT);
                break;
            case "char":
                typeStack.push(Type.CHAR);
                break;
            case "byte":
                typeStack.push(Type.BYTE);
                break;
        } 

        return null;
    }

    @Override
    public Void visitFloatingPointType(FloatingPointTypeContext ctx) {
        // floatingPointType
        //     : 'float'
        //     | 'double'
        //     ;
        if (ctx.FLOAT() != null) {
            typeStack.push(Type.FLOAT);
        } 
        return null;
    }

    @Override
    public Void visitLiteral(LiteralContext ctx) {
        // literal
        //     : IntegerLiteral
        //     | FloatingPointLiteral
        //     | BooleanLiteral
        //     | CharacterLiteral
        //     | StringLiteral
        //     | NullLiteral
        //     ;
        if (ctx.IntegerLiteral() != null) {
            ir.addOP(Op.LITERAL, ctx.IntegerLiteral());
            typeStack.push(Type.INT);
        } else if (ctx.FloatingPointLiteral() != null) {
            ir.addOP(Op.LITERAL, ctx.FloatingPointLiteral());
            typeStack.push(Type.FLOAT);
        } else if (ctx.CharacterLiteral() != null) {
            ir.addOP(Op.LITERAL, ctx.CharacterLiteral());
            typeStack.push(Type.CHAR);
        } else if (ctx.StringLiteral() != null) {
            ir.addOP(Op.LITERAL, ctx.StringLiteral());
            typeStack.push(Type.STRING);
        }

        return null;
    }

    @Override
    public Void visitExpressionName(ExpressionNameContext ctx) {
        // expressionName
        //     : Identifier
        //     | ambiguousName '.' Identifier
        //     ;

        if (ctx.Identifier() != null) {
            identifierExpression(ctx.Identifier().getText());
        } else {
            visit(ctx.ambiguousName());
        }

        return null;
    }

    private void identifierExpression(String identifier) {
        // need to check if already defined the var 
        if (alreadyDefined(identifier)) {
            VarInfo info = SymbolTable.getInstance().getInfo(identifier);
            if (info.initialized == false) {
                error(ErrorType.UNINITIALIZED_VAR, 
                      String.format("cant use var `%s` because it's uninitialized.", identifier));
            } else {
                typeStack.push(info.type);
            }
        } else {
            error(ErrorType.UNDEFINED_VAR, 
                  String.format("`%s` cannot be resolved to a variable.", identifier));
        }
    }

    @Override
    public Void visitAssignment(AssignmentContext ctx) {
        // assignment
        //     : leftHandSide assignmentOperator expression
        //     ;
        
        // need to add a store op

        // need to check if its 
        // left hand side is the var name
        visit(ctx.leftHandSide());
        visit(ctx.expression());

        return null;
    }


}
