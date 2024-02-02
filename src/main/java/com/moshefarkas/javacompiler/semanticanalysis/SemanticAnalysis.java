package com.moshefarkas.javacompiler.semanticanalysis;

import java.util.Stack;

import com.moshefarkas.javacompiler.Value.Type;
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
    protected Stack<Type> typeStack = new Stack<>();

    protected void error(ErrorType errType, int lineNum, String errMsg) {
        System.err.println("\u001B[31m" + errType + " on line " + lineNum + ": " + errMsg + "\u001B[0m");
        test_error = errType;
    }

    protected void checkTypes(Type a, Type b, int lineNum) {
        if (a != b) {
            error(ErrorType.MISMATCHED_TYPE, lineNum, String.format("cannot operate type `%s` with type `%s`.", a, b));
        } 
    }
}
