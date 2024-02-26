package com.moshefarkas.javacompiler.semanticanalysis;

import com.moshefarkas.javacompiler.MethodInfo;
import com.moshefarkas.javacompiler.ast.BaseAstVisitor;
import com.moshefarkas.javacompiler.ast.nodes.FieldNode;
import com.moshefarkas.javacompiler.ast.nodes.MethodNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.BlockStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.LocalVarDecStmtNode;
import com.moshefarkas.javacompiler.semanticanalysis.SemanticAnalysis.ErrorType;
import com.moshefarkas.javacompiler.symboltable.ClassManager;
import com.moshefarkas.javacompiler.symboltable.Clazz;
import com.moshefarkas.javacompiler.symboltable.SymbolTable;

public class SymbolTableGenVisitor extends BaseAstVisitor {
    // responsible for adding local vars to symbol table, 
    // checking if local var alreay exists, duplicate method check,
    // and creating scopes

    public SymbolTableGenVisitor(String className) {
        currentClass = ClassManager.getIntsance().getClass(className);
    }

    private Clazz currentClass;
    private String currMethod;

    protected SymbolTable currentMethodSymbolTable(String currMethod) {
        return currentClass.methodManager.getSymbolTable(currMethod);
    }

    public ErrorType test_error;

    protected void error(ErrorType errType, int lineNum, String errMsg) {
        String className = currentClass.classNode.className;
        System.err.println("\u001B[31m" + className + " - " + errType + " on line " + lineNum + ": " + errMsg + "\u001B[0m");
        test_error = errType;
    }

    @Override
    public void visitLocalVarDecStmtNode(LocalVarDecStmtNode node) {
        if (currentMethodSymbolTable(currMethod).hasVar(node.var.name)) {
            error(ErrorType.DUPLICATE_VAR, node.lineNum, node.var.name);
        } else {
            currentMethodSymbolTable(currMethod).addLocal(node.var);
        }
    }

    @Override
    public void visitMethodNode(MethodNode node) {
        // need to add local vars to this methods
        if (currentClass.methodManager.hasMethod(node.methodName)) {
            error(
                ErrorType.DUPLICATE_METHOD, 
                node.lineNum, 
                node.methodName
            );
            return;
        } else {
            MethodInfo methodInfo = new MethodInfo();
            methodInfo.methodName = node.methodName;
            methodInfo.parameters = node.params;
            methodInfo.returnType = node.returnType;
            methodInfo.methodModifiers = node.methodModifiers;
            currentClass.methodManager.createNewMethod(node.methodName, methodInfo);
        }
        currMethod = node.methodName;
        super.visitMethodNode(node);
    }

    @Override
    public void visitBlockStmtNode(BlockStmtNode node) {
        currentMethodSymbolTable(currMethod).createNewScope();
        super.visitBlockStmtNode(node);
        currentMethodSymbolTable(currMethod).exitScope();
    }

    @Override
    public void visitFieldNode(FieldNode node) {
        // need to valiadate field modifers
        if (currentClass.fields.hasElement(node.fieldInfo.name)) {
            error(ErrorType.DUPLICATE_FIELD, node.lineNum, node.fieldInfo.name);
        } else {
            currentClass.fields.addElement(node.fieldInfo.name, node);
        }
    }
}
