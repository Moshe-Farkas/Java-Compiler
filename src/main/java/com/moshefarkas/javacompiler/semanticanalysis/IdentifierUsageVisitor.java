package com.moshefarkas.javacompiler.semanticanalysis;

import java.util.Collections;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.moshefarkas.javacompiler.VarInfo;
import com.moshefarkas.javacompiler.ast.nodes.MethodNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.ArrAccessExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.AssignExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.CallExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.IdentifierExprNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.BlockStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.ControlFlowStmt;
import com.moshefarkas.javacompiler.ast.nodes.statement.LocalVarDecStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.ReturnStmt;
import com.moshefarkas.javacompiler.ast.nodes.statement.WhileStmtNode;
import com.moshefarkas.javacompiler.symboltable.MethodManager;
import com.moshefarkas.javacompiler.symboltable.SymbolTable;

public class IdentifierUsageVisitor extends SemanticAnalysis {
    // responsible for checking if var is defined, and initialized.

    private String currMethod;
    private boolean seenReturnStmt;
    private int insideLoopCount = 0;

    private boolean insideLoop() {
        return insideLoopCount > 0;
    }

    @Override
    public void visitIdentifierExprNode(IdentifierExprNode node) {
        String varName = node.varName;
        SymbolTable methodSymbolTable  = MethodManager.getInstance().getSymbolTable(currMethod);
        if (!methodSymbolTable.hasVar(varName)) {
            error(ErrorType.UNDEFINED_IDENTIFIER, node.lineNum, varName);
        } else if (methodSymbolTable.getVarInfo(varName).hasValue == false) {
            error(
                ErrorType.UNINITIALIZED_VAR, 
                node.lineNum, 
                errorString("Can't use var `%s` as it's not initialized.", varName)
            );
        }
    }

    @Override 
    public void visitArrAccessExprNode(ArrAccessExprNode node) {
        VarInfo varInfo = MethodManager.getInstance()
            .getSymbolTable(currMethod)
            .getVarInfo(node.varName);

        if (varInfo.type.getSort() != Type.ARRAY) {
            error(
                ErrorType.INVALID_ARRAY_ACCESS, 
                node.lineNum, 
                errorString("Can't subscript non array var `%s`.", varInfo.name)
            );
        } else {
            validateArrayAccessLevels(node, varInfo);
        }
    }

    private void validateArrayAccessLevels(ArrAccessExprNode node, VarInfo varAccessInfo) {
        // checks that array access did not try to access a dimension 
        // that does not exist
        int maxDims = varAccessInfo.type.getDimensions();
        int accessLevels = 1;
        ArrAccessExprNode iterator = node;
        while (iterator.identifer instanceof ArrAccessExprNode) {
            iterator = (ArrAccessExprNode)iterator.identifer;
            accessLevels++;
        }
        if (accessLevels > maxDims) {
            error(
                ErrorType.INVALID_ARRAY_ACCESS, 
                node.lineNum, 
                errorString(
                    "Max array access level for var `%s` is `%s` but got `%s`.", 
                    varAccessInfo.name,
                    maxDims, 
                    accessLevels
                )
            );
        }
    }

    @Override
    public void visitAssignExprNode(AssignExprNode node) {
        SymbolTable methodSymbolTable  = MethodManager.getInstance().getSymbolTable(currMethod);
        if (!methodSymbolTable.hasVar(node.identifier.varName)) {
            error(ErrorType.UNDEFINED_IDENTIFIER, node.lineNum, node.identifier.varName);
        } else {
            MethodManager.getInstance()
                .getSymbolTable(currMethod)
                .getVarInfo(node.identifier.varName).hasValue = true;
            visit(node.identifier);
            visit(node.assignmentValue);
        }
    }

    @Override
    public void visitLocalVarDecStmtNode(LocalVarDecStmtNode node) {
        if (node.hasInitializer()) {
            MethodManager.getInstance()
                .getSymbolTable(currMethod)
                .getVarInfo(node.var.name).hasValue = true;
        }
        super.visitLocalVarDecStmtNode(node);
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

    @Override
    public void visitWhileStmtNode(WhileStmtNode node) {
        insideLoopCount++;
        super.visitWhileStmtNode(node);
        insideLoopCount--;
    }
    
    @Override
    public void visitControlFlowStmt(ControlFlowStmt node) {
        if (!insideLoop()) {
            error(
                ErrorType.INVALID_KEYWORD_USAGE, 
                node.lineNum, 
                errorString(
                    "Can't use `%s` statement outside of loop.", 
                    node.isBreak ? "break" : "continue"
                )
            );
        }
    }
}