package com.moshefarkas.javacompiler.semanticanalysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import com.moshefarkas.javacompiler.semanticanalysis.SemanticAnalysis.ErrorType;
import com.moshefarkas.javacompiler.symboltable.ClassManager;
import com.moshefarkas.javacompiler.symboltable.Clazz;

public class IdentifierUsageVisitorTest extends BaseSemanticAnalysis {

    private IdentifierUsageVisitor visitor;

    @Override 
    protected void compileInstructions(String source) {
        super.compileInstructions(source);
        Clazz c = SymbolTableGenVisitor.createSymbolTable(ast);
        ClassManager.getIntsance().addNewClass(c.className, c);
        
        visitor = new IdentifierUsageVisitor(c.className);
        visitor.visitClassNode(ast);
    }

    @Override 
    protected void compileMethod(String method) {
        super.compileMethod(method);
        Clazz c = SymbolTableGenVisitor.createSymbolTable(ast);
        ClassManager.getIntsance().addNewClass(c.className, c);

        visitor = new IdentifierUsageVisitor(c.className);
        visitor.visitClassNode(ast);
    }

    @Override 
    public void compileMethodDecl(String source) {
        super.compileMethodDecl(source);
        Clazz c = SymbolTableGenVisitor.createSymbolTable(ast);
        ClassManager.getIntsance().addNewClass(c.className, c);

        visitor = new IdentifierUsageVisitor(c.className);
        visitor.visitClassNode(ast);
    }



    @Test
    public void testUndefinedVar() {
        compileInstructions("int a = b;");
        assertEquals(ErrorType.UNDEFINED_IDENTIFIER, visitor.test_error);

        // compileInstructions("int a = a;");
        // assertEquals(ErrorType.UNDEFINED_IDENTIFIER, visitor.test_error);

        compileInstructions("t = 90;");
        assertEquals(ErrorType.UNDEFINED_IDENTIFIER, visitor.test_error);

        StringBuilder method = new StringBuilder();
        method.append("if (true) {int g = 4;} else {g = 0;}");
        compileInstructions(method.toString());
        assertEquals(ErrorType.UNDEFINED_IDENTIFIER, visitor.test_error);

        compileInstructions("int a = 0; if (true) {int b = a;} else {a = 4;}");
        assertEquals(null, visitor.test_error);

        compileInstructions("if (true) {int b = 8;} else if (b == 9) { }");
        assertEquals(ErrorType.UNDEFINED_IDENTIFIER, visitor.test_error);
    }

    @Test
    public void testUninitializedVar() {
        compileInstructions("int a; int b = a;");
        assertEquals(ErrorType.UNINITIALIZED_VAR, visitor.test_error);

        compileInstructions("int a = 4.9f; int b = a;");
        assertNotEquals(ErrorType.UNINITIALIZED_VAR, visitor.test_error);

        compileInstructions("int b; int a; a = a + b;");
        assertEquals(ErrorType.UNINITIALIZED_VAR, visitor.test_error);

        compileInstructions("int[] a; int[] b = a;");
        assertEquals(ErrorType.UNINITIALIZED_VAR, visitor.test_error);
        
        compileInstructions("int a; a = 4; int b = a;");
        assertEquals(null, visitor.test_error);

        compileMethod("public static void mmm(int a) {int b = a;}");
        assertEquals(null, visitor.test_error);

        compileMethod("public static void mmm(int a, int b) {a = b;}");
        assertEquals(null, visitor.test_error);
    }

    @Test
    public void testMethodModifiers() {
        String endMethodHeader = " void mm()";
        compileMethodDecl("public private static" + endMethodHeader);
        assertEquals(ErrorType.INVALID_METHOD_HEADER, visitor.test_error);

        compileMethodDecl("public public" + endMethodHeader);
        assertEquals(ErrorType.INVALID_METHOD_HEADER, visitor.test_error);

        compileMethodDecl("public static" + endMethodHeader);
        assertEquals(null, visitor.test_error);

        compileMethodDecl("public abstract" + endMethodHeader);
        assertEquals(null, visitor.test_error);

        compileMethodDecl("public static abstract" + endMethodHeader);
        assertEquals(ErrorType.INVALID_METHOD_HEADER, visitor.test_error);
    }

    @Test 
    public void testUndefinedMethod() {
        compileInstructions("k();");
        assertEquals(ErrorType.UNDEFINED_IDENTIFIER, visitor.test_error);
    }

    @Test
    public void testMissingRetStmt() {
        compileMethod("public static int tm(){}");
        assertEquals(ErrorType.MISSING_RET_STMT, visitor.test_error);

        compileMethod("public static int tm(){return 3;}");
        assertEquals(null, visitor.test_error);
    }

    @Test 
    public void testBreakAndContinue() {
        compileInstructions("while (true) {break;}");
        assertEquals(null, visitor.test_error);

        compileInstructions("while (true) {continue;} ");
        assertEquals(null, visitor.test_error);

        compileInstructions("while (true) {continue;} continue;");
        assertEquals(ErrorType.INVALID_KEYWORD_USAGE, visitor.test_error);

        compileInstructions("while (true) {continue;} break;");
        assertEquals(ErrorType.INVALID_KEYWORD_USAGE, visitor.test_error);

        compileInstructions("while (true) { while (false) {} continue;}");
        assertEquals(null, visitor.test_error);

        compileInstructions("while (true) { while (false) {} } break;");
        assertEquals(ErrorType.INVALID_KEYWORD_USAGE, visitor.test_error);
    }

    @Test 
    public void testArrayAccess() {
        compileInstructions("int a = 0; a[9] = 0;");
        assertEquals(ErrorType.INVALID_ARRAY_ACCESS, visitor.test_error);
        
        compileInstructions("int[] a = new int[9]; a[9][9] = 0;");
        assertEquals(ErrorType.INVALID_ARRAY_ACCESS, visitor.test_error);

        compileInstructions("int[] a = new int[9]; a[9][9][9] = 0;");
        assertEquals(ErrorType.INVALID_ARRAY_ACCESS, visitor.test_error);

        compileInstructions("int[] a = new int[9]; a[9] = 0;");
        assertEquals(null, visitor.test_error);

        compileInstructions("int a = 9; int[] b = a[0];");
        assertEquals(ErrorType.INVALID_ARRAY_ACCESS, visitor.test_error);

        compileInstructions("int[] a = new int[8]; int[] b = a[89][9];");
        assertEquals(ErrorType.INVALID_ARRAY_ACCESS, visitor.test_error);

        compileInstructions("int[] a = new int[8]; int[] b = a[89];");
        assertEquals(null, visitor.test_error);
    }
}
