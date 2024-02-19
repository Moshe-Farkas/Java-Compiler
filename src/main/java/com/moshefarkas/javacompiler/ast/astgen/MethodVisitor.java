package com.moshefarkas.javacompiler.ast.astgen;

import java.util.Stack;

import org.objectweb.asm.Type;

import com.moshefarkas.generated.Java8Parser.BlockContext;
import com.moshefarkas.generated.Java8Parser.BlockStatementContext;
import com.moshefarkas.generated.Java8Parser.ExpressionContext;
import com.moshefarkas.generated.Java8Parser.FloatingPointTypeContext;
import com.moshefarkas.generated.Java8Parser.IfThenElseStatementContext;
import com.moshefarkas.generated.Java8Parser.IfThenStatementContext;
import com.moshefarkas.generated.Java8Parser.IntegralTypeContext;
import com.moshefarkas.generated.Java8Parser.LocalVariableDeclarationContext;
import com.moshefarkas.generated.Java8Parser.MethodBodyContext;
import com.moshefarkas.generated.Java8Parser.ReturnStatementContext;
import com.moshefarkas.generated.Java8Parser.StatementExpressionContext;
import com.moshefarkas.generated.Java8Parser.UnannArrayTypeContext;
import com.moshefarkas.generated.Java8Parser.UnannPrimitiveTypeContext;
import com.moshefarkas.generated.Java8Parser.VariableDeclaratorContext;
import com.moshefarkas.generated.Java8Parser.VariableDeclaratorIdContext;
import com.moshefarkas.generated.Java8Parser.WhileStatementContext;
import com.moshefarkas.generated.Java8ParserBaseVisitor;
import com.moshefarkas.javacompiler.VarInfo;
import com.moshefarkas.javacompiler.ast.nodes.expression.ExpressionNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.BlockStmtNode;
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

    private VarInfo currLocalVarDecl;
    // private Type currLocalVarDeclType;
    private ExpressionNode currVarInitializer = null;
        
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

        // local var node needs exprNode as initializer and varInfo for vafinfo.
        // unannType can be a primitive type or an array/rerference type
        
        // need to reset fields

        currLocalVarDecl = new VarInfo();
        currVarInitializer = null;

        visit(ctx.unannType());
        // currLocalVarDecl.type = currLocalVarDeclType;
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
    public Void visitUnannPrimitiveType(UnannPrimitiveTypeContext ctx) {
        // unannPrimitiveType
        //     : numericType
        //     | 'boolean'
        //     ;
        if (ctx.BOOLEAN() != null) {
            currLocalVarDecl.type = Type.BOOLEAN_TYPE;
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
            currLocalVarDecl.type = Type.FLOAT_TYPE;
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
        Type declType = null;
      switch (ctx.getText()) {
            case "int":
                declType = Type.INT_TYPE;
                break;
            case "char":
                declType = Type.CHAR_TYPE;
                break;
            case "byte":
                declType = Type.BYTE_TYPE;
                break;
            case "short":
                declType = Type.SHORT_TYPE;
                break;
        } 
        currLocalVarDecl.type = declType;
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

    @Override
    public Void visitUnannArrayType(UnannArrayTypeContext ctx) {
        // unannArrayType
        //     : unannPrimitiveType dims
        //     | unannClassOrInterfaceType dims
        //     | unannTypeVariable dims
        //     ;
        int dimsCount = ctx.dims().LBRACK().size();
        visitUnannPrimitiveType(ctx.unannPrimitiveType());
        String dims = "";
        for (int i = 0; i < dimsCount; i++) {
            dims += "[";
        }
        currLocalVarDecl.type = Type.getType(dims + currLocalVarDecl.type.getDescriptor());
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
}
