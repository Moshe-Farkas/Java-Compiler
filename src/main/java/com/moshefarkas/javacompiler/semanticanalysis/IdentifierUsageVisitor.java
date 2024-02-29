package com.moshefarkas.javacompiler.semanticanalysis;

import java.util.Collections;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.moshefarkas.javacompiler.ast.nodes.FieldNode;
import com.moshefarkas.javacompiler.ast.nodes.IVarDecl;
import com.moshefarkas.javacompiler.ast.nodes.MethodNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.ArrAccessExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.AssignExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.CallExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.IdentifierExprNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.BlockStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.ControlFlowStmt;
import com.moshefarkas.javacompiler.ast.nodes.statement.LocalVarDecStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.ReturnStmt;
import com.moshefarkas.javacompiler.ast.nodes.statement.WhileStmtNode;
import com.moshefarkas.javacompiler.symboltable.LocalVarSymbolTable;

public class IdentifierUsageVisitor extends SemanticAnalysis {
    // responsible for checking if var is defined, and initialized,
    // checking if a var is subscriptable, missing return statement,
    // calling undefined method, calling method with wrong num of args,
    // invalid method header, using break/continue outside a loop
    // and accessing a non static field/method from a non static method

    public IdentifierUsageVisitor(String className) {
        super(className);
    }

    private String currMethod;

    private boolean seenReturnStmt;
    private int insideLoopCount = 0;

    private boolean insideLoop() {
        return insideLoopCount > 0;
    }

    @Override
    public void visitIdentifierExprNode(IdentifierExprNode node) {
        // if local -> check if hasValue
        String varName = node.varName;
        IVarDecl varDecl = null; 

        if (currentClass.hasLocalVar(currMethod, varName)) {
            varDecl = currentMethodSymbolTable(currMethod).getVarDeclNode(varName);
        } else if (currentClass.hasField(varName)) {
            varDecl = currentClass.fields.getElement(varName);
            validateFieldUsage((FieldNode)varDecl);
        } else {
            error(ErrorType.UNDEFINED_IDENTIFIER, node.lineNum, varName);
            return;
        }

        if (varDecl.hasValue() == false) {
            error(
                ErrorType.UNINITIALIZED_VAR, 
                node.lineNum, 
                errorString("Can't use var `%s` as it's not initialized.", varName)
            );
        }
    }
    
    private void validateFieldUsage(FieldNode node) {
        // check if non static field used in static method
        if (!node.isStaticField()) {
            if (currentClass.methodManager.getMethod(currMethod).methodNode.isStaticMethod()) {
                error(
                    ErrorType.INVALID_STATIC_ACCESS, 
                    node.lineNum, 
                    errorString(
                        "Can't access non static field `%s` in non static method", 
                        node.fieldName
                    )
                );
            }
        }
    }

    @Override 
    public void visitArrAccessExprNode(ArrAccessExprNode node) {
        // can be a field decl or var decl node
        IVarDecl varDecl = null;
        if (currentClass.hasField(node.varName)) {
            varDecl = currentClass.fields.getElement(node.varName);
        } else if (currentClass.hasLocalVar(currMethod, node.varName)) {
            varDecl = currentMethodSymbolTable(currMethod).getVarDeclNode(node.varName);
        } else {
            // case of iden trying to subscript doesnt even exist
            error(ErrorType.UNDEFINED_IDENTIFIER, node.lineNum, node.varName);
            return;
        }

        if (varDecl.getType().getSort() != Type.ARRAY) {
            error(
                ErrorType.INVALID_ARRAY_ACCESS, 
                node.lineNum, 
                errorString("Can't subscript non array var `%s`.", varDecl.getName())
            );
        } else {
            validateArrayAccessLevels(node, varDecl);
        }
    }

    private void validateArrayAccessLevels(ArrAccessExprNode node, IVarDecl arrVarDecl) {
        // checks that array access did not try to access a dimension 
        // that does not exist
        int maxDims = arrVarDecl.getType().getDimensions();
        int accessLevels = 1;
        ArrAccessExprNode iterator = node;
        while (iterator.identifer instanceof ArrAccessExprNode) {
            iterator = (ArrAccessExprNode)iterator.identifer;
            accessLevels++;
        }
        if (accessLevels > maxDims) {
            error(
                ErrorType.INVALID_ARRAY_ACCESS, 
                node.lineNum, 
                errorString(
                    "Max array access level for var `%s` is `%s` but got `%s`.", 
                    arrVarDecl.getName(),
                    maxDims, 
                    accessLevels
                )
            );
        }
    }

