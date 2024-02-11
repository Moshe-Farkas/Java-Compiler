package com.moshefarkas.javacompiler.semanticanalysis;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.objectweb.asm.Type;

import com.moshefarkas.javacompiler.SymbolTable;
import com.moshefarkas.javacompiler.ast.BaseAstVisitor;
import com.moshefarkas.javacompiler.ast.nodes.expression.AssignExprNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.LocalVarDecStmtNode;

public class TypeEvalVisitorTest extends BaseSemanticAnalysis {
    
    private Type type;

    private void compileNewSource(String source) {
        SymbolTable.getInstance().test_reset();
        final TypeEvalVisitor visitor = new TypeEvalVisitor();
        compile(source);
        SymbolTableGenVisitor sv = new SymbolTableGenVisitor();
        sv.visitClassNode(ast);
        IdentifierUsageVisitor idv = new IdentifierUsageVisitor();
        idv.visitClassNode(ast);
        new BaseAstVisitor() {

            @Override
            public void visitAssignExprNode(AssignExprNode node) {
                type = visitor.evalType(node.assignmentValue);
                super.visitAssignExprNode(node);
            }

            @Override
            public void visitLocalVarDecStmtNode(LocalVarDecStmtNode node) {
                type = visitor.evalType(node.initializer);
                super.visitLocalVarDecStmtNode(node);
            }
            
        }.visit(ast);
    }

    @Test 
    public void basicTypesTest() {
        compileNewSource("int a = 5 + 0;");
        assertEquals(Type.INT_TYPE, type);

        compileNewSource("float b = 4 + 45;");
        assertEquals(Type.INT_TYPE, type);

        compileNewSource("float b = 9 + 5 - 6f;");
        assertEquals(Type.FLOAT_TYPE, type);
        
        compileNewSource("char b = '5';");
        assertEquals(Type.CHAR_TYPE, type);

        compileNewSource("byte b = '5';");
        assertEquals(Type.CHAR_TYPE, type);
    }
}

