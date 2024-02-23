package com.moshefarkas.javacompiler.ast.astgen;

import java.util.Stack;

import com.moshefarkas.generated.Java8Parser.BlockContext;
import com.moshefarkas.generated.Java8Parser.BlockStatementContext;
import com.moshefarkas.generated.Java8Parser.BreakStatementContext;
import com.moshefarkas.generated.Java8Parser.ConstructorBodyContext;
import com.moshefarkas.generated.Java8Parser.ContinueStatementContext;
import com.moshefarkas.generated.Java8Parser.ExpressionContext;
import com.moshefarkas.generated.Java8Parser.IfThenElseStatementContext;
import com.moshefarkas.generated.Java8Parser.IfThenStatementContext;
import com.moshefarkas.generated.Java8Parser.LocalVariableDeclarationContext;
import com.moshefarkas.generated.Java8Parser.MethodBodyContext;
import com.moshefarkas.generated.Java8Parser.ReturnStatementContext;
import com.moshefarkas.generated.Java8Parser.StatementExpressionContext;
import com.moshefarkas.generated.Java8Parser.WhileStatementContext;
import com.moshefarkas.generated.Java8ParserBaseVisitor;
import com.moshefarkas.javacompiler.VarInfo;
import com.moshefarkas.javacompiler.ast.nodes.expression.ExpressionNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.BlockStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.ControlFlowStmt;
import com.moshefarkas.javacompiler.ast.nodes.statement.ExprStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.IfStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.LocalVarDecStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.ReturnStmt;
import com.moshefarkas.javacompiler.ast.nodes.statement.StatementNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.WhileStmtNode;

public class MethodVisitor extends Java8ParserBaseVisitor<Void> {

    public BlockStmtNode statements = new BlockStmtNode();
    private Stack<ExpressionNode> expressionStack = new Stack<>();
    private Stack<StatementNode> statementStack = new Stack<>();

    @Override
    public Void visitConstructorBody(ConstructorBodyContext ctx) {
        // constructorBody
        //     : '{' explicitConstructorInvocation? blockStatements? '}'
        //     ;
        BlockStmtNode block = new BlockStmtNode();
        if (ctx.blockStatements() != null) {
            for (BlockStatementContext bsc : ctx.blockStatements().blockStatement()) {
                visit(bsc);
                StatementNode statement = statementStack.pop();
                block.addStatement(statement);
            }
        }
        statements = block;
        return null;
    }

    @Override
    public Void visitMethodBody(MethodBodyContext ctx) {
        // methodBody
        //     : block
        //     | ';'
        //     ;
        // this needs to set global block to statementStack.pop(); after visiting ctx.block
        // need to delete all local var stuff.
        visit(ctx.block());

        BlockStmtNode methodBlock = (BlockStmtNode)statementStack.pop();
        statements = methodBlock;
        return null;
    }

    @Override
    public Void visitBlock(BlockContext ctx) {
        // block
        //     : '{' blockStatements? '}'
        //     ;

        BlockStmtNode block = new BlockStmtNode();
        if (ctx.blockStatements() != null) {
            for (BlockStatementContext bsc : ctx.blockStatements().blockStatement()) {
                visit(bsc);
                StatementNode statement = statementStack.pop();
                block.addStatement(statement);
            }
        }
        statementStack.push(block);
        return null;
    }

    @Override
    public Void visitWhileStatement(WhileStatementContext ctx) {
        // whileStatement
        //     : 'while' '(' expression ')' statement
        //     ;
        visit(ctx.expression());
        ExpressionNode condition = expressionStack.pop();
        visit(ctx.statement());
        StatementNode statement = statementStack.pop();

        WhileStmtNode whileNode = new WhileStmtNode();
        whileNode.setCondition(condition);
        whileNode.setStatement(statement);

        whileNode.lineNum = ctx.getStart().getLine();

        statementStack.push(whileNode);
        return null;
    }

    @Override
    public Void visitIfThenStatement(IfThenStatementContext ctx) {
        // ifThenStatement
        //     : 'if' '(' expression ')' statement
        //     ;
        visit(ctx.expression());
        ExpressionNode condition = expressionStack.pop();
        visit(ctx.statement());
        StatementNode statement = statementStack.pop();

        IfStmtNode ifStmtNode = new IfStmtNode();
        ifStmtNode.setCondition(condition);
        ifStmtNode.setIfStatement(statement);

        ifStmtNode.lineNum = ctx.getStart().getLine();
        statementStack.push(ifStmtNode);
        return null;
    }

