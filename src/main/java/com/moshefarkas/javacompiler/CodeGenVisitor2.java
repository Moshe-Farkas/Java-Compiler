package com.moshefarkas.javacompiler;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.moshefarkas.generated.JavaBaseVisitor;
import com.moshefarkas.generated.JavaParser.BlockStatementContext;
import com.moshefarkas.generated.JavaParser.ExpressionContext;
import com.moshefarkas.generated.JavaParser.IntegerLiteralContext;
import com.moshefarkas.generated.JavaParser.LocalVariableDeclarationContext;
import com.moshefarkas.generated.JavaParser.PrimaryContext;
import com.moshefarkas.generated.JavaParser.VariableDeclaratorIdContext;
import com.moshefarkas.javacompiler.dispatching.LiteralDispatcher;

public class CodeGenVisitor2 extends JavaBaseVisitor<Object> {

    public final List<Byte> bytecode = new ArrayList<>();
    private final Map<String, Short> localVars = new HashMap<>();
    private short currentLocalVarIndex = 0;
    private final Stack<Object> operandStack = new Stack<>();

    public void compile(OutputStream os) {
        try {
            DataOutputStream dos = new DataOutputStream(os);
            for (byte b : bytecode) {
                dos.writeByte(b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void newMethodDecl(String methName) { 
    }

    private void newVariableDecl(String varName) {
        localVars.put(varName, currentLocalVarIndex++);
    }

    @Override
    public Object visitBlockStatement(BlockStatementContext ctx) {
    // blockStatement
    //     :   localVariableDeclarationStatement
    //     |   classDeclaration
    //     |   interfaceDeclaration
    //     |   statement
    //     ;
        // can be either local var decl or statement

        if (ctx.localVariableDeclarationStatement() != null) {
            visit(ctx.localVariableDeclarationStatement());
        } else {
            visit(ctx.statement());
        }

        return null;
    }

    @Override
    public Object visitExpression(ExpressionContext ctx) {
        if (ctx.primary() != null) {
            visit(ctx.primary());
        }
         
        // if (ctx.op != null)
            // switch (ctx.op.getText()) {
            //     case "+":
            //         Object left = visit(ctx.expression(0));
            //         Object right = visit(ctx.expression(1));
            //         dispatchOp(left, right, ctx.op.getText());
            //         break;
            // }
        return null;
    }

    @Override
    public Object visitIntegerLiteral(IntegerLiteralContext ctx) {
        //  integerLiteral
        //     :   HexLiteral
        //     |   OctalLiteral
        //     |   DecimalLiteral
        //     ;       
        int input = Integer.valueOf(ctx.getText());

        byte[] code = new LiteralDispatcher(input).dispatchForInt();
        for (byte b : code) {
            bytecode.add(b);
        }

        return input;
    }

    @Override
    public Object visitPrimary(PrimaryContext ctx) {
        // primary
        //     :   '(' expression ')'
        //     |   'this'
        //     |   'super'
        //     |   literal
        //     |   Identifier
        //     |   type '.' 'class'
        //     |   'void' '.' 'class'

        if (ctx.Identifier() != null) {
            operandStack.push(ctx.Identifier().getText());
        }

        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object visitVariableDeclaratorId(VariableDeclaratorIdContext ctx) {
        String iden = ctx.Identifier().getText();
        localVars.put(iden, currentLocalVarIndex++);

        // TODO Auto-generated method stub
        return super.visitVariableDeclaratorId(ctx);
    }
}
