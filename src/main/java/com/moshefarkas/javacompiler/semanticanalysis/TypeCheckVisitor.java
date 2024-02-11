package com.moshefarkas.javacompiler.semanticanalysis;

import java.util.HashMap;
import java.util.Stack;
import org.objectweb.asm.Type;
import org.stringtemplate.v4.debug.EvalExprEvent;

import com.moshefarkas.javacompiler.SymbolTable;
import com.moshefarkas.javacompiler.ast.nodes.expression.AssignExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.BinaryExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.CallExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.CastExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.IdentifierExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.LiteralExprNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.LocalVarDecStmtNode;

public class TypeCheckVisitor extends SemanticAnalysis {

    // only should check assign and local var. needs to setExprType from gotten type

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
        if (wideningRules.get(a) > wideningRules.get(b)) {
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

    private static HashMap<Type, Integer> wideningRules = new HashMap<>();
    static {
        // check if valid downcast
        wideningRules.put(Type.BYTE_TYPE, 0); // byte is same as int
        wideningRules.put(Type.SHORT_TYPE, 1);
        wideningRules.put(Type.CHAR_TYPE, 2);
        wideningRules.put(Type.INT_TYPE, 3);
        wideningRules.put(Type.FLOAT_TYPE, 4);
    }

    private boolean validAssignment(Type varType, Type assignType) {
        if (wideningRules.get(varType) >= wideningRules.get(assignType)) {
            return true;
        }
        // i into type b is allowed
        if (varType == Type.BYTE_TYPE && assignType == Type.INT_TYPE) { return true; }
        return false;
    }

    @Override
    public void visitAssignExprNode(AssignExprNode node) {
        // Type assignmentType = new TypeEvalVisitor().evalType(node.assignmentValue);
        // Type varType = SymbolTable.getInstance().getVarType(node.identifier.varName);
        // if (!validAssignment(varType, assignmentType)) {
        //     error(ErrorType.MISMATCHED_ASSIGNMENT_TYPE, node.lineNum, 
        //     String.format("cannot assign epxression of type `%s` to type `%s`.", assignmentType, varType));
        // }

        visit(node.assignmentValue);
        Type assignmentType = typeStack.pop();
        Type varType = SymbolTable.getInstance().getVarType(node.identifier.varName);

        if (!validAssignment(varType, assignmentType)) {
            error(ErrorType.MISMATCHED_ASSIGNMENT_TYPE, node.lineNum, 
            String.format("cannot assign epxression of type `%s` to type `%s`.", assignmentType, varType));
        }
        node.assignmentValue.setExprType(assignmentType);
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
        node.setExprType(SymbolTable.getInstance().getReturnType(node.methodName));
    }

    @Override
    public void visitLocalVarDecStmtNode(LocalVarDecStmtNode node) {
        if (!node.var.initialized) {
            return;
        }
        // Type initializerType = new TypeEvalVisitor().evalType(node.initializer);
        // Type varType = SymbolTable.getInstance().getVarType(node.var.name);
        // if (!validAssignment(varType, initializerType)) {
        //     error(ErrorType.MISMATCHED_ASSIGNMENT_TYPE, node.lineNum, 
        //     String.format("cannot assign epxression of type `%s` to type `%s`.", initializerType, varType));
        // }


        visit(node.initializer);
        Type initializerType = typeStack.pop();
        Type varType = SymbolTable.getInstance().getVarType(node.var.name);

        if (!validAssignment(varType, initializerType)) {
            error(ErrorType.MISMATCHED_ASSIGNMENT_TYPE, node.lineNum, 
            String.format("cannot assign epxression of type `%s` to type `%s`.", initializerType, varType));
        }
        node.initializer.setExprType(initializerType);
    }

    private boolean validCast(Type targetCast, Type toCast) {
        if (targetCast == Type.INT_TYPE && toCast == Type.FLOAT_TYPE)
            return true;
        if (targetCast == Type.FLOAT_TYPE && toCast == Type.INT_TYPE)
            return true;
        
        return false;
    }

    @Override
    public void visitCastExprNode(CastExprNode node) {
        visit(node.expression); 
        Type exprType = typeStack.pop();
        if (!validCast(node.targetCast, exprType)) {
            error(
                ErrorType.INVALID_CAST, 
                node.lineNum, 
                String.format("cannot cast type `%s` to type `%s`.", exprType, node.targetCast));
        }
        
        typeStack.push(node.targetCast);
        node.exprType = node.targetCast;
    }
}
