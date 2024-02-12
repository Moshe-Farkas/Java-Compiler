package com.moshefarkas.javacompiler.semanticanalysis;

import java.util.Stack;

import org.objectweb.asm.Type;

import com.moshefarkas.javacompiler.SymbolTable;
import com.moshefarkas.javacompiler.ast.nodes.expression.BinaryExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.CastExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.ExpressionNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.IdentifierExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.IdentifierExprNode.VarIdenExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.LiteralExprNode;

public class TypeEvalVisitor extends SemanticAnalysis {
    
    private Stack<Type> typeStack;

    protected Type evalType(ExpressionNode node) {
        typeStack = new Stack<>();
        visit(node);
        return typeStack.pop();
    }

    @Override
    public void visitBinaryExprNode(BinaryExprNode node) {
        visit(node.left);
        visit(node.right);
        Type right = typeStack.pop();
        Type left = typeStack.pop();

        Type exprType = null;
        if (left == Type.FLOAT_TYPE || right == Type.FLOAT_TYPE) {
            exprType = Type.FLOAT_TYPE;
        } else if (left == Type.INT_TYPE || right == Type.INT_TYPE) {
            exprType = Type.INT_TYPE;
        } else if (left == Type.BYTE_TYPE || right == Type.BYTE_TYPE) {
            exprType = Type.INT_TYPE;
        } else if (left == Type.CHAR_TYPE || right == Type.CHAR_TYPE) {
            exprType = Type.INT_TYPE;
        } else if (left == Type.SHORT_TYPE || right == Type.SHORT_TYPE) {
            exprType = Type.INT_TYPE;
        } 
        typeStack.push(exprType);
        node.setExprType(exprType);
    }

    // @Override
    // public void visitIdentifierExprNode(IdentifierExprNode node) {
    //     Type idenType = SymbolTable.getInstance().getVarType(node.varName);
    //     typeStack.push(idenType);
    //     node.setExprType(idenType);
    // }

    @Override
    public void visitVarIdenExprNode(VarIdenExprNode node) {
        Type idenType = SymbolTable.getInstance().getVarType(node.varName);
        typeStack.push(idenType);
        node.setExprType(idenType);
    }

    @Override
    public void visitLiteralExprNode(LiteralExprNode node) {
        typeStack.push(node.exprType);
    }

    private boolean validCast(Type targetType, Type typeToCast) {
        if (targetType == Type.INT_TYPE && typeToCast == Type.FLOAT_TYPE) 
            return true;
        return false;
    }

    @Override
    public void visitCastExprNode(CastExprNode node) {
        visit(node.expression);
        Type exprType = typeStack.pop();


        typeStack.push(node.targetCast);
        node.setExprType(node.targetCast);
    }
}
