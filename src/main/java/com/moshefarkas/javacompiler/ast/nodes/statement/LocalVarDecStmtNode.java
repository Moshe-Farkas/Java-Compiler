package com.moshefarkas.javacompiler.ast.nodes.statement;

import com.moshefarkas.javacompiler.VarInfo;
import com.moshefarkas.javacompiler.ast.nodes.expression.ExpressionNode;

public class LocalVarDecStmtNode extends StatementNode {
    
    public ExpressionNode initializer; 
    public VarInfo var;
}