    @Override
    public Void visitIfThenElseStatement(IfThenElseStatementContext ctx) {
        // ifThenElseStatement
        //     : 'if' '(' expression ')' statementNoShortIf 'else' statement
        //     ;
        visit(ctx.expression());
        ExpressionNode condition = expressionStack.pop();
        visit(ctx.statementNoShortIf());
        StatementNode ifStatement = statementStack.pop();
        visit(ctx.statement());
        StatementNode elseStatement = statementStack.pop();
        IfStmtNode ifStmtNode = new IfStmtNode();
        ifStmtNode.setCondition(condition);
        ifStmtNode.setIfStatement(ifStatement);
        ifStmtNode.setElseStatement(elseStatement);

        ifStmtNode.lineNum = ctx.getStart().getLine();
        statementStack.push(ifStmtNode);
        return null;
    }

    @Override
    public Void visitLocalVariableDeclaration(LocalVariableDeclarationContext ctx) {
        // localVariableDeclaration
        //     : variableModifier* unannType variableDeclaratorList
        //     ;
        VarVisitor varVisitor = new VarVisitor();
        varVisitor.visitLocalVariableDeclaration(ctx);
        LocalVarDecStmtNode localVarDeclNode = new LocalVarDecStmtNode();
        VarInfo varInfo = new VarInfo();
        varInfo.name = varVisitor.varName;
        varInfo.type = varVisitor.varType;
        localVarDeclNode.setVar(varInfo);
        localVarDeclNode.setInitializer(varVisitor.initializer);
        localVarDeclNode.lineNum = ctx.getStart().getLine();
        statementStack.push(localVarDeclNode);
        return null;
    }

    @Override
    public Void visitStatementExpression(StatementExpressionContext ctx) {
        //  statementExpression
        //     : assignment
        //     | preIncrementExpression
        //     | preDecrementExpression
        //     | postIncrementExpression
        //     | postDecrementExpression
        //     | methodInvocation
        //     | classInstanceCreationExpression
        //     ;
        ExpressionVisitor exprVisitor = new ExpressionVisitor();
        ExpressionNode expr;
        expr = (ExpressionNode)exprVisitor.visitStatementExpression(ctx);

        ExprStmtNode exprStmt = new ExprStmtNode();
        exprStmt.setExpression(expr);

        statementStack.push(exprStmt);
        return null;
    }

    @Override
    public Void visitExpression(ExpressionContext ctx) {
        ExpressionVisitor expressionVisitor = new ExpressionVisitor();
        ExpressionNode exprNode = (ExpressionNode)expressionVisitor.visit(ctx);
        expressionStack.push(exprNode);
        return null;
    }

    @Override
    public Void visitReturnStatement(ReturnStatementContext ctx) {
        // returnStatement
        //     : 'return' expression? ';'
        //     ;
        ReturnStmt returnStmt = new ReturnStmt();
        if (ctx.expression() != null) {
            visit(ctx.expression());
            returnStmt.setExpression(expressionStack.pop());
        }
        returnStmt.lineNum = ctx.getStart().getLine();
        statementStack.push(returnStmt);
        return null;
    }

    @Override
    public Void visitBreakStatement(BreakStatementContext ctx) {
        // breakStatement
        //     : 'break' Identifier? ';'
        //     ;
        if (ctx.Identifier() != null) 
            throw new UnsupportedOperationException("inaide visit break stmt in method visitor");
        ControlFlowStmt breakStmt = new ControlFlowStmt();
        breakStmt.setBreak(true);
        breakStmt.lineNum = ctx.getStart().getLine();
        statementStack.push(breakStmt);
        return null;
    }

    @Override
    public Void visitContinueStatement(ContinueStatementContext ctx) {
        // continueStatement
        //     : 'continue' Identifier? ';'
        //     ;
        if (ctx.Identifier() != null) 
            throw new UnsupportedOperationException("inaide visit break stmt in method visitor");
        ControlFlowStmt continueStmt = new ControlFlowStmt();
        continueStmt.setContinue(true);
        continueStmt.lineNum = ctx.getStart().getLine();
        statementStack.push(continueStmt);
        return null;
    }

}
