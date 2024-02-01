package com.moshefarkas.javacompiler.ast.astgen;

import java.util.Stack;

import com.moshefarkas.generated.Java8Parser.ExpressionContext;
import com.moshefarkas.generated.Java8Parser.FloatingPointTypeContext;
import com.moshefarkas.generated.Java8Parser.IntegralTypeContext;
import com.moshefarkas.generated.Java8Parser.LocalVariableDeclarationContext;
import com.moshefarkas.generated.Java8Parser.UnannPrimitiveTypeContext;
import com.moshefarkas.generated.Java8Parser.VariableDeclaratorContext;
import com.moshefarkas.generated.Java8Parser.VariableDeclaratorIdContext;
import com.moshefarkas.generated.Java8Parser.WhileStatementContext;
import com.moshefarkas.generated.Java8ParserBaseVisitor;
import com.moshefarkas.javacompiler.Value.Type;
import com.moshefarkas.javacompiler.VarInfo;
import com.moshefarkas.javacompiler.ast.nodes.expression.ExpressionNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.BlockStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.LocalVarDecStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.WhileStmtNode;

public class MethodVisitor extends Java8ParserBaseVisitor<Void> {

    public BlockStmtNode statements = new BlockStmtNode();
    private Stack<ExpressionNode> expressionStack = new Stack<>();

    private int localVarIndex = 0;
    private VarInfo currLocalVarDecl;
    private ExpressionNode currVarInitializer = null;
    private Type currLocalVarDeclType;

    @Override
    public Void visitWhileStatement(WhileStatementContext ctx) {
        WhileStmtNode node = new WhileStmtNode();
        
        visit(ctx.expression());
        ExpressionNode condition = expressionStack.pop();
        node.setCondition(condition);

        statements.addStatement(node);
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
        
        statements.addStatement(lclVarNode);
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
    public Void visitExpression(ExpressionContext ctx) {
        ExpressionVisitor expressionVisitor = new ExpressionVisitor();
        ExpressionNode exprNode = expressionVisitor.visit(ctx);
        expressionStack.push(exprNode);
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
