package com.moshefarkas.javacompiler.ast.nodes.statement;

import org.objectweb.asm.Type;

import com.moshefarkas.javacompiler.ast.AstVisitor;
import com.moshefarkas.javacompiler.ast.nodes.IVarDecl;
import com.moshefarkas.javacompiler.ast.nodes.expression.ExpressionNode;

public class LocalVarDecStmtNode extends StatementNode implements IVarDecl {
    
    public ExpressionNode initializer; 
    public Type varType;
    public String varName;
    public boolean hasValue = false;
    public int localIndex = -1;

    public boolean hasInitializer() {
        return initializer != null;
    }

    @Override
    public void setInitializerNode(ExpressionNode initializer) {
        this.initializer = initializer;
        addChild(initializer);
    }

    @Override
    public ExpressionNode getInitializerNode() {
        return initializer;
    }

    @Override
    public void accept(AstVisitor v) {
        v.visitLocalVarDecStmtNode(this);
    }

    @Override 
    public String toString() {
        return "name: " 
            + varName 
            + ", \ninitializer: " 
            + initializer 
            + ", \ntype: " 
            + varType 
            + ", \nlocalindex: " 
            + localIndex 
            + ", \nhasvalue: "
            + hasValue;
    }

    @Override
    public String getName() {
        return varName;
    }

    @Override
    public Type getType() {
        return varType;
    }

    @Override
    public void setType(Type type) {
        this.varType = type;
    }

    @Override
    public boolean hasValue() {
        return hasValue;
    }

    @Override
    public void setName(String name) {
        varName = name;
    }

    @Override
    public void setHasValue(boolean hasValue) {
        this.hasValue = hasValue;
    }
}
