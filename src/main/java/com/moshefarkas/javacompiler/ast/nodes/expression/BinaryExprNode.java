package com.moshefarkas.javacompiler.ast.nodes.expression;

import org.objectweb.asm.Type;

import com.moshefarkas.javacompiler.ast.AstVisitor;

public class BinaryExprNode extends ExpressionNode {
    
    public enum BinOp {
        PLUS,
        MINUS,
        DIV,
        MUL,
        MOD,
        EQ_EQ,
        NOT_EQ,
        GT,
        GT_EQ,
        LT,
        LT_EQ,
    }

    public BinOp op;
    public ExpressionNode right;
    public ExpressionNode left;
    public Type domType; // if a side needs to be casted to a type
    
    public void setDomType(Type domType) {
        this.domType = domType;
    }
    
    public void setOp(BinOp op) {
        this.op = op;
    }

    public void setRight(ExpressionNode right) {
        this.right = right;
        addChild(right);
    }

    public void setLeft(ExpressionNode left) {
        this.left = left;
        addChild(left);
    }
    
    @Override
    public void accept(AstVisitor v) {
        v.visitBinaryExprNode(this);
    }

    @Override
    public String toString() {
        return "type: " + exprType + ", " + "op: " + op + " left: " + left + " right: " + right;
    }
}
