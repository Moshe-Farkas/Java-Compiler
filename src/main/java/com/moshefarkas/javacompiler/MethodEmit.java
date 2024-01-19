package com.moshefarkas.javacompiler;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.moshefarkas.generated.JavaBaseVisitor;
import com.moshefarkas.generated.JavaParser.ClassInterTypeContext;
import com.moshefarkas.generated.JavaParser.ClassOrInterfaceTypeContext;
import com.moshefarkas.generated.JavaParser.FormalParameterDeclsContext;
import com.moshefarkas.generated.JavaParser.FormalParameterDeclsRestContext;
import com.moshefarkas.generated.JavaParser.FormalParametersContext;
import com.moshefarkas.generated.JavaParser.NonVoidMethodContext;
import com.moshefarkas.generated.JavaParser.PrimtypeContext;
import com.moshefarkas.generated.JavaParser.VariableDeclaratorIdContext;
import com.moshefarkas.generated.JavaParser.VoidMethodContext;

public class MethodEmit extends JavaBaseVisitor<Object> {

    @Override
    public Object visitNonVoidMethod(NonVoidMethodContext ctx) {
        // :   type Identifier formalParameters ('[' ']')* methodDeclarationRest   # NonVoidMethod
        String methName = ctx.Identifier().getText();
        String type = (String)visit(ctx.type());
        String params = (String)visit(ctx.formalParameters());
        System.out.println(methName + " <- " + params + " : returns: " + type);
        return null;
    }

    @Override
    public Object visitVoidMethod(VoidMethodContext ctx) {
        String methName = ctx.Identifier().getText();
        String params = (String)visit(ctx.formalParameters());
        System.out.println(methName + " <- " + params + " : returns: void.");
        return null;
    }

    @Override
    public Object visitPrimtype(PrimtypeContext ctx) {
        return ctx.getText();
    }

    @Override
    public Object visitClassInterType(ClassInterTypeContext ctx) {
        String text = ctx.getText();
        String dimensions = "";
        while (text.endsWith("[]")) {
            dimensions += "[]";
            text = text.substring(0, text.length() - 2);
        }
        return visit(ctx.classOrInterfaceType()) + dimensions;
    }

    
    @Override
    public Object visitClassOrInterfaceType(ClassOrInterfaceTypeContext ctx) {

    // classOrInterfaceType
    //     :   Identifier typeArguments? ('.' Identifier typeArguments? )*
    //     ;
        String ret = "";
        for (TerminalNode iden : ctx.Identifier()) {
            ret += iden.getText(); 
        }
        return ret;
    }

    @Override
    public Object visitFormalParameterDecls(FormalParameterDeclsContext ctx) {
        String type = (String)visit(ctx.type());
        String iden = (String)visit(ctx.formalParameterDeclsRest());
        return type + " " + iden;
    }

    @Override
    public Object visitFormalParameterDeclsRest(FormalParameterDeclsRestContext ctx) {
    // :   variableDeclaratorId (',' formalParameterDecls)?
        String iden = (String)visit(ctx.variableDeclaratorId());
        if (ctx.formalParameterDecls() != null) {
            return iden + " " + visit(ctx.formalParameterDecls());
        }
        return iden;
    }

    @Override
    public Object visitVariableDeclaratorId(VariableDeclaratorIdContext ctx) {
        return ctx.Identifier().getText();
    }

    @Override
    public Object visitFormalParameters(FormalParametersContext ctx) {
        if (ctx.formalParameterDecls() != null) {
            // aka non empty params 
            return visit(ctx.formalParameterDecls());
        }
        return "";
    }

    // type -> classOrInter
}
