package com.moshefarkas.javacompiler.ast.nodes;

import java.util.List;

import org.objectweb.asm.Type;

import com.moshefarkas.javacompiler.ast.AstVisitor;
import com.moshefarkas.javacompiler.ast.nodes.statement.BlockStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.LocalVarDecStmtNode;

public class MethodNode extends AstNode {
    public Type returnType;
    public List<LocalVarDecStmtNode> params;
    public List<Integer> methodModifiers;  // uses OpCode.ACC_XXX for modifers

    public BlockStmtNode statements;

    public String methodName;

    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }

    public void setParams(List<LocalVarDecStmtNode> params) {
        this.params = params;
    }

    public void setMethodModifiers(List<Integer> accessModifiers) {
        this.methodModifiers = accessModifiers;
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
        return "ret type: " + returnType + ", " + "name: " + methodName + ", " + methodModifiers + ", statements: " + statements;
    }

    @Override
    public void accept(AstVisitor v) {
        v.visitMethodNode(this); 
    }
}
