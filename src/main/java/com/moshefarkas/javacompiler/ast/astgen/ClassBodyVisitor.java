package com.moshefarkas.javacompiler.ast.astgen;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.moshefarkas.generated.Java8Parser.ClassMemberDeclarationContext;
import com.moshefarkas.generated.Java8Parser.ConstructorDeclarationContext;
import com.moshefarkas.generated.Java8Parser.ConstructorDeclaratorContext;
import com.moshefarkas.generated.Java8Parser.ConstructorModifierContext;
import com.moshefarkas.generated.Java8Parser.FieldDeclarationContext;
import com.moshefarkas.generated.Java8Parser.FloatingPointTypeContext;
import com.moshefarkas.generated.Java8Parser.FormalParameterContext;
import com.moshefarkas.generated.Java8Parser.FormalParameterListContext;
import com.moshefarkas.generated.Java8Parser.IntegralTypeContext;
import com.moshefarkas.generated.Java8Parser.MethodDeclarationContext;
import com.moshefarkas.generated.Java8Parser.MethodDeclaratorContext;
import com.moshefarkas.generated.Java8Parser.MethodHeaderContext;
import com.moshefarkas.generated.Java8Parser.MethodModifierContext;
import com.moshefarkas.generated.Java8Parser.ResultContext;
import com.moshefarkas.generated.Java8Parser.UnannArrayTypeContext;
import com.moshefarkas.generated.Java8Parser.UnannPrimitiveTypeContext;
import com.moshefarkas.generated.Java8ParserBaseVisitor;
import com.moshefarkas.javacompiler.ast.nodes.ConstructorNode;
import com.moshefarkas.javacompiler.ast.nodes.FieldNode;
import com.moshefarkas.javacompiler.ast.nodes.MethodNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.LocalVarDecStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.StatementNode;

public class ClassBodyVisitor extends Java8ParserBaseVisitor<Object> {

    public List<MethodNode> methods = new ArrayList<>();
    public List<FieldNode> fields = new ArrayList<>();
    public List<ConstructorNode> constructors = new ArrayList<>();

    @Override
    public Object visitFieldDeclaration(FieldDeclarationContext ctx) {
        // fieldDeclaration
        //     : fieldModifier* unannType variableDeclaratorList ';'
        //     ;
        FieldVisitor fieldVisitor = new FieldVisitor();
        fieldVisitor.visitFieldDeclaration(ctx);

        fieldVisitor.fieldNode.lineNum = ctx.getStart().getLine();
        fields.add(fieldVisitor.fieldNode);
        return null;
    }

    @Override
    public Void visitMethodDeclaration(MethodDeclarationContext ctx) {
        // methodDeclaration
        //     : methodModifier* methodHeader methodBody
        //     ;
        List<Integer> methodAccessModifiers = new ArrayList<>();
        
        for (MethodModifierContext mmc : ctx.methodModifier()) {
            methodAccessModifiers.add(visitMethodModifier(mmc));
        }

        // [0]: return type, [1] method name, [2] params list
        Object[] methHeader = (Object[])visit(ctx.methodHeader());
        Type returnType = (Type)methHeader[0];
        String methodName = (String)methHeader[1];
        List<LocalVarDecStmtNode> methodParams = (List<LocalVarDecStmtNode>)methHeader[2];

        MethodNode methodNode = new MethodNode();
        methodNode.setMethodName(methodName);
        methodNode.setReturnType(returnType);
        methodNode.setMethodModifiers(methodAccessModifiers);
        methodNode.setParams(methodParams);

        MethodVisitor mv = new MethodVisitor();
        mv.visitMethodBody(ctx.methodBody());
        methodNode.setStatements(mv.statements);
        
        methodNode.lineNum = ctx.getStart().getLine();
        methods.add(methodNode);
        return null;
    }

