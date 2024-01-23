package com.moshefarkas.javacompiler.semanticanalysis;

import java.util.Stack;

import com.moshefarkas.generated.JavaBaseVisitor;
import com.moshefarkas.generated.JavaParser.BlockStatementContext;
import com.moshefarkas.generated.JavaParser.ExpressionContext;
import com.moshefarkas.generated.JavaParser.IntegerLiteralContext;
import com.moshefarkas.generated.JavaParser.LiteralContext;
import com.moshefarkas.generated.JavaParser.LocalVariableDeclarationContext;
import com.moshefarkas.generated.JavaParser.PrimaryContext;
import com.moshefarkas.generated.JavaParser.PrimitiveTypeContext;
import com.moshefarkas.generated.JavaParser.VariableDeclaratorContext;
import com.moshefarkas.generated.JavaParser.VariableDeclaratorIdContext;
import com.moshefarkas.generated.JavaParser.VariableDeclaratorsContext;
import com.moshefarkas.javacompiler.SymbolTable;
import com.moshefarkas.javacompiler.VarInfo;

public class TypeCheckVisitor extends JavaBaseVisitor<Void> {
    public enum Type {
        ERROR,
        INT,
        FLOAT,
        CHAR,
        STRING,
        OBJECT,
        BOOL,
        BYTE, 
        SHORT,
    }

    // mostly the leaf nodes are the ones that push types on the type stack
    // check for already defined vars.

    private final Stack<Type> typeStack = new Stack<>();
    private final Stack<VarInfo> varInfoStack = new Stack<>();

    private void checkTypes(String errorMessage) {
        Type b = typeStack.pop();
        Type a = typeStack.pop();
        if (a != b) {
            typeStack.push(Type.ERROR);
            System.err.printf("%s type `%s` is incompatable with type `%s`.\n", errorMessage, a, b);
        } else {
            typeStack.push(a);
        }
    }

    private void undefinedVariable(String identifier) {
        System.err.printf("`%s` cannot be resolved to a variable.\n", identifier);
        typeStack.push(Type.ERROR);
    }

    private boolean alreadyDefined(String identifier) {
        return SymbolTable.getInstance().hasVar(identifier);
    }

    private void resetTypeStack() {
        typeStack.clear();
    }

    @Override
    public Void visitBlockStatement(BlockStatementContext ctx) {
        // blockStatement
        //     :   localVariableDeclarationStatement
        //     |   classDeclaration
        //     |   interfaceDeclaration
        //     |   statement
        //     ;
        if (ctx.localVariableDeclarationStatement() != null) {
            visit(ctx.localVariableDeclarationStatement());
        } else if (ctx.statement() != null) {
            visit(ctx.statement());
        }

        resetTypeStack();
        return null;
    }

    @Override
    public Void visitLocalVariableDeclaration(LocalVariableDeclarationContext ctx) {
        // localVariableDeclaration
        //     :   variableModifiers type variableDeclarators
        //     ;
        visit(ctx.type());
        Type varType = typeStack.peek();

        visit(ctx.variableDeclarators());
        VarInfo declaredVar = varInfoStack.pop();
        declaredVar.type = varType;
        if (alreadyDefined(declaredVar.name)) {
            System.err.printf("Duplicate local variable `%s`.", declaredVar.name);
        } else {
            SymbolTable.getInstance().addLocal(declaredVar.name, declaredVar);
        }
        if (declaredVar.initialized) {
            // check if the initializer matches the var type
            checkTypes("Type assignment error.");
        }
        return null;
    }

    @Override
    public Void visitVariableDeclarators(VariableDeclaratorsContext ctx) {
        // variableDeclarators
        //     :   variableDeclarator (',' variableDeclarator)*
        //     ;
        visit(ctx.variableDeclarator(0));
        return null;
    }

