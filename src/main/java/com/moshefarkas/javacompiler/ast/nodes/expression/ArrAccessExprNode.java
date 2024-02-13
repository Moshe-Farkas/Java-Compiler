package com.moshefarkas.javacompiler.ast.nodes.expression;

import com.moshefarkas.javacompiler.ast.AstVisitor;

public class ArrAccessExprNode extends IdentifierExprNode {
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
            return "ArrAcc -: iden: " + identifer + " :: index: " + index + " <><> " + super.toString();
        }
}
