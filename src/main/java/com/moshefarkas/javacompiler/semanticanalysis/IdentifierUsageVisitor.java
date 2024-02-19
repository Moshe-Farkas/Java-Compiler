package com.moshefarkas.javacompiler.semanticanalysis;

import java.util.Collections;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.moshefarkas.javacompiler.ast.nodes.MethodNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.CallExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.IdentifierExprNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.BlockStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.ReturnStmt;
import com.moshefarkas.javacompiler.symboltable.MethodManager;
import com.moshefarkas.javacompiler.symboltable.SymbolTable;

public class IdentifierUsageVisitor extends SemanticAnalysis {
    // responsible for checking if var is defined, and initialized.

    // need to check if a non void method contains a return statement

    private String currMethod;
    private boolean seenReturnStmt;

    @Override
    public void visitIdentifierExprNode(IdentifierExprNode node) {
        String varName = node.varName;
        SymbolTable methodSymbolTable  = MethodManager.getInstance().getSymbolTable(currMethod);
        if (!methodSymbolTable.hasVar(varName)) {
            error(ErrorType.UNDEFINED_IDENTIFIER, node.lineNum, varName);
        } else if (methodSymbolTable.getVarInfo(varName).initialized == false) {
            error(ErrorType.UNINITIALIZED_VAR, node.lineNum, varName);
        } 
    }

    @Override
    public void visitCallExprNode(CallExprNode node) {
        if (!MethodManager.getInstance().hasMethod(node.methodName)) {
            error(
                ErrorType.UNDEFINED_IDENTIFIER, 
                node.lineNum, 
                errorString("Undefined method `%s`.", node.methodName)
            );
            return;
        }
        Type[] methodParamsTypes = MethodManager.getInstance().getParamTypes(node.methodName);
        if (methodParamsTypes.length != node.arguments.size()) {
            String reason = String.format("Expected `%d` args but got `%d`.", methodParamsTypes.length, node.arguments.size());
            error(ErrorType.MISMATCHED_ARGUMENTS, node.lineNum, reason);
        } 
        super.visitCallExprNode(node);
    }

    @Override
    public void visitMethodNode(MethodNode node) {
        currMethod = node.methodName;
        MethodManager.getInstance().getSymbolTable(currMethod).resetScopes();
        validateMethodModifiers(node);

        seenReturnStmt = false;
        super.visitMethodNode(node);
        Type currMethodRetType = MethodManager.getInstance().getReturnType(currMethod);
        if (currMethodRetType != Type.VOID_TYPE && !seenReturnStmt) {
            error(
                ErrorType.MISSING_RET_STMT, 
                node.lineNum, 
                errorString(
                    "method `%s` needs a return statement of type `%s`.", 
                    currMethod, currMethodRetType
                )
            );
        }
    }

    private void validateMethodModifiers(MethodNode node) {
        boolean seenAccessMod = false;
        for (int i = 0; i < node.methodModifiers.size(); i++) {
            int modifer = node.methodModifiers.get(i);
            if (Collections.frequency(node.methodModifiers, modifer) > 1) {
                error(
                    ErrorType.INVALID_METHOD_HEADER, 
                    node.lineNum, 
                    "Cannot use a modifer more than once."
                );
                node.methodModifiers.remove((Object)modifer);
            }

            switch (modifer) {
                case Opcodes.ACC_PRIVATE:
                case Opcodes.ACC_PUBLIC:
                case Opcodes.ACC_PROTECTED:
                    if (seenAccessMod) {
                        error(
                            ErrorType.INVALID_METHOD_HEADER, 
                            node.lineNum, 
                            "can't have more than one access modifier."
                        );
                    }
                    seenAccessMod = true;
                    break;
                case Opcodes.ACC_STATIC:
                case Opcodes.ACC_ABSTRACT:
                    int temp = modifer == Opcodes.ACC_STATIC ? Opcodes.ACC_ABSTRACT : Opcodes.ACC_STATIC;
                    if (node.methodModifiers.contains(temp)) {
                        error(
                            ErrorType.INVALID_METHOD_HEADER, 
                            node.lineNum, 
                            "can't have a method be abstract and static."
                        );
                    }
                    break;
            }
        }
    }

    @Override
    public void visitBlockStmtNode(BlockStmtNode node) {
        SymbolTable methodSymbolTable = MethodManager.getInstance().getSymbolTable(currMethod);
        methodSymbolTable.enterScope();
        super.visitBlockStmtNode(node);
        methodSymbolTable.exitScope();
    }

    @Override
    public void visitReturnStmt(ReturnStmt node) {
        seenReturnStmt = true;
        super.visitReturnStmt(node);
    }
}