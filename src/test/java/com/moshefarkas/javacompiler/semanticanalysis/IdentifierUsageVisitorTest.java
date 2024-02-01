package com.moshefarkas.javacompiler.semanticanalysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import com.moshefarkas.javacompiler.BaseMock;
import com.moshefarkas.javacompiler.SymbolTable;
import com.moshefarkas.javacompiler.semanticanalysis.SemanticAnalysis.ErrorType;

public class IdentifierUsageVisitorTest extends BaseMock {

    private IdentifierUsageVisitor visitor;

    private void compileNewSource(String source) {
        SymbolTable.getInstance().test_reset();
        visitor = new IdentifierUsageVisitor();
        compile(source);
        SymbolTableGenVisitor v = new SymbolTableGenVisitor();
        v.visitClassNode(ast);
        visitor.visitClassNode(ast);
    }

    // need to check undefined error and unintizaled
    @Test
    public void testUndefinedVar() {
        compileNewSource("int a = b;");
        assertEquals(ErrorType.UNDEFINED_VAR, visitor.test_error);

        // compileNewSource("int a = a;");
        // assertEquals(ErrorType.UNDEFINED_VAR, visitor.test_error);

        // compileNewSource("t = 90;");
        // assertEquals(ErrorType.UNDEFINED_VAR, visitor.test_error);
    }

    @Test
    public void testUninitializedVar() {
        compileNewSource("int a; int b = a;");
        assertEquals(ErrorType.UNINITIALIZED_VAR, visitor.test_error);

        compileNewSource("int a = 56 - '3'; int b = a;");
        assertNotEquals(ErrorType.UNINITIALIZED_VAR, visitor.test_error);
    }
}
