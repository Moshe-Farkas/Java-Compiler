package com.moshefarkas.javacompiler.semanticanalysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.moshefarkas.javacompiler.semanticanalysis.SemanticAnalysis.ErrorType;
import com.moshefarkas.javacompiler.symboltable.Clazz;
import com.moshefarkas.javacompiler.symboltable.Method;

public class SymbolTableGenVisitorTest extends BaseSemanticAnalysis {

    private Clazz clazz;
    
    @Override
    protected void compileInsctructions(String source) {
        super.compileInsctructions(source);
        SymbolTableGenVisitor.createSymbolTable(ast);
    }

    @Override
    protected void compileMethodDecl(String header)  {
        super.compileMethodDecl(header);
        SymbolTableGenVisitor.createSymbolTable(ast);
    }

    @Override
    protected void compileMethod(String method)  {
        super.compileMethod(method);
        clazz = SymbolTableGenVisitor.createSymbolTable(ast);
    }

    @Test 
    public void testDuplicateVar() {
        compileInsctructions("int a; int a;");
        assertEquals(ErrorType.DUPLICATE_VAR, SymbolTableGenVisitor.test_error);

        compileMethodDecl("void sd(int a, int a)");
        assertEquals(ErrorType.DUPLICATE_VAR, SymbolTableGenVisitor.test_error);

        compileMethodDecl("void sd(int a, float a)");
        assertEquals(ErrorType.DUPLICATE_VAR, SymbolTableGenVisitor.test_error);

        compileInsctructions("int a; if (true) {int b;} int b;");
        assertEquals(null, SymbolTableGenVisitor.test_error);

        compileInsctructions("int a; if (true) {int a;}");
        assertEquals(ErrorType.DUPLICATE_VAR, SymbolTableGenVisitor.test_error);
    }

    @Test 
    public void testLocalVarIndeces() {
        // test static methods
        StringBuilder method = new StringBuilder();
        method.append("public static void mm(int a)");
        method.append("{int b; int c;}");
        compileMethod(method.toString());
        Method m = clazz.methodManager.getMethod("mm");

        m.symbolTable.resetScopes();
        m.symbolTable.enterScope();

        assertTrue(m.symbolTable.getVarDeclNode("a").localIndex == 0);
        assertTrue(m.symbolTable.getVarDeclNode("b").localIndex == 1);
        assertTrue(m.symbolTable.getVarDeclNode("c").localIndex == 2);

        // test methods
        method = new StringBuilder();
        method.append("public void mm(int a)");
        method.append("{int b; if (true){int m;} int p;}");
        compileMethod(method.toString());
        m = clazz.methodManager.getMethod("mm");
        m.symbolTable.resetScopes();
        m.symbolTable.enterScope();

        assertTrue(m.symbolTable.getVarDeclNode("a").localIndex == 1);
        assertTrue(m.symbolTable.getVarDeclNode("b").localIndex == 2);
        m.symbolTable.enterScope();
        assertTrue(m.symbolTable.getVarDeclNode("m").localIndex == 3);
        m.symbolTable.exitScope();
        assertTrue(m.symbolTable.getVarDeclNode("p").localIndex == 3);
    }
}
