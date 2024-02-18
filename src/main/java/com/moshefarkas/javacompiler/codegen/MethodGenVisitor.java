package com.moshefarkas.javacompiler.codegen;

import java.nio.channels.AcceptPendingException;
import java.util.Stack;

import org.antlr.v4.parse.ANTLRParser.id_return;
import org.antlr.v4.parse.ANTLRParser.modeSpec_return;
import org.antlr.v4.parse.ANTLRParser.optionsSpec_return;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.stringtemplate.v4.compiler.STParser.memberExpr_return;

import com.ibm.icu.impl.coll.BOCSU;
import com.moshefarkas.javacompiler.VarInfo;
import com.moshefarkas.javacompiler.ast.BaseAstVisitor;
import com.moshefarkas.javacompiler.ast.nodes.MethodNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.ArrAccessExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.ArrayInitializerNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.AssignExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.BinaryExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.CallExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.CastExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.ExpressionNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.IdentifierExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.LiteralExprNode;
import com.moshefarkas.javacompiler.ast.nodes.expression.UnaryExprNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.BlockStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.IfStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.LocalVarDecStmtNode;
import com.moshefarkas.javacompiler.ast.nodes.statement.WhileStmtNode;
import com.moshefarkas.javacompiler.symboltable.SymbolTable;

public class MethodGenVisitor extends BaseAstVisitor {

    private MethodVisitor methodVisitor;
    private Stack<Label> labelStack = new Stack<>();

    public MethodGenVisitor(MethodVisitor methodVisitor) {
        this.methodVisitor = methodVisitor;
    }

    private void emitTypeCast(Type targetType, Type toCastType) {
        if (targetType == toCastType) 
            return;   // no need to cast to same type

        if (targetType == Type.FLOAT_TYPE) {
            methodVisitor.visitInsn(toCastType.getOpcode(Opcodes.I2F));
        } else if (targetType == Type.INT_TYPE && toCastType == Type.FLOAT_TYPE) {
            methodVisitor.visitInsn(Opcodes.F2I);
        }
    }

    @Override
    public void visitBlockStmtNode(BlockStmtNode node) {
        SymbolTable.getInstance().enterScope();
        super.visitBlockStmtNode(node);
        SymbolTable.getInstance().exitScope();
    }

    @Override
    public void visitMethodNode(MethodNode node) {
        methodVisitor.visitCode();
        super.visitMethodNode(node);

        // // temp sout
        // methodVisitor.visitFieldInsn(Opcodes.GETSTATIC, 
        //     "java/lang/System", 
        //     "out", 
        //     "Ljava/io/PrintStream;"
        // );

        // methodVisitor.visitVarInsn(Opcodes.ILOAD, 0);
    
        // methodVisitor.visitMethodInsn(
        //     Opcodes.INVOKEVIRTUAL, 
        //     "java/io/PrintStream", 
        //     "println", 
        //     "(I)V", 
        //     false
        // );

        methodVisitor.visitInsn(Opcodes.RETURN);
        methodVisitor.visitMaxs(-1, -1);
        methodVisitor.visitEnd();
    }

    @Override
    public void visitLocalVarDecStmtNode(LocalVarDecStmtNode node) {
        if (node.var.initialized) {
            visit(node.initializer);
            VarInfo var = SymbolTable.getInstance().getVarInfo(node.var.name);
            emitTypeCast(var.type, node.initializer.exprType);
            methodVisitor.visitVarInsn(var.type.getOpcode(Opcodes.ISTORE), var.localIndex);
        }
    }
    
    @Override
    public void visitAssignExprNode(AssignExprNode node) {
        if (node.identifier instanceof ArrAccessExprNode) {
            genArrAccStore((ArrAccessExprNode)node.identifier, node.assignmentValue);
        } else {
            visit(node.assignmentValue);
            VarInfo var = SymbolTable.getInstance().getVarInfo(node.identifier.varName);
            emitTypeCast(var.type, node.assignmentValue.exprType);
            methodVisitor.visitVarInsn(var.type.getOpcode(Opcodes.ISTORE), var.localIndex);
        }
    }

