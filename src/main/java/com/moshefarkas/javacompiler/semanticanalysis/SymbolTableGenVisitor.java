package com.moshefarkas.javacompiler.semanticanalysis;

import org.objectweb.asm.Opcodes;

import com.moshefarkas.javacompiler.ast.BaseAstVisitor;
import com.moshefarkas.javacompiler.ast.nodes.ClassNode;
import com.moshefarkas.javacompiler.ast.nodes.FieldNode;
import com.moshefarkas.javacompiler.ast.nodes.MethodNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.BlockStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.LocalVarDecStmtNode;
import com.moshefarkas.javacompiler.semanticanalysis.SemanticAnalysis.ErrorType;
import com.moshefarkas.javacompiler.symboltable.Clazz;
import com.moshefarkas.javacompiler.symboltable.LocalVarSymbolTable;

public class SymbolTableGenVisitor extends BaseAstVisitor {
    // responsible for adding local vars to symbol table, 
    // checking if local var alreay exists, duplicate method check,
    // and creating scopes

    private SymbolTableGenVisitor() {}

    public static Clazz createSymbolTable(ClassNode ast) {
        hadErr = false;
        test_error = null;
        currentClass = new Clazz(ast);
        new SymbolTableGenVisitor().visit(ast);
        currentClass.className = ast.className;
        return currentClass;
    }
    protected static ErrorType test_error;
    public static boolean hadErr = false;

    private static Clazz currentClass;
    private String currMethod;

    private LocalVarSymbolTable currentMethodSymbolTable(String currMethod) {
        return currentClass.methodManager.getSymbolTable(currMethod);
    }

    protected void error(ErrorType errType, int lineNum, String errMsg) {
        String className = currentClass.classNode.className;
        System.err.println("\u001B[31m" + className + " - " + errType + " on line " + lineNum + ": " + errMsg + "\u001B[0m");
        test_error = errType;
        hadErr = true;
    }

    @Override
    public void visitLocalVarDecStmtNode(LocalVarDecStmtNode node) {
        if (currentMethodSymbolTable(currMethod).hasVar(node.varName)) {
            error(ErrorType.DUPLICATE_VAR, node.lineNum, node.varName);
        } else {
            currentMethodSymbolTable(currMethod).addLocal(node);
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
            currentClass.methodManager.createNewMethod(
                node.methodName, 
                node,
                node.methodModifiers.contains(Opcodes.ACC_STATIC) // need to know wether or not to
            );                                                    // start at 0 or 1 index
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
        if (currentClass.fields.hasElement(node.fieldName)) {
            error(ErrorType.DUPLICATE_FIELD, node.lineNum, node.fieldName);
        } else {
            currentClass.fields.addElement(node.fieldName, node);
        }
    }
}
