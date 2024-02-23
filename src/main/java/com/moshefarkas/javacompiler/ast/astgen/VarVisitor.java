package com.moshefarkas.javacompiler.ast.astgen;

import org.objectweb.asm.Type;

import com.moshefarkas.generated.Java8Parser.FloatingPointTypeContext;
import com.moshefarkas.generated.Java8Parser.IntegralTypeContext;
import com.moshefarkas.generated.Java8Parser.UnannArrayTypeContext;
import com.moshefarkas.generated.Java8Parser.UnannClassType_lfno_unannClassOrInterfaceTypeContext;
import com.moshefarkas.generated.Java8Parser.UnannPrimitiveTypeContext;
import com.moshefarkas.generated.Java8Parser.UnannReferenceTypeContext;
import com.moshefarkas.generated.Java8Parser.UnannTypeVariableContext;
import com.moshefarkas.generated.Java8Parser.VariableDeclaratorContext;
import com.moshefarkas.generated.Java8Parser.VariableDeclaratorIdContext;
import com.moshefarkas.generated.Java8Parser.VariableInitializerContext;
import com.moshefarkas.generated.Java8ParserBaseVisitor;
import com.moshefarkas.javacompiler.ast.nodes.expression.ExpressionNode;

public class VarVisitor extends Java8ParserBaseVisitor<Void> {
    // vars have: type, name, and initializer
    public String varName;
    public Type varType;
    public ExpressionNode initializer = null;

    // used by field declarations and method visitor for local vars
    // unannType variableDeclaratorList


    @Override
    public Void visitVariableDeclarator(VariableDeclaratorContext ctx) {
        // variableDeclarator
        //     : variableDeclaratorId ('=' variableInitializer)?
        //     ;
        
        if (ctx.variableInitializer() != null) {
            visitVariableInitializer(ctx.variableInitializer());
        }
        visitVariableDeclaratorId(ctx.variableDeclaratorId());

        return null;
    }

    @Override
    public Void visitVariableDeclaratorId(VariableDeclaratorIdContext ctx) {
        // variableDeclaratorId
        //     : Identifier dims?
        //     ;
        varName = ctx.Identifier().getText();
        if (ctx.dims() != null) {
            throw new UnsupportedOperationException("inside var decl id in var visitor");
        }
        return null;
    }

    @Override
    public Void visitVariableInitializer(VariableInitializerContext ctx) {
        // variableInitializer
        //     : expression
        //     | arrayInitializer
        //     ;
        if (ctx.arrayInitializer() != null) {
            initializer = (ExpressionNode)(new ExpressionVisitor().visit(ctx.arrayInitializer()));
        } else {
            initializer = (ExpressionNode)(new ExpressionVisitor().visit(ctx.expression()));
        }
        return null;
    }

    @Override
    public Void visitUnannPrimitiveType(UnannPrimitiveTypeContext ctx) {
        // unannPrimitiveType
        //     : numericType
        //     | 'boolean'
        //     ;
        if (ctx.BOOLEAN() != null) {
            varType = Type.BOOLEAN_TYPE;
        } else {
            visit(ctx.numericType());
        }
        return null;
    }

    @Override
    public Void visitUnannArrayType(UnannArrayTypeContext ctx) {
        // unannArrayType
        //     : unannPrimitiveType dims
        //     | unannClassOrInterfaceType dims
        //     | unannTypeVariable dims
        //     ;
        int dimsCount = ctx.dims().LBRACK().size();
        if (ctx.unannPrimitiveType() != null) {
            visitUnannPrimitiveType(ctx.unannPrimitiveType());
        } else {
            throw new UnsupportedOperationException("inside unann array type in var visitor");
        }
        String dims = "";
        for (int i = 0; i < dimsCount; i++) {
            dims += "[";
        }
        varType = Type.getType(dims + varType.getDescriptor());
        return null;
    }

    @Override
    public Void visitFloatingPointType(FloatingPointTypeContext ctx) {
        // floatingPointType
        //     : 'float'
        //     | 'double'
        //     ;
        if (ctx.FLOAT() != null) {
            varType = Type.FLOAT_TYPE;
        } 
        return null;
    }

    @Override
    public Void visitIntegralType(IntegralTypeContext ctx) {
        // integralType
        //     : 'byte'
        //     | 'short'
        //     | 'int'
        //     | 'long'
        //     | 'char'
        //     ;
        Type declType = null;
        switch (ctx.getText()) {
            case "int":
                declType = Type.INT_TYPE;
                break;
            case "char":
                declType = Type.CHAR_TYPE;
                break;
            case "byte":
                declType = Type.BYTE_TYPE;
                break;
            case "short":
                declType = Type.SHORT_TYPE;
                break;
        } 
        varType = declType;
        return null;
    }

    @Override
    public Void visitUnannClassType_lfno_unannClassOrInterfaceType(
            UnannClassType_lfno_unannClassOrInterfaceTypeContext ctx) {
        // unannClassType_lfno_unannClassOrInterfaceType
        //     : Identifier typeArguments?
        //     ;
        varType = Type.getType("L" + ctx.Identifier().getText() + ";");
        if (ctx.typeArguments() != null) {
            throw new UnsupportedOperationException("inside var visitor");
        }
        return null;
    }
}
