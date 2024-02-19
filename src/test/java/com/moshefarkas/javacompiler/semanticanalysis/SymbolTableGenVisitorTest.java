package com.moshefarkas.javacompiler.semanticanalysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.moshefarkas.javacompiler.semanticanalysis.SemanticAnalysis.ErrorType;
import com.moshefarkas.javacompiler.symboltable.Method;
import com.moshefarkas.javacompiler.symboltable.MethodManager;

public class SymbolTableGenVisitorTest extends BaseSemanticAnalysis {
    private SymbolTableGenVisitor visitor;
    @Override
    protected void compile(String source) {
        MethodManager.getInstance().test_reset();
        visitor = new SymbolTableGenVisitor();
        super.compile(source);
        visitor.visitClassNode(ast);
    }

    @Override
    protected void compileMethodDecl(String header)  {
        MethodManager.getInstance().test_reset();
        visitor = new SymbolTableGenVisitor();
        super.compileMethodDecl(header);
        visitor.visitClassNode(ast);
    }

    @Override
    protected void compileMethod(String method)  {
        MethodManager.getInstance().test_reset();
        visitor = new SymbolTableGenVisitor();
        super.compileMethod(method);
        visitor.visitClassNode(ast);
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
        Method m = MethodManager.getInstance().getMethod("mm");
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
        m = MethodManager.getInstance().getMethod("mm");
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
