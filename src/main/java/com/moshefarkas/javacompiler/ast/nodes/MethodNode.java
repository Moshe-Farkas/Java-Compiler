package com.moshefarkas.javacompiler.ast.nodes;

import java.util.List;

import com.moshefarkas.javacompiler.ast.AstVisitor;
import com.moshefarkas.javacompiler.ast.nodes.statement.BlockStmtNode;

public class MethodNode extends AstNode {
    public String returnType;
    public List<String> params;
    public List<String> accessModifiers;
    public BlockStmtNode statements;
    public String methodName;

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }

    public void setAccessModifiers(List<String> accessModifiers) {
        this.accessModifiers = accessModifiers;
    }

    public void setStatements(BlockStmtNode statements) {
        this.statements = statements;
        addChild(statements);
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public String toString() {
        return "ret type: " + returnType + ", " + "name: " + methodName + ", " + accessModifiers + ", statements: " + statements;
    }

    @Override
    public void accept(AstVisitor v) {
        v.visitMethodNode(this); 
    }
}
