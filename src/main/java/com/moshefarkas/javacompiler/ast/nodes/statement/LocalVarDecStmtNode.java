package com.moshefarkas.javacompiler.ast.nodes.statement;

import org.objectweb.asm.Type;

import com.moshefarkas.javacompiler.ast.AstVisitor;
import com.moshefarkas.javacompiler.ast.nodes.expression.ExpressionNode;

public class LocalVarDecStmtNode extends StatementNode {
    
    public ExpressionNode initializer; 
    public Type varType;
    public String varName;
    public boolean hasValue = false;
    public int localIndex = -1;

    public boolean hasInitializer() {
        return initializer != null;
    }

    public void setInitializer(ExpressionNode initializer) {
        this.initializer = initializer;
        addChild(initializer);
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }

    public void setType(Type type) {
        this.varType = type;
    }

    @Override
    public void accept(AstVisitor v) {
        v.visitLocalVarDecStmtNode(this);
    }

    @Override 
    public String toString() {
        return "var: " + varName + ", initializer: " + initializer;
    }
}
