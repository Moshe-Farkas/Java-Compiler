package com.moshefarkas.javacompiler.ast.nodes.expression;

import java.util.ArrayList;
import java.util.List;

import com.moshefarkas.javacompiler.ast.AstVisitor;

public class CallExprNode extends ExpressionNode {

    // need a identifier which is the method name
    // need a list of arguments
    public String methodName;
    public List<ExpressionNode> arguments = new ArrayList<>();

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setArguments(List<ExpressionNode> arguments) {
        this.arguments = arguments;
        for (ExpressionNode node : arguments) {
            addChild(node);
        }
    }

    @Override
    public void accept(AstVisitor v) {
        v.visitCallExprNode(this);
    }

    @Override 
    public String toString() { 
        return "calle: " + methodName + ", args: " + arguments;
    }
}
