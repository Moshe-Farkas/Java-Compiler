package com.moshefarkas.javacompiler.ast.astgen;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Opcodes;

import com.moshefarkas.generated.Java8Parser.FieldDeclarationContext;
import com.moshefarkas.generated.Java8Parser.FieldModifierContext;
import com.moshefarkas.generated.Java8ParserBaseVisitor;
import com.moshefarkas.javacompiler.ast.nodes.FieldNode;

public class FieldVisitor extends Java8ParserBaseVisitor<Void> {

    public FieldNode fieldNode;

    @Override
    public Void visitFieldDeclaration(FieldDeclarationContext ctx) {
        // fieldDeclaration
        //     : fieldModifier* unannType variableDeclaratorList ';'
        //     ;
        List<Integer> fieldModifiers = new ArrayList<>();
        for (FieldModifierContext fmc : ctx.fieldModifier()) {
            fieldModifiers.add(modifier(fmc.getText()));
        }
        VarVisitor varVisitor = new VarVisitor();
        varVisitor.visit(ctx);

        fieldNode = new FieldNode();
        fieldNode.setName(varVisitor.varName);
        fieldNode.setType(varVisitor.varType);
        fieldNode.setFieldModifiers(fieldModifiers);
        fieldNode.setInitializerNode(varVisitor.initializer);
        fieldNode.setHasValue(varVisitor.initializer != null);
        return null;
    }
    
    private int modifier(String mod) {
        // fieldModifier
        //     : annotation
        //     | 'public'
        //     | 'protected'
        //     | 'private'
        //     | 'static'
        //     | 'final'
        //     | 'transient'
        //     | 'volatile'
        //     ;
        switch (mod) {
            case "public":
                return Opcodes.ACC_PUBLIC;
            case "private":
                return Opcodes.ACC_PRIVATE;
            case "protected":
                return Opcodes.ACC_PROTECTED;
            case "static":
                return Opcodes.ACC_STATIC;
            case "final":
                return Opcodes.ACC_FINAL;
            default:
                throw new UnsupportedOperationException("inside visit method modifier in class body vis");
        }
    }
}
