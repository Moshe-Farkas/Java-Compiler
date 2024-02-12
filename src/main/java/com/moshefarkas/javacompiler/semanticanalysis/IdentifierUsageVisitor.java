package com.moshefarkas.javacompiler.semanticanalysis;

import org.objectweb.asm.Type;

import com.moshefarkas.javacompiler.SymbolTable;
import com.moshefarkas.javacompiler.ast.nodes.expression.CallExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.IdentifierExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.IdentifierExprNode.VarIdenExprNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.LocalVarDecStmtNode;

public class IdentifierUsageVisitor extends SemanticAnalysis {

    // responsible for checking if var is defined, and initialized.

    // @Override
    // public void visitIdentifierExprNode(IdentifierExprNode node) {
    //     String varName = node.varName;
    //     if (!SymbolTable.getInstance().hasVar(varName)) {
    //         error(ErrorType.UNDEFINED_VAR, node.lineNum, varName);
    //     } else if (!SymbolTable.getInstance().getVarInfo(varName).initialized) {
    //         error(ErrorType.UNINITIALIZED_VAR, node.lineNum, varName);
    //     } 
    // }
    
    @Override
    public void visitVarIdenExprNode(VarIdenExprNode node) {
        String varName = node.varName;
        if (!SymbolTable.getInstance().hasVar(varName)) {
            error(ErrorType.UNDEFINED_VAR, node.lineNum, varName);
        } else if (!SymbolTable.getInstance().getVarInfo(varName).initialized) {
            error(ErrorType.UNINITIALIZED_VAR, node.lineNum, varName);
        } 
    }

    @Override
    public void visitCallExprNode(CallExprNode node) {
        Type[] methodParamsTypes = SymbolTable.getInstance().getParamTypes(node.methodName);
        if (methodParamsTypes.length != node.arguments.size()) {
            String reason = String.format("Expected `%d` args but got `%d`.", methodParamsTypes.length, node.arguments.size());
            error(ErrorType.MISMATCHED_ARGUMENTS, node.lineNum, reason);
        } 
        super.visitCallExprNode(node);
    }
}
