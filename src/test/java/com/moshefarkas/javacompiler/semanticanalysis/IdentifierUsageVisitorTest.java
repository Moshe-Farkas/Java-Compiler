package com.moshefarkas.javacompiler.semanticanalysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import com.moshefarkas.javacompiler.semanticanalysis.SemanticAnalysis.ErrorType;
import com.moshefarkas.javacompiler.symboltable.MethodManager;
import com.moshefarkas.javacompiler.symboltable.SymbolTable;

public class IdentifierUsageVisitorTest extends BaseSemanticAnalysis {

    private IdentifierUsageVisitor visitor;

    private void compileNewSource(String source) {
        MethodManager.getInstance().test_reset();
        visitor = new IdentifierUsageVisitor();
        compile(source);
        SymbolTableGenVisitor v = new SymbolTableGenVisitor();
        v.visitClassNode(ast);
        visitor.visitClassNode(ast);
    }

    private void compileNewMethodheader(String header) {
        MethodManager.getInstance().test_reset();
        visitor = new IdentifierUsageVisitor();
        compileMethodModifiers(header);
        SymbolTableGenVisitor v = new SymbolTableGenVisitor();
        v.visitClassNode(ast);
        visitor.visitClassNode(ast);
    }

    @Test
    public void testUndefinedVar() {
        compileNewSource("int a = b;");
        assertEquals(ErrorType.UNDEFINED_IDENTIFIER, visitor.test_error);

        // compileNewSource("int a = a;");
        //sertEquals(ErrorType.UNDEFINED_VAR, visitor.test_error);

        compileNewSource("t = 90;");
        assertEquals(ErrorType.UNDEFINED_IDENTIFIER, visitor.test_error);
    }

    @Test
    public void testUninitializedVar() {
        compileNewSource("int a; int b = a;");
        assertEquals(ErrorType.UNINITIALIZED_VAR, visitor.test_error);

        compileNewSource("int a = 4.9f; int b = a;");
        assertNotEquals(ErrorType.UNINITIALIZED_VAR, visitor.test_error);

        compileNewSource("int b; int a; a = a + b;");
        assertEquals(ErrorType.UNINITIALIZED_VAR, visitor.test_error);

        compileNewSource("int a[]; int b = a[0];");
        assertEquals(ErrorType.UNINITIALIZED_VAR, visitor.test_error);
    }

    @Test
    public void testMethodModifiers() {
        compileNewMethodheader("public private static");
        assertEquals(ErrorType.INVALID_METHOD_HEADER, visitor.test_error);

        compileNewMethodheader("public public");
        assertEquals(ErrorType.INVALID_METHOD_HEADER, visitor.test_error);

        compileNewMethodheader("public static");
        assertEquals(null, visitor.test_error);

        compileNewMethodheader("public abstract");
        assertEquals(null, visitor.test_error);

        compileNewMethodheader("public static abstract");
        assertEquals(ErrorType.INVALID_METHOD_HEADER, visitor.test_error);
    }

    @Test 
    public void testUndefinedMethod() {
        compileNewSource("k();");
        assertEquals(ErrorType.UNDEFINED_IDENTIFIER, visitor.test_error);
    }
}
