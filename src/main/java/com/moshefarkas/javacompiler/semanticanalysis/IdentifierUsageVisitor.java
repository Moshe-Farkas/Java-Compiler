package com.moshefarkas.javacompiler.semanticanalysis;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import org.antlr.v4.parse.ANTLRParser.modeSpec_return;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.moshefarkas.javacompiler.SymbolTable;
import com.moshefarkas.javacompiler.ast.nodes.MethodNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.CallExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.IdentifierExprNode;

public class IdentifierUsageVisitor extends SemanticAnalysis {

    // responsible for checking if var is defined, and initialized.

    @Override
    public void visitIdentifierExprNode(IdentifierExprNode node) {
        String varName = node.varName;
        if (!SymbolTable.getInstance().hasVar(varName)) {
            error(ErrorType.UNDEFINED_VAR, node.lineNum, varName);
        } else if (SymbolTable.getInstance().getVarInfo(varName).initialized == false) {
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

    @Override
    public void visitMethodNode(MethodNode node) {
        // HashSet<String> seenModifers = new HashSet<>();
        boolean seenAccessMod = false;

        for (int i = 0; i < node.methodModifiers.size(); i++) {
            int modifer = node.methodModifiers.get(i);
            if (Collections.frequency(node.methodModifiers, modifer) > 1) {
                error(
                    ErrorType.INVALID_METHOD_HEADER, 
                    node.lineNum, 
                    "Cannot use a modifer more than once."
                );
                node.methodModifiers.remove((Object)modifer);
            }

            switch (modifer) {
                case Opcodes.ACC_PRIVATE:
                case Opcodes.ACC_PUBLIC:
                case Opcodes.ACC_PROTECTED:
                    if (seenAccessMod) {
                        error(
                            ErrorType.INVALID_METHOD_HEADER, 
                            node.lineNum, 
                            "can't have more than one access modifier."
                        );
                    }
                    seenAccessMod = true;
                    break;
                case Opcodes.ACC_STATIC:
                case Opcodes.ACC_ABSTRACT:
                    int temp = modifer == Opcodes.ACC_STATIC ? Opcodes.ACC_ABSTRACT : Opcodes.ACC_STATIC;
                    if (node.methodModifiers.contains(temp)) {
                        error(
                            ErrorType.INVALID_METHOD_HEADER, 
                            node.lineNum, 
                            "can't have a method be abstract and static."
                        );
                    }
                    break;
            }
            super.visitMethodNode(node);
        }
    }
}

