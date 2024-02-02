package com.moshefarkas.javacompiler.semanticanalysis;

import com.moshefarkas.javacompiler.SymbolTable;
import com.moshefarkas.javacompiler.ast.nodes.expression.IdentifierExprNode;

public class IdentifierUsageVisitor extends SemanticAnalysis {

    // responsible for checking if var is defined, and initialized.

    @Override
    public void visitIdentifierExprNode(IdentifierExprNode node) {
        String varName = node.varName;
        if (!SymbolTable.getInstance().hasVar(varName)) {
            error(ErrorType.UNDEFINED_VAR, node.lineNum, varName);
        } else if (!SymbolTable.getInstance().getInfo(varName).initialized) {
            error(ErrorType.UNINITIALIZED_VAR, node.lineNum, varName);
        }
    }
}
