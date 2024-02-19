package com.moshefarkas.javacompiler.semanticanalysis;

import java.util.HashMap;
import java.util.Stack;

import org.objectweb.asm.Type;

import com.moshefarkas.javacompiler.ast.nodes.MethodNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.ArrAccessExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.ArrayInitializerNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.AssignExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.BinaryExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.CallExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.CastExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.ExpressionNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.IdentifierExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.LiteralExprNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.BlockStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.IfStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.LocalVarDecStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.ReturnStmt;
import com.moshefarkas.javacompiler.symboltable.Method;
import com.moshefarkas.javacompiler.symboltable.MethodManager;
import com.moshefarkas.javacompiler.symboltable.SymbolTable;

public class TypeCheckVisitor extends SemanticAnalysis {

    private class SemanticError extends RuntimeException {}

    private Stack<Type> typeStack = new Stack<>();
    private String currMethod;

    private Type lookupType(Type a, Type b) {
        //            int    float bool   char  byte
        // int        int    float null    I     I
        // float      float  float null    F     F
        // bool       null   null, bool    null null
        // char       I       F    null    I     I
        // byte       I       F    null    I     I

        if (a == Type.INT_TYPE) {
            if (b == Type.INT_TYPE)     return Type.INT_TYPE;
            if (b == Type.FLOAT_TYPE)   return Type.FLOAT_TYPE;
            if (b == Type.BOOLEAN_TYPE) return null;
            if (b == Type.CHAR_TYPE)    return Type.INT_TYPE;
            if (b == Type.BYTE_TYPE)    return Type.INT_TYPE;
        } else if (a == Type.FLOAT_TYPE) {
            if (b == Type.FLOAT_TYPE)   return Type.FLOAT_TYPE;
            if (b == Type.INT_TYPE)     return Type.FLOAT_TYPE;
            if (b == Type.CHAR_TYPE)    return Type.FLOAT_TYPE;
            if (b == Type.BYTE_TYPE)    return Type.FLOAT_TYPE;
            if (b == Type.BOOLEAN_TYPE) return null;
        } else if (a == Type.CHAR_TYPE) {
            if (b == Type.FLOAT_TYPE)   return Type.FLOAT_TYPE;
            if (b == Type.INT_TYPE)     return Type.INT_TYPE;
            if (b == Type.CHAR_TYPE)    return Type.INT_TYPE;
            if (b == Type.BYTE_TYPE)    return Type.INT_TYPE;
            if (b == Type.BOOLEAN_TYPE) return null;
        } else if (a == Type.BYTE_TYPE) {
            if (b == Type.FLOAT_TYPE)   return Type.FLOAT_TYPE;
            if (b == Type.INT_TYPE)     return Type.INT_TYPE;
            if (b == Type.CHAR_TYPE)    return Type.INT_TYPE;
            if (b == Type.BYTE_TYPE)    return Type.INT_TYPE;
        }

        return null;
    }

    @Override
    public void visitBinaryExprNode(BinaryExprNode node) {
        // need to handle boolean binary expression
        visit(node.left);
        visit(node.right);
        Type a = typeStack.pop();
        Type b = typeStack.pop();
        switch (node.op) {
            case EQ_EQ:
            case NOT_EQ:
                if (a == Type.BOOLEAN_TYPE && b == Type.BOOLEAN_TYPE) {
                    node.setDomType(Type.BOOLEAN_TYPE);
                    node.setExprType(Type.BOOLEAN_TYPE);
                    typeStack.push(Type.BOOLEAN_TYPE);
                    break;
                }
            case GT   :
            case GT_EQ:
            case LT   :
            case LT_EQ:
                binaryBoolOp(node, a, b);
                break;
            default:
                binaryOp(node, a, b);
        }
    }

    private void binaryBoolOp(BinaryExprNode node, Type a, Type b) {
        // need to set dom type
        Type castType = lookupType(a, b);
        if (castType == null) {
            error(
                ErrorType.INVALID_OPERATOR_TYPES, // migrate to invalid op type
                node.lineNum, 
                errorString("Can't use `%s` for type `%s` with type `%s`.", node.op, a, b)
            );
            throw new SemanticError();
        }

        node.setDomType(castType);
        node.setExprType(Type.BOOLEAN_TYPE);
        typeStack.push(Type.BOOLEAN_TYPE);
    }

    private void binaryOp(BinaryExprNode node, Type a, Type b) {
        Type castType = lookupType(a, b);
        if (a == Type.BOOLEAN_TYPE && b == Type.BOOLEAN_TYPE) castType = null;
        if (castType == null) {
            error(
                ErrorType.INVALID_OPERATOR_TYPES, // migrate to invalid op type
                node.lineNum, 
                errorString("Can't use `%s` for type `%s` with type `%s`.", node.op, a, b)
            );
            throw new SemanticError();
        }
        node.setDomType(castType);
        node.setExprType(castType);
        typeStack.push(castType);
    }

    @Override
    public void visitLiteralExprNode(LiteralExprNode node) {
        typeStack.push(node.exprType);
    }

