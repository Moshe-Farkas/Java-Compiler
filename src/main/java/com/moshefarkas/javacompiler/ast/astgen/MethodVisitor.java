package com.moshefarkas.javacompiler.ast.astgen;

import java.util.Stack;

import com.moshefarkas.generated.Java8Parser.BlockContext;
import com.moshefarkas.generated.Java8Parser.BlockStatementContext;
import com.moshefarkas.generated.Java8Parser.ExpressionContext;
import com.moshefarkas.generated.Java8Parser.FloatingPointTypeContext;
import com.moshefarkas.generated.Java8Parser.IfThenStatementContext;
import com.moshefarkas.generated.Java8Parser.IntegralTypeContext;
import com.moshefarkas.generated.Java8Parser.LocalVariableDeclarationContext;
import com.moshefarkas.generated.Java8Parser.MethodBodyContext;
import com.moshefarkas.generated.Java8Parser.StatementExpressionContext;
import com.moshefarkas.generated.Java8Parser.UnannPrimitiveTypeContext;
import com.moshefarkas.generated.Java8Parser.VariableDeclaratorContext;
import com.moshefarkas.generated.Java8Parser.VariableDeclaratorIdContext;
import com.moshefarkas.generated.Java8Parser.WhileStatementContext;
import com.moshefarkas.generated.Java8ParserBaseVisitor;
import com.moshefarkas.javacompiler.Value.Type;
import com.moshefarkas.javacompiler.VarInfo;
import com.moshefarkas.javacompiler.ast.nodes.expression.ExpressionNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.BlockStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.ExprStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.IfStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.LocalVarDecStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.StatementNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.WhileStmtNode;

public class MethodVisitor extends Java8ParserBaseVisitor<Void> {

    public BlockStmtNode statements;
    private Stack<ExpressionNode> expressionStack = new Stack<>();
    private Stack<StatementNode> statementStack = new Stack<>();

    private int localVarIndex = 0;
    private VarInfo currLocalVarDecl;
    private ExpressionNode currVarInitializer = null;
    private Type currLocalVarDeclType;
        
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
        for (BlockStatementContext bsc : ctx.blockStatements().blockStatement()) {
            visit(bsc);
            StatementNode statement = statementStack.pop();
            block.addStatement(statement);
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
        ifStmtNode.setStatement(statement);

        ifStmtNode.lineNum = ctx.getStart().getLine();
        statementStack.push(ifStmtNode);
        return null;
    }

    @Override
    public Void visitLocalVariableDeclaration(LocalVariableDeclarationContext ctx) {
        // localVariableDeclaration
        //     : variableModifier* unannType variableDeclaratorList
        //     ;
        
        // need to reset fields
        currLocalVarDecl = new VarInfo();
        currLocalVarDecl.localIndex = localVarIndex++;
        currLocalVarDeclType = null;
        currVarInitializer = null;

        visit(ctx.unannType());
        currLocalVarDecl.type = currLocalVarDeclType;
        visit(ctx.variableDeclaratorList());
        
        LocalVarDecStmtNode lclVarNode = new LocalVarDecStmtNode();
        lclVarNode.setVar(currLocalVarDecl);
        lclVarNode.setInitializer(currVarInitializer);
        
        lclVarNode.lineNum = ctx.getStart().getLine();

        statementStack.push(lclVarNode);
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

        if (ctx.assignment() != null) {
            expr = exprVisitor.visitAssignment(ctx.assignment());
        } else {
            throw new UnsupportedOperationException("inside expression statement.");
        }

        ExprStmtNode exprStmt = new ExprStmtNode();
        exprStmt.setExpression(expr);

        statementStack.push(exprStmt);
        return null;
    }

    @Override
    public Void visitExpression(ExpressionContext ctx) {
        ExpressionVisitor expressionVisitor = new ExpressionVisitor();
        ExpressionNode exprNode = expressionVisitor.visit(ctx);
        expressionStack.push(exprNode);
        return null;
    }

    @Override
    public Void visitUnannPrimitiveType(UnannPrimitiveTypeContext ctx) {
        // unannPrimitiveType
        //     : numericType
        //     | 'boolean'
        //     ;
        if (ctx.BOOLEAN() != null) {
            currLocalVarDeclType = Type.BOOL;
        } else {
            visit(ctx.numericType());
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
            currLocalVarDeclType = Type.FLOAT;
        } 
        return null;
    }

    @Override
    public Void visitIntegralType(IntegralTypeContext ctx) {
        // integralType
        //     : 'byte'
        //     | 'short'
        //     | 'int'
        //     | 'long'
        //     | 'char'
        //     ;
      switch (ctx.getText()) {
            case "int":
                currLocalVarDeclType = Type.INT;
                break;
            case "char":
                currLocalVarDeclType = Type.CHAR;
                break;
            case "byte":
                currLocalVarDeclType = Type.BYTE;
                break;
            case "short":
                currLocalVarDeclType = Type.SHORT;
                break;
        } 
        return null;
    }

    @Override
    public Void visitVariableDeclarator(VariableDeclaratorContext ctx) {
        // variableDeclarator
        //     : variableDeclaratorId ('=' variableInitializer)?
        //     ;
        
        if (ctx.variableInitializer() != null) {
            currLocalVarDecl.initialized = true;
            visit(ctx.variableInitializer());
            currVarInitializer = expressionStack.pop();            
        }
        visit(ctx.variableDeclaratorId());

        return null;
    }

    @Override
    public Void visitVariableDeclaratorId(VariableDeclaratorIdContext ctx) {
        // variableDeclaratorId
        //     : Identifier dims?
        //     ;
        currLocalVarDecl.name = ctx.Identifier().getText();
        return null;
    }
}
