package com.moshefarkas.javacompiler.semanticanalysis;

import com.moshefarkas.javacompiler.SymbolTable;
import com.moshefarkas.javacompiler.ast.BaseAstVisitor;
import com.moshefarkas.javacompiler.ast.nodes.ClassNode;

public class SemanticAnalysis extends BaseAstVisitor {

    protected SemanticAnalysis() {}
    public SemanticAnalysis(ClassNode ast) {
        test_error = null;
        hadErr = false;
        SymbolTableGenVisitor sv = new SymbolTableGenVisitor();
        sv.visitClassNode(ast);
        if (hadErr) return;
        IdentifierUsageVisitor iuv = new IdentifierUsageVisitor();
        iuv.visitClassNode(ast);
        if (hadErr) return;
        TypeCheckVisitor s = new TypeCheckVisitor();
        s.visitClassNode(ast);
        if (hadErr) return;
    }

    protected enum ErrorType {
        MISMATCHED_ARGUMENTS,
        MISMATCHED_TYPE,
        MISMATCHED_ASSIGNMENT_TYPE,
        UNDEFINED_VAR,
        DUPLICATE_VAR,
        DUPLICATE_METHOD,
        UNINITIALIZED_VAR,
        INVALID_CAST,
    } 

    protected ErrorType test_error;
    public static boolean hadErr = false;

    protected void error(ErrorType errType, int lineNum, String errMsg) {
        System.err.println("\u001B[31m" + errType + " on line " + lineNum + ": " + errMsg + "\u001B[0m");
        test_error = errType;
        hadErr = true;
    }
}