    @Override
    public void visitIdentifierExprNode(IdentifierExprNode node) {
        Type idenType = MethodManager.getInstance().getSymbolTable(currMethod).getVarType(node.varName);
        
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
        if (varType == Type.BOOLEAN_TYPE && assignType == Type.BOOLEAN_TYPE)
            return true;
        if (varType == Type.BOOLEAN_TYPE || assignType == Type.BOOLEAN_TYPE)
            return false;
        if (varType.getSort() == Type.ARRAY || assignType.getSort() == Type.ARRAY) {
            if (varType.equals(assignType)) {
                return true;
            } 
            return false;
        }
        
        if (!wideningRules.containsKey(assignType) || !wideningRules.containsKey(varType)) {
            return false;
        }

        if (wideningRules.get(varType) >= wideningRules.get(assignType)) {
            return true;
        }
        // i into type b is allowed
        if (varType == Type.BYTE_TYPE && assignType == Type.INT_TYPE) { return true; }
        return false;
    }

    @Override
    public void visitAssignExprNode(AssignExprNode node) {
        visit(node.identifier);
        Type idenType = typeStack.pop();
        visit(node.assignmentValue);
        Type assignmentType = typeStack.pop();

        if (!validAssignment(idenType, assignmentType)) {
            error(
                ErrorType.MISMATCHED_ASSIGNMENT_TYPE, 
                node.lineNum, 
                errorString("cannot assign epxression of type `%s` to type `%s`.", assignmentType, idenType)
            );
        }
        node.assignmentValue.setExprType(assignmentType);
    }

    @Override
    public void visitCallExprNode(CallExprNode node) {
        Type[] paramTypes = MethodManager.getInstance().getParamTypes(node.methodName);

        for (int i = 0; i < node.arguments.size(); i++) {
            try {
                visit(node.arguments.get(i));
                Type argType = typeStack.pop();
                Type paramType = paramTypes[i];
                if (!validAssignment(paramType, argType)) {
                    error(
                        ErrorType.MISMATCHED_ARGUMENTS, 
                        node.lineNum, 
                        errorString(
                            "Expcected type `%s` but got `%s` as an arg instead.",
                            paramType, argType
                        )
                    );
                }
            } catch (SemanticError e) {typeStack.clear();}
        }
        Type calleReturnType = MethodManager.getInstance().getReturnType(node.methodName);
        node.setExprType(calleReturnType);
        typeStack.push(calleReturnType);
    }

    @Override
    public void visitLocalVarDecStmtNode(LocalVarDecStmtNode node) {
        if (!node.var.initialized) {
            return;
        }
        try {
            visit(node.initializer);
            Type initializerType = typeStack.pop();
            Type varType = MethodManager.getInstance().getSymbolTable(currMethod).getVarType(node.var.name);

            if (!validAssignment(varType, initializerType)) {
                error(
                    ErrorType.MISMATCHED_ASSIGNMENT_TYPE, 
                    node.lineNum, 
                    errorString(
                        "cannot assign epxression of type `%s` to type `%s`.",
                        initializerType, varType
                    )
                );
            }
        } catch (SemanticError e) {typeStack.clear();}
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
                errorString(
                    "cannot cast type `%s` to type `%s`.", 
                    exprType, node.targetCast
                )
            );
        }
        
        typeStack.push(node.targetCast);
        node.exprType = node.targetCast;
    }

    @Override
    public void visitArrayInitializer(ArrayInitializerNode node) {
        for (ExpressionNode size : node.arraySizes) {
            visit(size);
            Type indexType = typeStack.pop();
            if (indexType != Type.INT_TYPE) {
                error(
                    ErrorType.INVALID_ARRAY_INIT, 
                    node.lineNum, 
                    errorString(
                        "cannot use type `%s` to init an array",
                        indexType
                    )
                );
            }
        }

        typeStack.push(node.type);
        node.setExprType(node.type);
    }

    @Override
    public void visitArrAccessExprNode(ArrAccessExprNode node) {
        visit(node.identifer);
        Type subIdenType = typeStack.pop();
        Type innerIdenType = null;
        if (subIdenType.getSort() == Type.ARRAY) {
            // remove one dimmension
            String typeStr = subIdenType.toString();
            innerIdenType = Type.getType(typeStr.toString().substring(1));
        }
        node.setExprType(innerIdenType);
        typeStack.push(innerIdenType);
    }

    @Override
    public void visitIfStmtNode(IfStmtNode node) {
        try {
            visit(node.condition);
            Type conditionType = typeStack.pop();
            if (conditionType != Type.BOOLEAN_TYPE) {
                error(
                    ErrorType.MISMATCHED_TYPE, 
                    node.lineNum, 
                    errorString(
                        "Can't use epxression of type `%s`. Condition need to be bool.", 
                        conditionType
                    )
                );
            }
        } catch (SemanticError e) { typeStack.clear(); }
        
        visit(node.ifStatement);
    }

    @Override
    public void visitBlockStmtNode(BlockStmtNode node) {
        SymbolTable methodSymbolTable = MethodManager.getInstance().getSymbolTable(currMethod);
        methodSymbolTable.enterScope();
        super.visitBlockStmtNode(node);
        methodSymbolTable.exitScope();
    }

    @Override
    public void visitMethodNode(MethodNode node) {
        currMethod = node.methodName;
        MethodManager.getInstance().getSymbolTable(currMethod).resetScopes();
        super.visitMethodNode(node);
    }

    @Override
    public void visitReturnStmt(ReturnStmt node) {
        // need to check if incorrect type
        if (node.expression == null) 
            return;
        visit(node.expression);
        Type exprType = typeStack.pop();
        Type currMethodRetType = MethodManager.getInstance().getReturnType(currMethod);
        if (!validAssignment(currMethodRetType, exprType)) {
            error(
                ErrorType.MISMATCHED_TYPE, 
                node.lineNum, 
                errorString(
                    "Can't return epxression of type `%s`. Expected type `%s`.", 
                    exprType, currMethodRetType
                )
            );
        }
    }
}
