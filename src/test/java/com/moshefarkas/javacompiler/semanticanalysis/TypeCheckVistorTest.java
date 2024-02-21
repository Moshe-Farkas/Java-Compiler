package com.moshefarkas.javacompiler.semanticanalysis;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.moshefarkas.javacompiler.semanticanalysis.SemanticAnalysis.ErrorType;
import com.moshefarkas.javacompiler.symboltable.MethodManager;
import com.moshefarkas.javacompiler.symboltable.SymbolTable;

public class TypeCheckVistorTest extends BaseSemanticAnalysis {

    private TypeCheckVisitor visitor;

    @Override
    protected void compile(String source) {
        SemanticAnalysis.hadErr = false;
        MethodManager.getInstance().test_reset();
        visitor = new TypeCheckVisitor();
        super.compile(source);
        SymbolTableGenVisitor sv = new SymbolTableGenVisitor();
        sv.visitClassNode(ast);
        IdentifierUsageVisitor idv = new IdentifierUsageVisitor();
        idv.visitClassNode(ast);
        visitor.visitClassNode(ast);
    }

    @Override 
    protected void compileMethod(String method) {
        SemanticAnalysis.hadErr = false;
        MethodManager.getInstance().test_reset();
        visitor = new TypeCheckVisitor();
        super.compileMethod(method);
        SymbolTableGenVisitor sv = new SymbolTableGenVisitor();
        sv.visitClassNode(ast);
        IdentifierUsageVisitor idv = new IdentifierUsageVisitor();
        idv.visitClassNode(ast);
        visitor.visitClassNode(ast);
    }

    @Test
    public void testMismatchedTypes() {
        compile("int b = 5f - 6;");
        assertEquals(ErrorType.MISMATCHED_ASSIGNMENT_TYPE, visitor.test_error);
        
        compile("float b = 5f - 6;");
        assertEquals(null, visitor.test_error);

        compile("int c = '4' + 0;");
        assertEquals(null, visitor.test_error);
    }

    @Test 
    public void testAssignment() {
        compile("int a = 4; a = 6;");
        assertEquals(null, visitor.test_error);

        compile("int a; a = 9;");
        assertEquals(null, visitor.test_error);

        compile("int a = 0; int b; b = 9 + a * a;");
        assertEquals(null, visitor.test_error);

        compile("int a; a = (float)90;");
        assertEquals(ErrorType.MISMATCHED_ASSIGNMENT_TYPE, visitor.test_error);

        compile("float a; a = (int)5f;");
        assertEquals(null, visitor.test_error);
    }

    @Test 
    public void testArrayAssignment() {
        compile("int[] a; a = new int[9];");
        assertEquals(null, visitor.test_error);

        compile("int[] a; a = 9;");
        assertEquals(ErrorType.MISMATCHED_ASSIGNMENT_TYPE, visitor.test_error);

        compile("int[] a; a = null;");
        assertEquals(null, visitor.test_error);

        compile("int[] a = null;");
        assertEquals(null, visitor.test_error);

        compile("int[][] a = new int[0][]; a[0] = null;");
        assertEquals(null, visitor.test_error);

        compile("int[][] a = new int[0][]; a = null;");
        assertEquals(null, visitor.test_error);

        compile("int[][] a = {9, 9, 8};");
        assertEquals(ErrorType.MISMATCHED_ASSIGNMENT_TYPE, visitor.test_error);
    }

    @Test
    public void testCallExpr() {
        compile("emptyMeth();");
        assertEquals(null, visitor.test_error);

        compile("intMeth(8f);");
        assertEquals(ErrorType.MISMATCHED_ARGUMENTS, visitor.test_error);

        compile("floatMeth(8);");
        assertEquals(null, visitor.test_error);

        compile("charMeth(8);");
        assertEquals(ErrorType.MISMATCHED_ARGUMENTS, visitor.test_error);

        compile("intArr1Dim(new int[4]);");
        assertEquals(null, visitor.test_error);

        compile("intArr1Dim(new int[4][]);");
        assertEquals(ErrorType.MISMATCHED_ARGUMENTS, visitor.test_error);

        compile("intArr2Dim(new int[4][]);");
        assertEquals(null, visitor.test_error);

        compile("intArr2Dim(new float[4][]);");
        assertEquals(ErrorType.MISMATCHED_ARGUMENTS, visitor.test_error);

        compile("int[] a = new int[4]; intArr1Dim(a);");
        assertEquals(null, visitor.test_error);
    }

