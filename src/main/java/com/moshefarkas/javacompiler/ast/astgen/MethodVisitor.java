package com.moshefarkas.javacompiler.ast.astgen;

import java.util.Stack;

import com.moshefarkas.generated.Java8Parser.ExpressionContext;
import com.moshefarkas.generated.Java8Parser.WhileStatementContext;
import com.moshefarkas.generated.Java8ParserBaseVisitor;
import com.moshefarkas.javacompiler.ast.nodes.expression.ExpressionNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.BlockStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.WhileStmtNode;

public class MethodVisitor extends Java8ParserBaseVisitor<Void> {

    // exposes a block of statements
    public BlockStmtNode statements = new BlockStmtNode();

    private Stack<ExpressionNode> expressionStack = new Stack<>();

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
    public Void visitExpression(ExpressionContext ctx) {
        ExpressionVisitor expressionVisitor = new ExpressionVisitor();
        ExpressionNode exprNode = expressionVisitor.visit(ctx);
        expressionStack.push(exprNode);
        System.out.println(exprNode);
        return null;
    }
}
