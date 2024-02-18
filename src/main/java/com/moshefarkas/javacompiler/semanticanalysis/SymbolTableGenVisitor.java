package com.moshefarkas.javacompiler.semanticanalysis;

import com.moshefarkas.javacompiler.MethodInfo;
import com.moshefarkas.javacompiler.ast.nodes.MethodNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.BlockStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.LocalVarDecStmtNode;
import com.moshefarkas.javacompiler.symboltable.SymbolTable;

public class SymbolTableGenVisitor extends SemanticAnalysis {

    @Override
    public void visitLocalVarDecStmtNode(LocalVarDecStmtNode node) {
        if (SymbolTable.getInstance().hasVar(node.var.name)) {
            error(ErrorType.DUPLICATE_VAR, node.lineNum, node.var.name);
        } else {
            SymbolTable.getInstance().addLocal(node.var.name, node.var);
        }
    }

    @Override
    public void visitMethodNode(MethodNode node) {
        super.visitMethodNode(node);
        if (SymbolTable.getInstance().hasMethod(node.methodName)) {
            error(ErrorType.DUPLICATE_METHOD, node.lineNum, node.methodName);
        } else {
            MethodInfo methodInfo = new MethodInfo();
            methodInfo.methodName = node.methodName;
            methodInfo.parameters = node.params;
            methodInfo.returnType = node.returnType;
            SymbolTable.getInstance().addMethod(node.methodName, methodInfo);
        }
    }

    @Override
    public void visitBlockStmtNode(BlockStmtNode node) {
        SymbolTable.getInstance().createNewScope();
        super.visitBlockStmtNode(node);
        SymbolTable.getInstance().exitScope();
    }
}
