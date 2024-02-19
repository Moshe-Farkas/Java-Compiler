package com.moshefarkas.javacompiler.semanticanalysis;

import com.moshefarkas.javacompiler.MethodInfo;
import com.moshefarkas.javacompiler.ast.nodes.MethodNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.BlockStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.LocalVarDecStmtNode;
import com.moshefarkas.javacompiler.symboltable.MethodManager;

public class SymbolTableGenVisitor extends SemanticAnalysis {

    private String currMethod;

    @Override
    public void visitLocalVarDecStmtNode(LocalVarDecStmtNode node) {
        if (MethodManager.getInstance().getSymbolTable(currMethod).hasVar(node.var.name)) {
            error(ErrorType.DUPLICATE_VAR, node.lineNum, node.var.name);
        } else {
            MethodManager.getInstance().getSymbolTable(currMethod).addLocal(node.var);
        }
    }

    @Override
    public void visitMethodNode(MethodNode node) {
        // need to add local vars to this methods
        if (MethodManager.getInstance().hasMethod(node.methodName)) {
            error(
                ErrorType.DUPLICATE_METHOD, 
                node.lineNum, 
                node.methodName
            );
            return;
        } else {
            MethodInfo methodInfo = new MethodInfo();
            methodInfo.methodName = node.methodName;
            methodInfo.parameters = node.params;
            methodInfo.returnType = node.returnType;
            MethodManager.getInstance().createNewMethod(node.methodName, methodInfo);
        }
        currMethod = node.methodName;
        super.visitMethodNode(node);
    }

    @Override
    public void visitBlockStmtNode(BlockStmtNode node) {
        MethodManager.getInstance().getSymbolTable(currMethod).createNewScope();
        super.visitBlockStmtNode(node);
        MethodManager.getInstance().getSymbolTable(currMethod).exitScope();
    }
}
