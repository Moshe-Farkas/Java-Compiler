package com.moshefarkas.javacompiler;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Type;

import com.moshefarkas.javacompiler.ast.nodes.statement.LocalVarDecStmtNode;

public class MethodInfo {
    public Type returnType;
    public String methodName;
    public List<LocalVarDecStmtNode> parameters = new ArrayList<>();

    @Override
    public String toString() {
        return "methoName: "          + methodName +
               ", returnType: "       + returnType +
               ", parameters:"        + parameters;
    }
}