    @Test 
    public void testCallExprAsExpression() {
        StringBuilder method = new StringBuilder();
        method.append("public int met(){}");
        method.append("public int methooo()");
        method.append("{int a = met();}");
        compileMethod(method.toString());
        assertEquals(null, visitor.test_error);

        method = new StringBuilder();
        method.append("public float met(){}");
        method.append("public int methooo()");
        method.append("{int a = met();}");
        compileMethod(method.toString());
        assertEquals(ErrorType.MISMATCHED_ASSIGNMENT_TYPE, visitor.test_error);

        method = new StringBuilder();
        method.append("public float met(){}");
        method.append("public int methooo()");
        method.append("{int a = (int)met();}");
        compileMethod(method.toString());
        assertEquals(null, visitor.test_error);

        method = new StringBuilder();
        method.append("public float met(){}");
        method.append("public int methooo()");
        method.append("{ if (met()) {} }");
        compileMethod(method.toString());
        assertEquals(ErrorType.MISMATCHED_TYPE, visitor.test_error);

        method = new StringBuilder();
        method.append("public void met(){}");
        method.append("public int methooo()");
        method.append("{ int a = met(); }");
        compileMethod(method.toString());
        assertEquals(ErrorType.MISMATCHED_ASSIGNMENT_TYPE, visitor.test_error);
    }

    @Test 
    public void testArrayDeclarations() {
        compile("int[] a = new int[4]; int b = a[0];");
        assertEquals(null, visitor.test_error);

        compile("float a[] = new int[3];");
        assertEquals(ErrorType.MISMATCHED_ASSIGNMENT_TYPE, visitor.test_error);

        compile("int[][] a = new int[3][1];");
        assertEquals(null, visitor.test_error);

        compile("int[][] a = new int[3][];");
        assertEquals(null, visitor.test_error);

        compile("int[][][] a = new int[3][][];");
        assertEquals(null, visitor.test_error);

        compile("int[] a; a = new int[5];");
        assertEquals(null, visitor.test_error);

        compile("int[] a; a = new int[4][5];");
        assertEquals(ErrorType.MISMATCHED_ASSIGNMENT_TYPE, visitor.test_error);

        compile("int[][] a; a = new int[4];");
        assertEquals(ErrorType.MISMATCHED_ASSIGNMENT_TYPE, visitor.test_error);

        compile("int[][] a; a = new int[4][9];");
        assertEquals(null, visitor.test_error);

        compile("int[][] a; a = new int[4][];");
        assertEquals(null, visitor.test_error);

        compile("int[][][] a = new int[4][][]; int[][] b = a[0];");
        assertEquals(null, visitor.test_error);

        compile("int[][][] a = new int[4][][]; int[][] b = a;");
        assertEquals(ErrorType.MISMATCHED_ASSIGNMENT_TYPE, visitor.test_error);

        // compileSource("int[][][] a = new int[4][][]; int[][] b = a[0][0];");
        // assertEquals(ErrorType.MISMATCHED_ASSIGNMENT_TYPE, visitor.test_error);

        compile("int[] a = new int['l'];");
        assertEquals(ErrorType.INVALID_ARRAY_INIT, visitor.test_error);

        compile("int[][] a = new int['p'][5f];");
        assertEquals(ErrorType.INVALID_ARRAY_INIT, visitor.test_error);

        compile("int[][] a = new int[(int)5f][5];");
        assertEquals(null, visitor.test_error);
    }

