package com.moshefarkas.javacompiler.semanticanalysis;

import java.util.HashMap;
import java.util.Stack;
import org.objectweb.asm.Type;

import com.moshefarkas.javacompiler.SymbolTable;
import com.moshefarkas.javacompiler.ast.nodes.expression.AssignExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.BinaryExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.IdentifierExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.LiteralExprNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.LocalVarDecStmtNode;

public class TypeCheckVisitor extends SemanticAnalysis {

    protected Stack<Type> typeStack = new Stack<>();

    @Override
    public void visitBinaryExprNode(BinaryExprNode node) {
        visit(node.left);
        visit(node.right);

        Type b = typeStack.pop();
        Type a = typeStack.pop();

        if (a == Type.FLOAT_TYPE || b == Type.FLOAT_TYPE) {
            node.setExprType(Type.FLOAT_TYPE);
        } else {
            node.setExprType(a);
        }
    }

    @Override
    public void visitLiteralExprNode(LiteralExprNode node) {
        typeStack.push(node.type);
    }

    @Override
    public void visitIdentifierExprNode(IdentifierExprNode node) {
        typeStack.push(SymbolTable.getInstance().getType(node.varName));
    }

    private static HashMap<Type, Integer> downcastRules = new HashMap<>();
    static {
        // check if valid downcast
        downcastRules.put(Type.BYTE_TYPE, 0);
        downcastRules.put(Type.SHORT_TYPE, 1);
        downcastRules.put(Type.CHAR_TYPE, 2);
        downcastRules.put(Type.INT_TYPE, 3);
        downcastRules.put(Type.FLOAT_TYPE, 4);
    }

    private boolean validAssignment(Type varType, Type assignType) {
        if (downcastRules.get(varType) >= downcastRules.get(assignType)) {
            return true;
        }
        return false;
    }

    @Override
    public void visitAssignExprNode(AssignExprNode node) {
        visit(node.assignmentValue);
        Type assignmentType = typeStack.pop();
        Type varType = SymbolTable.getInstance().getType(node.varName);

        if (!validAssignment(varType, assignmentType)) {
            error(ErrorType.MISMATCHED_ASSIGNMENT_TYPE, node.lineNum, 
            String.format("cannot assign epxression of type `%s` to type `%s`.", assignmentType, varType));
        }
    }

    @Override
    public void visitLocalVarDecStmtNode(LocalVarDecStmtNode node) {
        if (!node.var.initialized) {
            return;
        }
        visit(node.initializer);
        Type assignmentType = typeStack.pop();
        Type varType = SymbolTable.getInstance().getType(node.var.name);

        if (!validAssignment(varType, assignmentType)) {
            error(ErrorType.MISMATCHED_ASSIGNMENT_TYPE, node.lineNum, 
            String.format("cannot assign epxression of type `%s` to type `%s`.", assignmentType, varType));
        }
    }
}
