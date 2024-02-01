package com.moshefarkas.javacompiler;

import com.moshefarkas.javacompiler.ast.BaseAstVisitor;
import com.moshefarkas.javacompiler.ast.nodes.statement.LocalVarDecStmtNode;

public class SymbolTableGenVisitor extends BaseAstVisitor {

    @Override
    public void visitLocalVarDecStmtNode(LocalVarDecStmtNode node) {
        SymbolTable.getInstance().addLocal(node.var.name, node.var);
    }
}