    private void genArrAccStore(ArrAccessExprNode node, ExpressionNode assignmentValue) {
        VarInfo var = SymbolTable.getInstance().getVarInfo(node.varName);
        methodVisitor.visitVarInsn(Opcodes.ALOAD, var.localIndex);
        visit(node.index);
        if (node.identifer instanceof ArrAccessExprNode) {
            recursiveArrAccStore((ArrAccessExprNode)node.identifer);
        }

        visit(assignmentValue);
        emitTypeCast(node.exprType, assignmentValue.exprType);
        methodVisitor.visitInsn(node.exprType.getOpcode(Opcodes.IASTORE));
    }

    private void recursiveArrAccStore(ArrAccessExprNode node) {
            methodVisitor.visitInsn(Opcodes.AALOAD);
            visit(node.index);
            if (node.identifer instanceof ArrAccessExprNode)
                recursiveArrAccStore((ArrAccessExprNode)node.identifer);
    }

    @Override
    public void visitCastExprNode(CastExprNode node) {
        visit(node.expression);
        emitTypeCast(node.targetCast, node.expression.exprType);
    }

    @Override
    public void visitCallExprNode(CallExprNode node) {
        Type[] paramTypes = SymbolTable.getInstance().getParamTypes(node.methodName);
        for (int i = 0; i < node.arguments.size(); i++) {
            visit(node.arguments.get(i));
            Type argType = node.arguments.get(i).exprType;
            Type paramType = paramTypes[i];
            emitTypeCast(paramType, argType);
        }

        String descriptor = SymbolTable.getInstance().getMethodDescriptor(node.methodName);

        methodVisitor.visitMethodInsn(
            Opcodes.INVOKESTATIC, 
            "Demo", 
            node.methodName, 
            descriptor, 
            false
        );
    }

    private void binBoolExpr(BinaryExprNode node, int op) {
        // now need to emit correct opcode based on node.expr and node.op
        // == -> cmp_ne 
        // != -> cmp_eq
        if (node.domType == Type.FLOAT_TYPE) {
            binBoolFloatExrp(node);
            return;
        }
        
        Label gotoFalse = new Label();
        methodVisitor.visitJumpInsn(node.domType.getOpcode(op), gotoFalse);
        emitBoolConst(true);
        Label gotoEnd = new Label();
        methodVisitor.visitJumpInsn(Opcodes.GOTO, gotoEnd);
        methodVisitor.visitLabel(gotoFalse);
        emitBoolConst(false);
        methodVisitor.visitLabel(gotoEnd);
    }

    private void binBoolFloatExrp(BinaryExprNode node) {
        Label gotoFalse = new Label();
        int comparisionOp;
        int jumpInsOp;
        switch (node.op) {
            case EQ_EQ: 
                comparisionOp = Opcodes.FCMPL;
                jumpInsOp = Opcodes.IFNE;
                break;
            case NOT_EQ:
                comparisionOp = Opcodes.FCMPL;
                jumpInsOp = Opcodes.IFEQ;
                break;
            case GT:
                comparisionOp = Opcodes.FCMPL;
                jumpInsOp = Opcodes.IFLE;
                break;
            case GT_EQ:
                comparisionOp = Opcodes.FCMPL;
                jumpInsOp = Opcodes.IFLT;
                break;
            case LT:
                comparisionOp = Opcodes.FCMPG;
                jumpInsOp = Opcodes.IFGE;
                break;
            case LT_EQ:
                comparisionOp = Opcodes.FCMPG;
                jumpInsOp = Opcodes.IFGT;
                break;
            default:
                comparisionOp = -1;
                jumpInsOp = -1;
        }

        methodVisitor.visitInsn(comparisionOp);
        methodVisitor.visitJumpInsn(jumpInsOp, gotoFalse);
        emitBoolConst(true);
        Label gotoEnd = new Label();
        methodVisitor.visitJumpInsn(Opcodes.GOTO, gotoEnd);
        methodVisitor.visitLabel(gotoFalse);
        emitBoolConst(false);
        methodVisitor.visitLabel(gotoEnd);
    }

