package com.moshefarkas.javacompiler.ast.nodes.expression;

import com.moshefarkas.javacompiler.ast.AstVisitor;

public class IdentifierExprNode extends ExpressionNode {

    public static class VarIdenExprNode extends IdentifierExprNode {
        public String varName;

        public void setVarName(String varName) {
            this.varName = varName;
        }

        @Override
        public void accept(AstVisitor v) {
            v.visitVarIdenExprNode(this);
        }

        @Override 
        public String toString() {
            return "name: " + varName + ", type: " + exprType;
        }
    }

    public static class ArrAccessExprNode extends IdentifierExprNode {
        public IdentifierExprNode identifer;
        public ExpressionNode index; // needs to be int
        public void setIdentifer(IdentifierExprNode identifer) {
            this.identifer = identifer;
            addChild(identifer);
        }

        public void setIndex(ExpressionNode index) {
            this.index = index;
            addChild(index);
        }

        @Override
        public void accept(AstVisitor v) {
            v.visitArrAccessExprNode(this);
        }

        @Override
        public String toString() {
            return "iden: " + identifer + "\nindex: " + index;
        }
    }

    // public String varName;

    // @Override 
    // public void accept(AstVisitor v) {
    //     v.visitIdentifierExprNode(this);
    // }

    // @Override 
    // public String toString() {
    //     return "name: " + varName + ", type: " + exprType;
    // }

    // public void setVarName(String varName) {
    //     this.varName = varName;
    // }
}
