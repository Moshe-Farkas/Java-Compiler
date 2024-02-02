package com.moshefarkas.javacompiler.semanticanalysis;

import com.moshefarkas.javacompiler.ast.BaseAstVisitor;

public class SemanticAnalysis extends BaseAstVisitor {

    protected enum ErrorType {
        MISMATCHED_TYPE,
        MISMATCHED_ASSIGNMENT_TYPE,
        UNDEFINED_VAR,
        DUPLICATE_VAR,
        UNINITIALIZED_VAR,
    } 

    protected ErrorType test_error;
    protected boolean hadErr = false;

    protected void error(ErrorType errType, int lineNum, String errMsg) {
        System.err.println("\u001B[31m" + errType + " on line " + lineNum + ": " + errMsg + "\u001B[0m");
        test_error = errType;
    }
}
