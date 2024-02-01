package com.moshefarkas.javacompiler.semanticanalysis;

import java.util.Stack;

import com.moshefarkas.javacompiler.Value.Type;
import com.moshefarkas.javacompiler.ast.BaseAstVisitor;

public class SemanticAnalysis extends BaseAstVisitor {

    public enum ErrorType {
        MISMATCHED_TYPE,
        MISMATCHED_ASSIGNMENT_TYPE,
        UNDEFINED_VAR,
        DUPLICATE_VAR,
        UNINITIALIZED_VAR,
    } 

    public ErrorType test_error;

    protected Stack<Type> typeStack = new Stack<>();

    protected void error(ErrorType errType, String errMsg) {
        System.err.println("\u001B[31m" + errType + ": " + errMsg + "\u001B[0m");
        test_error = errType;
    }

    protected void checkTypes(Type a, Type b) {
        if (a != b) {
            error(ErrorType.MISMATCHED_TYPE, String.format("cannot operate type `%s` with type `%s`.", a, b));
        } 
    }
}
