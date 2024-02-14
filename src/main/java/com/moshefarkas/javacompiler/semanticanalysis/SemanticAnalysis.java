package com.moshefarkas.javacompiler.semanticanalysis;

import org.objectweb.asm.Type;

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
        INVALID_METHOD_HEADER,
        MISMATCHED_ARGUMENTS,
        MISMATCHED_TYPE,
        MISMATCHED_ASSIGNMENT_TYPE,
        UNDEFINED_VAR,
        DUPLICATE_VAR,
        DUPLICATE_METHOD,
        UNINITIALIZED_VAR,
        INVALID_CAST,
        INVALID_ARRAY_INIT,
    } 

    protected ErrorType test_error;
    public static boolean hadErr = false;

    protected void error(ErrorType errType, int lineNum, String errMsg) {
        System.err.println("\u001B[31m" + errType + " on line " + lineNum + ": " + errMsg + "\u001B[0m");
        test_error = errType;
        hadErr = true;
    }

    protected String errorString(String format, Object... args) {
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg instanceof Type) {
                args[i] = humanReadableType((Type)arg);
            }
        }
        return String.format(format, args);
    }

    private String humanReadableType(Type type) {
        String res = "inside human readable type";
        String dims = "";
        if (type.getSort() == Type.ARRAY) {
            for (int i = type.getDimensions(); i > 0; i--)
                dims += "[]";
            type = type.getElementType();
        }

        if (type.equals(Type.INT_TYPE)) {
            res = "int";
        } else if (type.equals(Type.FLOAT_TYPE)) {
            res = "float";
        } else if (type.equals(Type.BOOLEAN_TYPE)) {
            res = "boolean";
        } else if (type.equals(Type.CHAR_TYPE)) {
            res = "char";
        } else if (type.equals(Type.BYTE_TYPE)) {
            res = "byte";
        } else if (type.equals(Type.SHORT_TYPE)) {
            res = "short";
        } else if (type.equals(Type.VOID_TYPE)) {
            res = "void";
        }
        return res + dims;
    }
}