    @Override
    public void visitAssignExprNode(AssignExprNode node) {
        String varName = node.identifier.varName;
        IVarDecl varDecl;
        if (currentClass.hasLocalVar(currMethod, varName)) {
            varDecl = currentMethodSymbolTable(currMethod).getVarDeclNode(varName);
        } else if (currentClass.hasField(varName)) {
            varDecl = currentClass.fields.getElement(varName);
        } else {
            error(ErrorType.UNDEFINED_IDENTIFIER, node.lineNum, node.identifier.varName);
            return;
        }
        
        varDecl.setHasValue(true);
        visit(node.identifier);
        visit(node.assignmentValue);
    }

    @Override
    public void visitLocalVarDecStmtNode(LocalVarDecStmtNode node) {
        if (node.hasInitializer()) {
            currentMethodSymbolTable(currMethod)
                .getVarDeclNode(node.varName)
                .setHasValue(true);
        }
        super.visitLocalVarDecStmtNode(node);
    }

    @Override
    public void visitCallExprNode(CallExprNode node) {
        if (!currentClass.methodManager.hasMethod(node.methodName)) {
            error(
                ErrorType.UNDEFINED_IDENTIFIER, 
                node.lineNum, 
                errorString("Undefined method `%s`.", node.methodName)
            );
            return;
        }
        Type[] methodParamsTypes = currentClass.methodManager.getParamTypes(node.methodName);
        if (methodParamsTypes.length != node.arguments.size()) {
            String reason = String.format("Expected `%d` args but got `%d`.", methodParamsTypes.length, node.arguments.size());
            error(ErrorType.MISMATCHED_ARGUMENTS, node.lineNum, reason);
        } 
        super.visitCallExprNode(node);
    }

    @Override
    public void visitMethodNode(MethodNode node) {
        currMethod = node.methodName;
        currentMethodSymbolTable(currMethod).resetScopes();
        validateMethodModifiers(node);

        seenReturnStmt = false;
        super.visitMethodNode(node);
        Type currMethodRetType = currentClass.methodManager.getReturnType(currMethod);
        if (currMethodRetType != Type.VOID_TYPE && !seenReturnStmt) {
            error(
                ErrorType.MISSING_RET_STMT, 
                node.lineNum, 
                errorString(
                    "method `%s` needs a return statement of type `%s`.", 
                    currMethod, currMethodRetType
                )
            );
        }
    }

    private void validateMethodModifiers(MethodNode node) {
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
        }
    }

    @Override
    public void visitBlockStmtNode(BlockStmtNode node) {
        LocalVarSymbolTable methodSymbolTable = currentMethodSymbolTable(currMethod);
        methodSymbolTable.enterScope();
        super.visitBlockStmtNode(node);
        methodSymbolTable.exitScope();
    }

    @Override
    public void visitReturnStmt(ReturnStmt node) {
        seenReturnStmt = true;
        super.visitReturnStmt(node);
    }

    @Override
    public void visitWhileStmtNode(WhileStmtNode node) {
        insideLoopCount++;
        super.visitWhileStmtNode(node);
        insideLoopCount--;
    }
    
    @Override
    public void visitControlFlowStmt(ControlFlowStmt node) {
        if (!insideLoop()) {
            error(
                ErrorType.INVALID_KEYWORD_USAGE, 
                node.lineNum, 
                errorString(
                    "Can't use `%s` statement outside of loop.", 
                    node.isBreak ? "break" : "continue"
                )
            );
        }
    }

    @Override
    public void visitFieldNode(FieldNode node) {
        // validateFieldModifiers(node.fieldModifiers);
        if (node.getInitializerNode() != null) {
            node.setHasValue(true);
        }
        super.visitFieldNode(node);
    }

    // private void validateFieldModifiers(List<Integer> modifiers) {

    // }
}