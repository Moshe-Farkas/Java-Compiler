package com.moshefarkas.javacompiler.semanticanalysis;

import com.moshefarkas.javacompiler.SymbolTable;
import com.moshefarkas.javacompiler.ast.nodes.statement.LocalVarDecStmtNode;

public class SymbolTableGenVisitor extends SemanticAnalysis {

    // need to check for duplicate vars

    @Override
    public void visitLocalVarDecStmtNode(LocalVarDecStmtNode node) {
        if (SymbolTable.getInstance().hasVar(node.var.name)) {
            error(ErrorType.DUPLICATE_VAR, node.lineNum, node.var.name);
        } else {
            SymbolTable.getInstance().addLocal(node.var.name, node.var);
        }
    }
}
