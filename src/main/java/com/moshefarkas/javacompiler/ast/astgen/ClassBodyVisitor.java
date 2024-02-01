package com.moshefarkas.javacompiler.ast.astgen;

import java.util.ArrayList;
import java.util.List;

import com.moshefarkas.generated.Java8Parser.IntegralTypeContext;
import com.moshefarkas.generated.Java8Parser.MethodDeclarationContext;
import com.moshefarkas.generated.Java8Parser.MethodDeclaratorContext;
import com.moshefarkas.generated.Java8Parser.MethodModifierContext;
import com.moshefarkas.generated.Java8Parser.ResultContext;
import com.moshefarkas.generated.Java8ParserBaseVisitor;
import com.moshefarkas.javacompiler.ast.nodes.MethodNode;

public class ClassBodyVisitor extends Java8ParserBaseVisitor<Void> {

    public List<MethodNode> methods = new ArrayList<>();
    public List<MethodNode> fields = new ArrayList<>();

    private List<String> currentMethodAccessModifiers;
    private String currentMethodRetType;
    private String currentMethodName;

    @Override
    public Void visitMethodDeclaration(MethodDeclarationContext ctx) {
        // methodDeclaration
        //     : methodModifier* methodHeader methodBody
        //     ;
        currentMethodAccessModifiers = new ArrayList<>();
        
        for (MethodModifierContext mmc : ctx.methodModifier()) {
            visit(mmc);
        }
        // methodNode.accessModifiers = currentMethodAccessModifiers;
        visit(ctx.methodHeader());
        MethodNode methodNode = new MethodNode();
        methodNode.setMethodName(currentMethodName);
        methodNode.setReturnType(currentMethodRetType);
        methodNode.setAccessModifiers(currentMethodAccessModifiers);

        MethodVisitor mv = new MethodVisitor();
        mv.visit(ctx.methodBody());

        methodNode.setStatements(mv.statements);
        
        // need to add the methodVisitor's list of statements to the methodNode
        methods.add(methodNode);
        return null;
    }

    @Override
    public Void visitMethodModifier(MethodModifierContext ctx) {
        // methodModifier
        //     : annotation
        //     | 'public'
        //     | 'protected'
        //     | 'private'
        //     | 'abstract'
        //     | 'static'
        //     | 'final'
        //     | 'synchronized'
        //     | 'native'
        //     | 'strictfp'
        //     ;
        if (ctx.annotation() == null) {
            currentMethodAccessModifiers.add(ctx.getText());
        }

        return null;
    }

    @Override
    public Void visitResult(ResultContext ctx) {
        // result
        //     : unannType
        //     | 'void'
        //     ;
        if (ctx.VOID() != null) {
            currentMethodRetType = "void";
        } else {
            visit(ctx.unannType());
        }
        
        return null;
    }

    @Override
    public Void visitIntegralType(IntegralTypeContext ctx) {
        currentMethodRetType = ctx.getText();
        return null;
    }

    @Override
    public Void visitMethodDeclarator(MethodDeclaratorContext ctx) {
        // methodDeclarator
        //     : Identifier '(' formalParameterList? ')' dims?
        //     ;
        currentMethodName = ctx.Identifier().getText();        
        
        return null;
    }
    
}
