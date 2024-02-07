package com.moshefarkas.javacompiler.ast.astgen;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Type;

import com.moshefarkas.generated.Java8Parser.FloatingPointTypeContext;
import com.moshefarkas.generated.Java8Parser.FormalParameterContext;
import com.moshefarkas.generated.Java8Parser.FormalParameterListContext;
import com.moshefarkas.generated.Java8Parser.IntegralTypeContext;
import com.moshefarkas.generated.Java8Parser.MethodDeclarationContext;
import com.moshefarkas.generated.Java8Parser.MethodDeclaratorContext;
import com.moshefarkas.generated.Java8Parser.MethodHeaderContext;
import com.moshefarkas.generated.Java8Parser.MethodModifierContext;
import com.moshefarkas.generated.Java8Parser.ResultContext;
import com.moshefarkas.generated.Java8ParserBaseVisitor;
import com.moshefarkas.javacompiler.VarInfo;
import com.moshefarkas.javacompiler.ast.nodes.MethodNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.LocalVarDecStmtNode;

public class ClassBodyVisitor extends Java8ParserBaseVisitor<Object> {

    public List<MethodNode> methods = new ArrayList<>();
    public List<MethodNode> fields = new ArrayList<>();

    // private List<String> currentMethodAccessModifiers;
    // private List<LocalVarDecStmtNode> currentMethodParams;
    // private Type currentMethodRetType;
    // private String currentMethodName;


    // methods will return their object that they create


    @Override
    public Void visitMethodDeclaration(MethodDeclarationContext ctx) {
        // methodDeclaration
        //     : methodModifier* methodHeader methodBody
        //     ;
        List<String> methodAccessModifiers = new ArrayList<>();
        // List<MethodInfo> methodParams          = new ArrayList<>();
        
        for (MethodModifierContext mmc : ctx.methodModifier()) {
            methodAccessModifiers.add((String)visit(mmc));
        }

        // [0]: return type, [1] method name, [2] params list
        Object[] methHeader = (Object[])visit(ctx.methodHeader());
        Type returnType = (Type)methHeader[0];
        String methodName = (String)methHeader[1];
        List<LocalVarDecStmtNode> methodParams = (List<LocalVarDecStmtNode>)methHeader[2];

        MethodNode methodNode = new MethodNode();
        methodNode.setMethodName(methodName);
        methodNode.setReturnType(returnType);
        methodNode.setAccessModifiers(methodAccessModifiers);
        methodNode.setParams(methodParams);


        MethodVisitor mv = new MethodVisitor();
        mv.visit(ctx.methodBody());
        methodNode.setStatements(mv.statements);
        
        
        methodNode.lineNum = ctx.getStart().getLine();
        methods.add(methodNode);
        return null;
    }

    @Override
    public String visitMethodModifier(MethodModifierContext ctx) {
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
        return ctx.getText();
    }

    @Override
    public Object[] visitMethodHeader(MethodHeaderContext ctx) {
        // methodHeader
        //     : result methodDeclarator throws_?
        //     | typeParameters annotation* result methodDeclarator throws_?
        //     ;
        // need to aggragate the ret type, name and params into an object array
        Type returnType = (Type)visit(ctx.result());
        // [0]: methodNam, [1]: params
        Object[] nameAndParams = (Object[])visit(ctx.methodDeclarator());


        
        return new Object[] {returnType, nameAndParams[0], nameAndParams[1]};
    }

    @Override
    public Type visitResult(ResultContext ctx) {
        // result
        //     : unannType
        //     | 'void'
        //     ;
        Type t;
        if (ctx.VOID() != null) {
            t = Type.VOID_TYPE;
        } else {
            t = (Type)visit(ctx.unannType());
        }
        
        return t;
    }

    @Override
    public Type visitIntegralType(IntegralTypeContext ctx) {
        // currentMethodRetType = ctx.getText();
        Type t;
        if (ctx.BYTE() != null)  {
            t = Type.BYTE_TYPE;
        } else if (ctx.CHAR() != null) {
            t = Type.CHAR_TYPE;
        } else if (ctx.INT() != null) {
            t = Type.INT_TYPE;
        } else if (ctx.SHORT() != null) {
            t = Type.SHORT_TYPE;
        } else {
            throw new UnsupportedOperationException("inside intergral type class body visitor");
        }
        return t;
    }

    @Override
    public Type visitFloatingPointType(FloatingPointTypeContext ctx) {
        Type t;
        if (ctx.FLOAT() != null) {
            t = Type.FLOAT_TYPE;
        } else {
            throw new UnsupportedOperationException("inside floating point class body visiter");
        }
        return t;
    }

    @Override
    public Object[] visitMethodDeclarator(MethodDeclaratorContext ctx) {
        // methodDeclarator
        //     : Identifier '(' formalParameterList? ')' dims?
        //     ;

        // need to return an methodName and params

        String methodName = ctx.Identifier().getText();        
        List<LocalVarDecStmtNode> params = new ArrayList<>();
        if (ctx.formalParameterList() != null) {
            params = (List<LocalVarDecStmtNode>)visit(ctx.formalParameterList());
        }
    
        return new Object[] {methodName, params};
    }

    @Override
    public Object visitFormalParameterList(FormalParameterListContext ctx) {
        // formalParameterList
        //     : receiverParameter
        //     | formalParameters ',' lastFormalParameter
        //     | lastFormalParameter
        //     ;

        List<LocalVarDecStmtNode> params = new ArrayList<>();
        if (ctx.formalParameters() != null) {
            for (FormalParameterContext fpc : ctx.formalParameters().formalParameter()) {
                params.add(visitFormalParameter(fpc));
            }
        }
        params.add(visitFormalParameter(ctx.lastFormalParameter().formalParameter()));
        return params;
    }

    @Override
    public LocalVarDecStmtNode visitFormalParameter(FormalParameterContext ctx) {
        // formalParameter
        //     : variableModifier* unannType variableDeclaratorId
        //     ;

        LocalVarDecStmtNode varNode = new LocalVarDecStmtNode();
        VarInfo var = new VarInfo();
        Type type = (Type)visit(ctx.unannType());
        String paramName = ctx.variableDeclaratorId().Identifier().getText();
        var.initialized = false;
        var.localIndex = 0;
        var.type = type;
        var.name = paramName;

        varNode.setVar(var);
        varNode.lineNum = ctx.getStart().getLine();
        return varNode;
    }
}
