package com.moshefarkas.javacompiler.semanticanalysis;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.moshefarkas.javacompiler.BaseMock;
import com.moshefarkas.javacompiler.SymbolTable;
import com.moshefarkas.javacompiler.semanticanalysis.SemanticAnalysis.ErrorType;

public class SymbolTableGenVisitorTest extends BaseMock {
    private SymbolTableGenVisitor visitor;
    private void compileNewSource(String source) {
        SymbolTable.getInstance().test_reset();
        visitor = new SymbolTableGenVisitor();
        compile(source);
        visitor.visitClassNode(ast);
    }

    @Test 
    public void testDuplicateVar() {
        compileNewSource("int a; int a;");
        assertEquals(ErrorType.DUPLICATE_VAR, visitor.test_error);
    }
}
