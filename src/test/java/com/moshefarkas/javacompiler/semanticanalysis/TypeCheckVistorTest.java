package com.moshefarkas.javacompiler.semanticanalysis;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.moshefarkas.javacompiler.semanticanalysis.SemanticAnalysis.ErrorType;
import com.moshefarkas.javacompiler.symboltable.SymbolTable;

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

        compileSource("int a; a = (float)90;");
        assertEquals(ErrorType.MISMATCHED_ASSIGNMENT_TYPE, visitor.test_error);

        compileSource("float a; a = (int)5f;");
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

        compileSource("intArr1Dim(new int[4]);");
        assertEquals(null, visitor.test_error);

        compileSource("intArr1Dim(new int[4][]);");
        assertEquals(ErrorType.MISMATCHED_ARGUMENTS, visitor.test_error);

        compileSource("intArr2Dim(new int[4][]);");
        assertEquals(null, visitor.test_error);

        compileSource("intArr2Dim(new float[4][]);");
        assertEquals(ErrorType.MISMATCHED_ARGUMENTS, visitor.test_error);

        compileSource("int[] a = new int[4]; intArr1Dim(a);");
        assertEquals(null, visitor.test_error);
    }

    @Test 
    public void testArrayDeclarations() {
        compileSource("int[] a = new int[4]; int b = a[0];");
        assertEquals(null, visitor.test_error);

        compileSource("float a[] = new int[3];");
        assertEquals(ErrorType.MISMATCHED_ASSIGNMENT_TYPE, visitor.test_error);

        compileSource("int[][] a = new int[3][1];");
        assertEquals(null, visitor.test_error);

        compileSource("int[][] a = new int[3][];");
        assertEquals(null, visitor.test_error);

        compileSource("int[][][] a = new int[3][][];");
        assertEquals(null, visitor.test_error);

        compileSource("int[] a; a = new int[5];");
        assertEquals(null, visitor.test_error);

        compileSource("int[] a; a = new int[4][5];");
        assertEquals(ErrorType.MISMATCHED_ASSIGNMENT_TYPE, visitor.test_error);

        compileSource("int[][] a; a = new int[4];");
        assertEquals(ErrorType.MISMATCHED_ASSIGNMENT_TYPE, visitor.test_error);

        compileSource("int[][] a; a = new int[4][9];");
        assertEquals(null, visitor.test_error);

        compileSource("int[][] a; a = new int[4][];");
        assertEquals(null, visitor.test_error);

        compileSource("int[][][] a = new int[4][][]; int[][] b = a[0];");
        assertEquals(null, visitor.test_error);

        compileSource("int[][][] a = new int[4][][]; int[][] b = a;");
        assertEquals(ErrorType.MISMATCHED_ASSIGNMENT_TYPE, visitor.test_error);

        // compileSource("int[][][] a = new int[4][][]; int[][] b = a[0][0];");
        // assertEquals(ErrorType.MISMATCHED_ASSIGNMENT_TYPE, visitor.test_error);

        compileSource("int[] a = new int['l'];");
        assertEquals(ErrorType.INVALID_ARRAY_INIT, visitor.test_error);

        compileSource("int[][] a = new int['p'][5f];");
        assertEquals(ErrorType.INVALID_ARRAY_INIT, visitor.test_error);

        compileSource("int[][] a = new int[(int)5f][5];");
        assertEquals(null, visitor.test_error);
    }

    @Test 
    public void testBinaryBoolExpr() {
        compileSource("if (true == false) {}");
        assertEquals(null, visitor.test_error);

        compileSource("if (5 == false) {}");
        assertEquals(ErrorType.INVALID_OPERATOR_TYPES, visitor.test_error);

        compileSource("if (true >= false) {}");
        assertEquals(ErrorType.INVALID_OPERATOR_TYPES, visitor.test_error);

        compileSource("if (true + false == false) {}");
        assertEquals(ErrorType.INVALID_OPERATOR_TYPES, visitor.test_error);

        compileSource("if (true == false == false) {}");
        assertEquals(null, visitor.test_error);

        compileSource("if (true == 5) {}");
        assertEquals(ErrorType.INVALID_OPERATOR_TYPES, visitor.test_error);
    }
}