    @Test 
    public void testBinaryBoolExpr() {
        compile("if (true == false) {}");
        assertEquals(null, visitor.test_error);

        compile("if (5 == false) {}");
        assertEquals(ErrorType.INVALID_OPERATOR_TYPES, visitor.test_error);

        compile("if (true >= false) {}");
        assertEquals(ErrorType.INVALID_OPERATOR_TYPES, visitor.test_error);

        compile("if (true + false == false) {}");
        assertEquals(ErrorType.INVALID_OPERATOR_TYPES, visitor.test_error);

        compile("if (true == false == false) {}");
        assertEquals(null, visitor.test_error);

        compile("if (true == 5) {}");
        assertEquals(ErrorType.INVALID_OPERATOR_TYPES, visitor.test_error);
    }

    @Test 
    public void testRetType() {
        compileMethod("public static int mmmm() {return 3;}");
        assertEquals(null, visitor.test_error);

        compileMethod("public static int mmmm() {return 3f;}");
        assertEquals(ErrorType.MISMATCHED_TYPE, visitor.test_error);

        compileMethod("public static int mmmm() {return (int)3f;}");
        assertEquals(null, visitor.test_error);

        compileMethod("public static float mmmm() {return 3;}");
        assertEquals(null, visitor.test_error);
    }

    @Test 
    public void testWhileCondition() { 
        compile("while (true) {}");
        assertEquals(null, visitor.test_error);

        compile("while (8) {}");
        assertEquals(ErrorType.MISMATCHED_TYPE, visitor.test_error);

        compile("while (8 == 9) {}");
        assertEquals(null, visitor.test_error);
    }

    @Test 
    public void testArrayAccess() {
        compile("int[] a = new int[9]; a[true] = 45;");
        assertEquals(ErrorType.MISMATCHED_TYPE, visitor.test_error);

        compile("int[][] a = new int[9][9]; a[true][false] = 45;");
        assertEquals(ErrorType.MISMATCHED_TYPE, visitor.test_error);

        compile("int[][] a = new int[9][9]; a[8][false] = 45;");
        assertEquals(ErrorType.MISMATCHED_TYPE, visitor.test_error);

        compile("int[][] a = new int[9][9]; a[false][9] = 45;");
        assertEquals(ErrorType.MISMATCHED_TYPE, visitor.test_error);

        compile("int[][] a = new int[9][9]; a[9][9] = 45;");
        assertEquals(null, visitor.test_error);

        compile("int[][] a = new int[9][9]; int b = 0; a[9][b] = 45;");
        assertEquals(null, visitor.test_error);

        compile("int[][] a = new int[9][9]; boolean b = false; a[9][b] = 45;");
        assertEquals(ErrorType.MISMATCHED_TYPE, visitor.test_error);
    }

    @Test 
    public void testArrayLiteral() { 
        // compile("int[][] a = {{}};");
        // assertEquals(null, visitor.test_error);

        // compile("int[][] a = {{9, 9}, { }};");
        // assertEquals(null, visitor.test_error);

        // compile("int[][][] a = { {{}}, {{}}, };");
        // assertEquals(null, visitor.test_error);

        // compile("int[][][] a = { {{}}, {{}}, {}};");
        // assertEquals(ErrorType.MISMATCHED_TYPE, visitor.test_error);

        // compile("int[][][] a = { {{}}, {{9, 9}, {9, 8}} };");
        // assertEquals(null, visitor.test_error);

        // compile("float[] a = {9f, 3f, 4f};");
        // assertEquals(null, visitor.test_error);

        // compile("float[][] a = {{4f, 4f}, {4f, 4f}};");
        // assertEquals(null, visitor.test_error);

        // compile("float[][] a = {{{9f}}, {4f, 4f}};");
        // assertEquals(ErrorType.MISMATCHED_TYPE, visitor.test_error);

        // compile("int[][] a = {{{9f}}, {{9f}}};");
        // assertEquals(ErrorType.MISMATCHED_ASSIGNMENT_TYPE, visitor.test_error);

        // compile("int[] a = {{}, {}};");
        // assertEquals(ErrorType.MISMATCHED_TYPE, visitor.test_error);

        // compile("int[][] a = {{}, {}, {true}};");
        // assertEquals(ErrorType.MISMATCHED_ASSIGNMENT_TYPE, visitor.test_error);
    }
}
