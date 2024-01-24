package com.moshefarkas.javacompiler.irgeneration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import com.moshefarkas.javacompiler.SymbolTable;
import com.moshefarkas.javacompiler.irgeneration.IrGeneratorVisitor.ErrorType;

public class IrGeneratorVisitorTest extends BaseMock {

    public IrGeneratorVisitor visitor = new IrGeneratorVisitor();

    private void compileNewSource(String source) {        
        SymbolTable.getInstance().test_reset();
        visitor = new IrGeneratorVisitor();
        compile(source);
        visitor.visit(tree);
    }

    @Test
    public void testMismatchedTypes() {
        compileNewSource("int b = \"eh\";");
        assertEquals(ErrorType.MISMATCHED_ASSIGNMENT_TYPE, visitor.test_error);

        // compileNewSource("int b = 5f - 6;");
        // assertEquals(ErrorType.MISMATCHED_ASSIGNMENT_TYPE, visitor.test_error);
        
        // compileNewSource("float b = 5f - 6;");
        // assertEquals(null, visitor.test_error);
    }

    @Test
    public void testUndefinedVar() {
        compileNewSource("int a = b;");
        assertEquals(ErrorType.UNDEFINED_VAR, visitor.test_error);

        compileNewSource("int a = a;");
        assertEquals(ErrorType.UNDEFINED_VAR, visitor.test_error);

        compileNewSource("t = 90;");
        assertEquals(ErrorType.UNDEFINED_VAR, visitor.test_error);
    }

    @Test
    public void testUninitializedVar() {
        compileNewSource("int a; int b = a;");
        assertEquals(ErrorType.UNINITIALIZED_VAR, visitor.test_error);

        compileNewSource("int a = 56 - '3'; int b = a;");
        assertNotEquals(ErrorType.UNINITIALIZED_VAR, visitor.test_error);
    }

    @Test 
    public void testDuplicateVar() {
        compileNewSource("int a; int a;");
        assertEquals(ErrorType.DUPLICATE_VAR, visitor.test_error);
    }

    @Test 
    public void testAssignment() {
        compileNewSource("int a = 4; a = 6;");
        assertEquals(null, visitor.test_error);

        compileNewSource("a = 9;");
        assertEquals(ErrorType.UNDEFINED_VAR, visitor.test_error);

        compileNewSource("int a; a = 9;");
        assertEquals(null, visitor.test_error);

        compileNewSource("int b; int a; a = a + b;");
        assertEquals(ErrorType.UNINITIALIZED_VAR, visitor.test_error);

        compileNewSource("int a = 0; int b; b = 9 + a * a;");
        assertEquals(null, visitor.test_error);

    }
}