    @Override
    public ConstructorNode visitConstructorDeclaration(ConstructorDeclarationContext ctx) {
        // constructorDeclaration
        //     : constructorModifier* constructorDeclarator throws_? constructorBody
        //     ;
        List<Integer> methodAccessModifiers = new ArrayList<>();
        for (ConstructorModifierContext cmc : ctx.constructorModifier()) {
            methodAccessModifiers.add(visitConstructorModifier(cmc));
        }

        String constructorName = ctx.constructorDeclarator().simpleTypeName().getText();
        List<LocalVarDecStmtNode> params = new ArrayList<>();
        if (ctx.constructorDeclarator().formalParameterList() != null) {
            params = (List<LocalVarDecStmtNode>)visit(ctx.constructorDeclarator().formalParameterList());
        }
        ConstructorNode constructorNode = new ConstructorNode();

        MethodVisitor mVisitor = new MethodVisitor();
        mVisitor.visitConstructorBody(ctx.constructorBody());

        constructorNode.setParams(params);
        constructorNode.setStatements(mVisitor.statements);
        constructorNode.setMethodModifiers(methodAccessModifiers);
        constructorNode.setMethodName(constructorName);
        constructorNode.lineNum = ctx.getStart().getLine();
        constructors.add(constructorNode);
        return null;
    }

    @Override
    public Integer visitConstructorModifier(ConstructorModifierContext ctx) {
        // constructorModifier
        //     : annotation
        //     | 'public'
        //     | 'protected'
        //     | 'private'
        //     ;
        if (ctx.annotation() != null) {
            throw new UnsupportedOperationException("inside constructor modifer in class bod visitor");
        }
        int mod = -1;
        switch (ctx.getText()) {
            case "public":
                mod = Opcodes.ACC_PUBLIC;
                break;
            case "private":
                mod = Opcodes.ACC_PRIVATE;
                break;
            case "protected":
                mod = Opcodes.ACC_PROTECTED;
                break;
        }
        return mod;
    }

    @Override
    public Integer visitMethodModifier(MethodModifierContext ctx) {
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
        switch (ctx.getText()) {
            case "public":
                return Opcodes.ACC_PUBLIC;
            case "private":
                return Opcodes.ACC_PRIVATE;
            case "protected":
                return Opcodes.ACC_PROTECTED;
            case "abstract":
                return Opcodes.ACC_ABSTRACT;
            case "static":
                return Opcodes.ACC_STATIC;
            case "final":
                return Opcodes.ACC_FINAL;
            default:
                throw new UnsupportedOperationException("inside visit method modifier in class body vis");
        }
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

        // Type type = (Type)visit(ctx.unannType());
        // String paramName = ctx.variableDeclaratorId().Identifier().getText();

        // varNode.hasValue = true;
        // varNode.setVarName(paramName);
        // varNode.setType(type);

        LocalVarDecStmtNode paramNode = new LocalVarDecStmtNode();

        VarVisitor varVisitor = new VarVisitor();
        varVisitor.visit(ctx);

        paramNode.setInitializerNode(varVisitor.initializer);
        paramNode.setType(varVisitor.varType);
        paramNode.setName(varVisitor.varName);
        paramNode.hasValue = true;
        paramNode.lineNum = ctx.getStart().getLine();
        return paramNode;
    }

    @Override
    public Type visitUnannPrimitiveType(UnannPrimitiveTypeContext ctx) {
        // unannPrimitiveType
        //     : numericType
        //     | 'boolean'
        //     ;
        if (ctx.BOOLEAN() != null) {
            return Type.BOOLEAN_TYPE;
        }
        return (Type)visit(ctx.numericType());
    }

    @Override
    public Type visitUnannArrayType(UnannArrayTypeContext ctx) {
        // unannArrayType
        //     : unannPrimitiveType dims
        //     | unannClassOrInterfaceType dims
        //     | unannTypeVariable dims
        //     ;
        int dims = ctx.dims().LBRACK().size();
        String typeStr = "";
        for (int i = 0; i < dims; i++) {
            typeStr += "[";
        }
        return Type.getType(typeStr + (Type)visitUnannPrimitiveType(ctx.unannPrimitiveType()));
    }
}
