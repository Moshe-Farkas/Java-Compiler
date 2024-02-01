package com.moshefarkas;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.moshefarkas.generated.Java8Parser.LiteralContext;
import com.moshefarkas.generated.Java8Parser.MethodDeclarationContext;
import com.moshefarkas.generated.Java8Parser.NormalClassDeclarationContext;
import com.moshefarkas.generated.Java8ParserBaseVisitor;

public class MyVisitor extends Java8ParserBaseVisitor<Object> {

    public ClassWriter cw;
    private MethodVisitor mv;


    @Override
    public Object visitNormalClassDeclaration(NormalClassDeclarationContext ctx) {
        // normalClassDeclaration
        //     : classModifier* 'class' Identifier typeParameters? superclass? superinterfaces? classBody
        //     ;
        
        cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        String className = ctx.Identifier().getText();        
        cw.visit(
            Opcodes.V1_8,   // class format version
            Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER,  
            className, // class name 
            null, // 
            "java/lang/Object", // super name
            null // interfaces
        );          

        visit(ctx.classBody());
        cw.visitEnd();
        return null;
    }

    @Override
    public Object visitMethodDeclaration(MethodDeclarationContext ctx) {
        // methodDeclaration
        //     : methodModifier* methodHeader methodBody
        //     ;
        int modifier = Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC;
        String methName = ctx.methodHeader().methodDeclarator().Identifier().getText();
        String descriptor = "()V";
        String signature = "()V";

        mv = cw.visitMethod(
            modifier,
            methName, 
            descriptor, 
            signature, 
            null
        );
        mv.visitCode();
        visit(ctx.methodBody());

        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        return null;
    }

    @Override
    public Object visitLiteral(LiteralContext ctx) {
        // literal
        //     : IntegerLiteral
        //     | FloatingPointLiteral
        //     | BooleanLiteral
        //     | CharacterLiteral
        //     | StringLiteral
        //     | NullLiteral
        //     ;

        if (ctx.IntegerLiteral() != null) {
            int value = Integer.valueOf(ctx.IntegerLiteral().getText());
            mv.visitLdcInsn(value);

        }


        return super.visitLiteral(ctx);
    }
}
