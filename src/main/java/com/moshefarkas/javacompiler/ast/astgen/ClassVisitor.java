package com.moshefarkas.javacompiler.ast.astgen;

import com.moshefarkas.generated.Java8Parser.NormalClassDeclarationContext;
import com.moshefarkas.generated.Java8ParserBaseVisitor;
import com.moshefarkas.javacompiler.ast.nodes.ClassNode;

public class ClassVisitor extends Java8ParserBaseVisitor<Void> {

    // public ClassNode currentClass = new ClassNode();
    public ClassNode currentClass;

    @Override
    public Void visitNormalClassDeclaration(NormalClassDeclarationContext ctx) {
        // normalClassDeclaration
        //     : classModifier* 'class' Identifier typeParameters? superclass? superinterfaces? classBody
        //     ;
        // currentClass.className = ctx.Identifier().getText();
        String className = ctx.Identifier().getText();
        ClassBodyVisitor cbv = new ClassBodyVisitor();
        cbv.visit(ctx.classBody());
        // currentClass.methods = cbv.methods;
        currentClass = new ClassNode(className, 0, cbv.methods);

        return null;
    }
}
