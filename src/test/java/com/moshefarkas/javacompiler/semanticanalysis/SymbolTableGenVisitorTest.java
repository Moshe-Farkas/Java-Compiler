package com.moshefarkas.javacompiler.semanticanalysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.moshefarkas.javacompiler.semanticanalysis.SemanticAnalysis.ErrorType;
import com.moshefarkas.javacompiler.symboltable.ClassManager;
import com.moshefarkas.javacompiler.symboltable.Method;

public class SymbolTableGenVisitorTest extends BaseSemanticAnalysis {
    private SymbolTableGenVisitor visitor;
    @Override
    protected void compile(String source) {
        ClassManager.getIntsance().test_reset();
        super.compile(source);
        visitor = new SymbolTableGenVisitor("Demo");
        visitor.visit(ast);
    }

    @Override
    protected void compileMethodDecl(String header)  {
        ClassManager.getIntsance().test_reset();
        super.compileMethodDecl(header);
        visitor = new SymbolTableGenVisitor("Demo");
        visitor.visit(ast);
    }

    @Override
    protected void compileMethod(String method)  {
        ClassManager.getIntsance().test_reset();
        super.compileMethod(method);
        visitor = new SymbolTableGenVisitor("Demo");
        visitor.visit(ast);
    }

    @Test 
    public void testDuplicateVar() {
        compile("int a; int a;");
        assertEquals(ErrorType.DUPLICATE_VAR, visitor.test_error);

        compileMethodDecl("void sd(int a, int a)");
        assertEquals(ErrorType.DUPLICATE_VAR, visitor.test_error);

        compileMethodDecl("void sd(int a, float a)");
        assertEquals(ErrorType.DUPLICATE_VAR, visitor.test_error);

        compile("int a; if (true) {int b;} int b;");
        assertEquals(null, visitor.test_error);

        compile("int a; if (true) {int a;}");
        assertEquals(ErrorType.DUPLICATE_VAR, visitor.test_error);
    }

    @Test 
    public void testLocalVarIndeces() {
        // need to compile 
        StringBuilder method = new StringBuilder();
        method.append("public static void mm(int a)");
        method.append("{int b; int c;}");
        compileMethod(method.toString());
        Method m = ClassManager.getIntsance().getClass("Demo").methodManager.getMethod("mm");
        m.symbolTable.resetScopes();
        m.symbolTable.enterScope();

        assertTrue(m.symbolTable.getVarInfo("a").localIndex == 0);
        assertTrue(m.symbolTable.getVarInfo("b").localIndex == 1);
        assertTrue(m.symbolTable.getVarInfo("c").localIndex == 2);

        // test with different scopes 
        method = new StringBuilder();
        method.append("public static void mm(int a)");
        method.append("{int b; if (true){int m;} int p;}");
        compileMethod(method.toString());
        m = ClassManager.getIntsance().getClass("Demo").methodManager.getMethod("mm");
        m.symbolTable.resetScopes();
        m.symbolTable.enterScope();

        assertTrue(m.symbolTable.getVarInfo("a").localIndex == 0);
        assertTrue(m.symbolTable.getVarInfo("b").localIndex == 1);
        m.symbolTable.enterScope();
        assertTrue(m.symbolTable.getVarInfo("m").localIndex == 2);
        m.symbolTable.exitScope();
        assertTrue(m.symbolTable.getVarInfo("p").localIndex == 2);
    }
}