    @Override
    public Void visitVariableDeclarator(VariableDeclaratorContext ctx) {
        // variableDeclarator
        //     :   variableDeclaratorId ('=' variableInitializer)?
        //     ;
        visit(ctx.variableDeclaratorId());
        if (ctx.variableInitializer() != null) {
            visit(ctx.variableInitializer());
            varInfoStack.peek().initialized = true;
        }
        return null;
    }

    @Override
    public Void visitVariableDeclaratorId(VariableDeclaratorIdContext ctx) {
        // variableDeclaratorId
        //     :   Identifier ('[' ']')*
        //     ;
        VarInfo varInfo = new VarInfo();
        varInfo.name = ctx.Identifier().getText();
        varInfoStack.push(varInfo);
        return null;
    }

    @Override
    public Void visitPrimitiveType(PrimitiveTypeContext ctx) {
        // primitiveType
        //     :   'boolean'
        //     |   'char'
        //     |   'byte'
        //     |   'short'
        //     |   'int'
        //     |   'long'
        //     |   'float'
        //     |   'double'
        //     ;
        Type t = Type.ERROR;
        switch (ctx.getText()) {
            case "boolean":
                t = Type.BOOL; 
                break;
            case "char":
                t = Type.CHAR; 
                break;
            case "byte":
                t = Type.BYTE; 
                break;
            case "short":
                t = Type.SHORT; 
                break;
            case "int":
                t = Type.INT; 
                break;
            case "float":
                t = Type.FLOAT; 
                break;
        
            default:
                throw new UnsupportedOperationException("Unspported type: " + ctx.getText());
        } 
        typeStack.push(t);

        return null;
    }

    @Override
    public Void visitExpression(ExpressionContext ctx) {
        if (ctx.primary() != null) {
            // when ancountered an identifier, lookup its type and place it on the stack.
            visit(ctx.primary());
        } else if (ctx.op != null) {
            visit(ctx.expression(0));
            visit(ctx.expression(1));
            switch (ctx.op.getText()) {
                case "+": 
                case "-": 
                case "=":
                case "*":
                    checkTypes("Mismatched types.");
                    break;
                default:
                    throw new UnsupportedOperationException("because of this: " + ctx.op.getText());
            }
        }
        return null;
    }

    @Override
    public Void visitPrimary(PrimaryContext ctx) {
        // primary
        //     :   '(' expression ')'
        //     |   'this'
        //     |   'super'
        //     |   literal
        //     |   Identifier
        //     |   type '.' 'class'
        //     |   'void' '.' 'class'
        //     ;
        if (ctx.literal() != null) {
            visit(ctx.literal());
        } else if (ctx.Identifier() != null) {
            // lookup its name in symbol table and push its type 
            identifierExpression(ctx.Identifier().getText());
        }
        return null;
    }

    private void identifierExpression(String identifier) {
        if (alreadyDefined(identifier)) {
            typeStack.push(SymbolTable.getInstance().getType(identifier));
        } else {
            undefinedVariable(identifier);
        }
    }

    @Override
    public Void visitLiteral(LiteralContext ctx) {
        // literal
        //     :   integerLiteral
        //     |   FloatingPointLiteral
        //     |   CharacterLiteral
        //     |   StringLiteral
        //     |   booleanLiteral
        //     |   'null'
        //     ;
        if (ctx.integerLiteral() != null) {
            visit(ctx.integerLiteral());
        } else if (ctx.CharacterLiteral() != null) {
            typeStack.push(Type.CHAR);
        } else if (ctx.StringLiteral() != null) {
            typeStack.push(Type.STRING);
        } else if (ctx.FloatingPointLiteral() != null) {
            typeStack.push(Type.FLOAT);
        }
        return null;
    }

    @Override
    public Void visitIntegerLiteral(IntegerLiteralContext ctx) {
        // integerLiteral
        //     :   HexLiteral
        //     |   OctalLiteral
        //     |   DecimalLiteral
        //     ;
        typeStack.push(Type.INT);
        return null;
    }
}