    @Override
    public void visitBinaryExprNode(BinaryExprNode node) {
        Type leftExprType = node.left.exprType;
        Type rightExprType = node.right.exprType;

        visit(node.left);
        emitTypeCast(node.domType, leftExprType);
        visit(node.right);
        emitTypeCast(node.domType, rightExprType);
    
        switch (node.op) {
            case GT:
                binBoolExpr(node, Opcodes.IF_ICMPLE);
                break;
            case GT_EQ:
                binBoolExpr(node, Opcodes.IF_ICMPLT);
                break;
            case LT:
                binBoolExpr(node, Opcodes.IF_ICMPGE);
                break;
            case LT_EQ:
                binBoolExpr(node, Opcodes.IF_ICMPGT);
                break;
            case EQ_EQ:
                binBoolExpr(node, Opcodes.IF_ICMPNE);
                break;
            case NOT_EQ:
                binBoolExpr(node, Opcodes.IF_ICMPEQ);
                break;
            default:
                switch (node.op) {
                    case PLUS:
                        methodVisitor.visitInsn(node.exprType.getOpcode(Opcodes.IADD));
                        break;
                    case MINUS:
                        methodVisitor.visitInsn(node.exprType.getOpcode(Opcodes.ISUB));
                        break;
                    case DIV:
                        methodVisitor.visitInsn(node.exprType.getOpcode(Opcodes.IDIV));
                        break;
                    case MUL:
                        methodVisitor.visitInsn(node.exprType.getOpcode(Opcodes.IMUL));
                        break;
                    case MOD:
                        methodVisitor.visitInsn(node.exprType.getOpcode(Opcodes.IREM));
                        break;
                }
        }
    }

    @Override
    public void visitUnaryExprNode(UnaryExprNode node) {
        visit(node.expr);
        switch (node.op) {
            case MINUS:
                methodVisitor.visitInsn(Opcodes.INEG);
                break;
        }
    }

    @Override
    public void visitLiteralExprNode(LiteralExprNode node) {
        if (node.exprType == Type.BOOLEAN_TYPE) {
            emitBoolConst((boolean)node.value);
        } else {
            methodVisitor.visitLdcInsn(node.value);
        }
    }

    private void emitBoolConst(boolean val) {
        if (val)
            methodVisitor.visitInsn(Opcodes.ICONST_1);
        else 
            methodVisitor.visitInsn(Opcodes.ICONST_0);
    }

    @Override
    public void visitIdentifierExprNode(IdentifierExprNode node) {
        VarInfo var = SymbolTable.getInstance().getVarInfo(node.varName);
        int op = var.type.getOpcode(Opcodes.ILOAD);
        methodVisitor.visitVarInsn(op, var.localIndex);
    }

    @Override
    public void visitIfStmtNode(IfStmtNode node) {
        Label label = new Label();
        labelStack.push(label);
        visit(node.condition);
        methodVisitor.visitJumpInsn(
            Opcodes.IFEQ,
            labelStack.pop()
        );
        visit(node.statement);
        methodVisitor.visitLabel(label);
    }

    @Override
    public void visitWhileStmtNode(WhileStmtNode node) {
        Label toEnd = new Label();
        labelStack.push(toEnd);

        Label jumpBack = new Label();
        methodVisitor.visitLabel(jumpBack);
        
        visit(node.condition);
        visit(node.statement);
        methodVisitor.visitJumpInsn(Opcodes.GOTO, jumpBack);

        methodVisitor.visitLabel(toEnd);
    }

    @Override
    public void visitArrAccessExprNode(ArrAccessExprNode node) {
        visit(node.identifer); 
        visit(node.index);
        methodVisitor.visitInsn(node.exprType.getOpcode(Opcodes.IALOAD));
    }

    @Override
    public void visitArrayInitializer(ArrayInitializerNode node) {
        for (ExpressionNode size : node.arraySizes) {
            visit(size);
        }
        if (node.dims > 1) {
            multiDimArrayInitializer(node);
        } else {
            singleDimArrayInitializer(node);
        }
    }

    private void multiDimArrayInitializer(ArrayInitializerNode node) {
        if (node.arraySizes.size() > 1) {
            System.out.println(node);
            methodVisitor.visitMultiANewArrayInsn(node.exprType.toString(), node.arraySizes.size());
        } else {
            methodVisitor.visitTypeInsn(Opcodes.ANEWARRAY, "[" + node.exprType.getElementType());
        }
    }

    private void singleDimArrayInitializer(ArrayInitializerNode node) {
        if (node.type.getElementType() == Type.INT_TYPE) {
            methodVisitor.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_INT);
        } else if (node.type.getElementType() == Type.FLOAT_TYPE) {
            methodVisitor.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_FLOAT);
        } else {
            throw new UnsupportedOperationException("inside vis arr init in method gen vis");
        }
    }
}
