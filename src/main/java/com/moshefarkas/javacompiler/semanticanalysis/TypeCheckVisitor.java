package com.moshefarkas.javacompiler.semanticanalysis;

import java.util.HashMap;
import java.util.Stack;
import org.objectweb.asm.Type;

import com.moshefarkas.javacompiler.SymbolTable;
import com.moshefarkas.javacompiler.ast.nodes.expression.AssignExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.BinaryExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.CallExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.IdentifierExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.LiteralExprNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.LocalVarDecStmtNode;

public class TypeCheckVisitor extends SemanticAnalysis {

    private Stack<Type> typeStack = new Stack<>();

    @Override
    public void visitBinaryExprNode(BinaryExprNode node) {
        visit(node.left);
        visit(node.right);

        Type b = typeStack.pop();
        Type a = typeStack.pop();
        // char, byte and short are treated as if int
        if (a == Type.CHAR_TYPE || a == Type.BYTE_TYPE || a == Type.SHORT_TYPE) {
            a = Type.INT_TYPE;
        }
        if (b == Type.CHAR_TYPE || b == Type.BYTE_TYPE || b == Type.SHORT_TYPE) {
            b = Type.INT_TYPE;
        }
        Type exprType;
        if (downcastRules.get(a) > downcastRules.get(b)) {
            exprType = a;
        } else {
            exprType = b;
        }
        node.setExprType(exprType);
        typeStack.push(exprType);
    }

    @Override
    public void visitLiteralExprNode(LiteralExprNode node) {
        typeStack.push(node.exprType);
    }

    @Override
    public void visitIdentifierExprNode(IdentifierExprNode node) {
        Type idenType = SymbolTable.getInstance().getVarType(node.varName);
        typeStack.push(idenType);
        node.setExprType(idenType);
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
        Type varType = SymbolTable.getInstance().getVarType(node.varName);

        if (!validAssignment(varType, assignmentType)) {
            error(ErrorType.MISMATCHED_ASSIGNMENT_TYPE, node.lineNum, 
            String.format("cannot assign epxression of type `%s` to type `%s`.", assignmentType, varType));
        }
    }

    @Override
    public void visitCallExprNode(CallExprNode node) {
        Type[] paramTypes = SymbolTable.getInstance().getParamTypes(node.methodName);
        for (int i = 0; i < node.arguments.size(); i++) {
            visit(node.arguments.get(i));
            Type argType = typeStack.pop();
            Type paramType = paramTypes[i];
            if (!validAssignment(paramType, argType)) {
                error(
                    ErrorType.MISMATCHED_ARGUMENTS, 
                    node.lineNum, 
                    String.format(
                        "Expcected type `%s` but got `%s` as an arg instead.", 
                        paramType, argType
                    )
                );
            }
        }
    }

    @Override
    public void visitLocalVarDecStmtNode(LocalVarDecStmtNode node) {
        if (!node.var.initialized) {
            return;
        }
        visit(node.initializer);
        Type assignmentType = typeStack.pop();
        Type varType = SymbolTable.getInstance().getVarType(node.var.name);

        if (!validAssignment(varType, assignmentType)) {
            error(ErrorType.MISMATCHED_ASSIGNMENT_TYPE, node.lineNum, 
            String.format("cannot assign epxression of type `%s` to type `%s`.", assignmentType, varType));
        }
    }
}
