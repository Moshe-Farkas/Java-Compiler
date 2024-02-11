package com.moshefarkas.javacompiler.semanticanalysis;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.moshefarkas.javacompiler.SymbolTable;
import com.moshefarkas.javacompiler.semanticanalysis.SemanticAnalysis.ErrorType;

public class TypeCheckVistorTest extends BaseSemanticAnalysis {

    private TypeCheckVisitor visitor;
    private void compileSource(String source) {
        SemanticAnalysis.hadErr = false;
        SymbolTable.getInstance().test_reset();
        visitor = new TypeCheckVisitor();
        compile(source);
        SymbolTableGenVisitor sv = new SymbolTableGenVisitor();
        sv.visitClassNode(ast);
        IdentifierUsageVisitor idv = new IdentifierUsageVisitor();
        idv.visitClassNode(ast);
        // if (idv.test_error != null || sv.test_error != null) {
        //     System.out.println(idv.test_error + "  --  - - - - ");
        //     System.exit(1);
        // }
        visitor.visitClassNode(ast);
    }

    @Test
    public void testMismatchedTypes() {
        compileSource("int b = 5f - 6;");
        assertEquals(ErrorType.MISMATCHED_ASSIGNMENT_TYPE, visitor.test_error);
        
        compileSource("float b = 5f - 6;");
        assertEquals(null, visitor.test_error);

        compileSource("int c = '4' + 0;");
        assertEquals(null, visitor.test_error);
    }

    @Test 
    public void testAssignment() {
        compileSource("int a = 4; a = 6;");
        assertEquals(null, visitor.test_error);

        compileSource("int a; a = 9;");
        assertEquals(null, visitor.test_error);

        compileSource("int a = 0; int b; b = 9 + a * a;");
        assertEquals(null, visitor.test_error);
    }

    @Test
    public void testCallExpr() {
        compileSource("emptyMeth();");
        assertEquals(null, visitor.test_error);

        compileSource("intMeth(8f);");
        assertEquals(ErrorType.MISMATCHED_ARGUMENTS, visitor.test_error);

        compileSource("floatMeth(8);");
        assertEquals(null, visitor.test_error);

        compileSource("charMeth(8);");
        assertEquals(ErrorType.MISMATCHED_ARGUMENTS, visitor.test_error);
    }
}
