package com.moshefarkas.javacompiler.ast.astgen;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Opcodes;

import com.moshefarkas.generated.Java8Parser.ClassModifierContext;
import com.moshefarkas.generated.Java8Parser.NormalClassDeclarationContext;
import com.moshefarkas.generated.Java8ParserBaseVisitor;
import com.moshefarkas.javacompiler.ast.nodes.ClassNode;

public class ClassVisitor extends Java8ParserBaseVisitor<Void> {

    public ClassNode currentClass;
    public List<Integer> classModifiers = new ArrayList<>();

    @Override
    public Void visitNormalClassDeclaration(NormalClassDeclarationContext ctx) {
        // normalClassDeclaration
        //     : classModifier* 'class' Identifier typeParameters? superclass? superinterfaces? classBody
        //     ;
        String className = ctx.Identifier().getText();
        ClassBodyVisitor cbv = new ClassBodyVisitor();
        cbv.visit(ctx.classBody());

        currentClass = new ClassNode();
        currentClass.setAccessModifiers(classModifiers);
        currentClass.setClassName(className);
        currentClass.setMethods(cbv.methods);
        currentClass.setFields(cbv.fields);
        currentClass.setConstrcutors(cbv.constructors);
        currentClass.lineNum = ctx.getStart().getLine();
        return null;
    }

    @Override
    public Void visitClassModifier(ClassModifierContext ctx) {
        // classModifier
        //     : annotation
        //     | 'public'
        //     | 'protected'
        //     | 'private'
        //     | 'abstract'
        //     | 'static'
        //     | 'final'
        //     | 'strictfp'
        //     ;
        int mod;
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
            case "abstract":
                mod = Opcodes.ACC_ABSTRACT;
                break;
            case "static":
                mod = Opcodes.ACC_STATIC;
                break;
            case "final":
                mod = Opcodes.ACC_FINAL;
                break;
            default:
                throw new UnsupportedOperationException("inside visit class mod in class visitor");
        }
        classModifiers.add(mod);
        return null;
    }
}
