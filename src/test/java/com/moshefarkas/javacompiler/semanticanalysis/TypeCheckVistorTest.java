package com.moshefarkas.javacompiler.semanticanalysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import com.moshefarkas.javacompiler.BaseMock;
import com.moshefarkas.javacompiler.SymbolTable;

public class TypeCheckVistorTest extends BaseMock {
    // needs to first call table gen visitor
    // then iden usage visitor 
    // then type check visitor

    private TypeCheckVisitor visitor;
    private void compileNewSource(String source) {
        SymbolTable.getInstance().test_reset();
        visitor = new TypeCheckVisitor();
        compile(source);
        SymbolTableGenVisitor sv = new SymbolTableGenVisitor();
        sv.visitClassNode(ast);
        IdentifierUsageVisitor idv = new IdentifierUsageVisitor();
        idv.visitClassNode(ast);
        visitor.visitClassNode(ast);
    }

    @Test
    public void testMismatchedTypes() {
        // compileNewSource("int b = \"eh\";");
        // assertEquals(ErrorType.MISMATCHED_ASSIGNMENT_TYPE, visitor.test_error);

        // compileNewSource("int b = 5f - 6;");
        // assertEquals(ErrorType.MISMATCHED_ASSIGNMENT_TYPE, visitor.test_error);
        
        // compileNewSource("float b = 5f - 6;");
        // assertEquals(null, visitor.test_error);
    }

    @Test 
    public void testAssignment() {
        // compileNewSource("int a = 4; a = 6;");
        // assertEquals(null, visitor.test_error);

        // compileNewSource("a = 9;");
        // assertEquals(ErrorType.UNDEFINED_VAR, visitor.test_error);

        // compileNewSource("int a; a = 9;");
        // assertEquals(null, visitor.test_error);

        // compileNewSource("int b; int a; a = a + b;");
        // assertEquals(ErrorType.UNINITIALIZED_VAR, visitor.test_error);

        // compileNewSource("int a = 0; int b; b = 9 + a * a;");
        // assertEquals(null, visitor.test_error);
    }
}
