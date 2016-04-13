/*  CodeIterator.java
 *
 *  Created on Feb 12, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.code;

import com.chaosinmotion.j2oc.vm.code.inst.ArrayStore;
import com.chaosinmotion.j2oc.vm.code.inst.EvalInstruction;
import com.chaosinmotion.j2oc.vm.code.inst.Goto;
import com.chaosinmotion.j2oc.vm.code.inst.IfBinaryCompare;
import com.chaosinmotion.j2oc.vm.code.inst.IfCompare;
import com.chaosinmotion.j2oc.vm.code.inst.Increment;
import com.chaosinmotion.j2oc.vm.code.inst.Instruction;
import com.chaosinmotion.j2oc.vm.code.inst.Invoke;
import com.chaosinmotion.j2oc.vm.code.inst.PutField;
import com.chaosinmotion.j2oc.vm.code.inst.PutStatic;
import com.chaosinmotion.j2oc.vm.code.inst.Return;
import com.chaosinmotion.j2oc.vm.code.inst.ReturnValue;
import com.chaosinmotion.j2oc.vm.code.inst.StoreTemporaryVariable;
import com.chaosinmotion.j2oc.vm.code.inst.StoreVariable;
import com.chaosinmotion.j2oc.vm.code.inst.SwitchInstruction;
import com.chaosinmotion.j2oc.vm.code.inst.TableInstruction;
import com.chaosinmotion.j2oc.vm.code.inst.ThrowInstruction;
import com.chaosinmotion.j2oc.vm.code.op.ArrayLength;
import com.chaosinmotion.j2oc.vm.code.op.ArrayLoadOp;
import com.chaosinmotion.j2oc.vm.code.op.BinaryOp;
import com.chaosinmotion.j2oc.vm.code.op.CheckCast;
import com.chaosinmotion.j2oc.vm.code.op.ConstantOp;
import com.chaosinmotion.j2oc.vm.code.op.GetFieldOp;
import com.chaosinmotion.j2oc.vm.code.op.GetStaticOp;
import com.chaosinmotion.j2oc.vm.code.op.InstanceOf;
import com.chaosinmotion.j2oc.vm.code.op.InvokeOp;
import com.chaosinmotion.j2oc.vm.code.op.MultiArrayOp;
import com.chaosinmotion.j2oc.vm.code.op.NewArrayOp;
import com.chaosinmotion.j2oc.vm.code.op.NewOp;
import com.chaosinmotion.j2oc.vm.code.op.NewRefArrayOp;
import com.chaosinmotion.j2oc.vm.code.op.Op;
import com.chaosinmotion.j2oc.vm.code.op.RecallTemporaryVariableOp;
import com.chaosinmotion.j2oc.vm.code.op.UnaryOp;
import com.chaosinmotion.j2oc.vm.code.op.VariableOp;

/**
 * provides the machinery for calling a function on each instruction or each operator
 * within a function
 */
public class CodeSearch
{
    private OpLambda opLambda;
    
    /**
     * The callback function (lambda function) invoked on each operator
     */
    public interface OpLambda
    {
        /**
         * Invoke the operator on each operation
         * @param op
         * @return Return true if we should halt operation.
         */
        boolean invokeOnOperator(Op op);
    }
    
    /**
     * Given the operator lambda, this iterates through and invokes this on all of
     * the operators in the system.
     * @param ol
     */
    public void doLambda(Code code, OpLambda ol)
    {
        opLambda = ol;
        for (Code.CodeSeg cs: code.getInst()) {
            if (cs.getInst() != null) {
                for (Instruction inst: cs.getInst()) {
                    if (hasFunctionCall(inst)) return;
                }
            }
        }
    }
    
    /**
     * Do the lambda operator on the instruction 
     * @param inst
     * @param ol
     */
    public void doLambda(Instruction inst, OpLambda ol)
    {
        opLambda = ol;
        hasFunctionCall(inst);
    }
    
    /**
     * Operator lambda
     * @param op
     * @param ol
     */
    public void doLambda(Op op, OpLambda ol)
    {
        opLambda = ol;
        hasFunctionCall(op);
    }
    
    private boolean hasFunctionCall(Op op)
    {
        if (op == null) return false;
        if (opLambda.invokeOnOperator(op)) return true;

        if (op instanceof ArrayLength) {
            if (hasFunctionCall(((ArrayLength)op).getOp())) return true;
            
        } else if (op instanceof ArrayLoadOp) {
            if (hasFunctionCall(((ArrayLoadOp)op).getArrayOp())) return true;
            if (hasFunctionCall(((ArrayLoadOp)op).getIndexOp())) return true;
            
        } else if (op instanceof BinaryOp) {
            if (hasFunctionCall(((BinaryOp)op).getLeft())) return true;
            if (hasFunctionCall(((BinaryOp)op).getRight())) return true;
                
        } else if (op instanceof CheckCast) {
            if (hasFunctionCall(((CheckCast)op).getOp())) return true;
            
        } else if (op instanceof ConstantOp) {
            // root

        } else if (op instanceof GetFieldOp) {
            if (hasFunctionCall(((GetFieldOp)op).getThis())) return true;
            
        } else if (op instanceof GetStaticOp) {
            // root
            
        } else if (op instanceof InstanceOf) {
            if (hasFunctionCall(((InstanceOf)op).getOp())) return true;
            
        } else if (op instanceof InvokeOp) {
            if (hasFunctionCall(((InvokeOp)op).getThis())) return true;
            for (Op argOp: (((InvokeOp)op).getArgs())) {
                if (hasFunctionCall(argOp)) return true;
            }
            
        } else if (op instanceof MultiArrayOp) {
            for (Op argOp: (((MultiArrayOp)op).getArgs())) {
                if (hasFunctionCall(argOp)) return true;
            }
            
        } else if (op instanceof NewArrayOp) {
            if (hasFunctionCall(((NewArrayOp)op).getOp())) return true;
            
        } else if (op instanceof NewOp) {
            // root
            
        } else if (op instanceof NewRefArrayOp) {
            if (hasFunctionCall(((NewRefArrayOp)op).getOp())) return true;
            
        } else if (op instanceof RecallTemporaryVariableOp) {
            // root
            
        } else if (op instanceof UnaryOp) {
            if (hasFunctionCall(((UnaryOp)op).getOp())) return true;
            
        } else if (op instanceof VariableOp) {
            // root
            
        }
        return false;
    }
    
    private boolean hasFunctionCall(Instruction inst)
    {
        if (inst instanceof ArrayStore) {
            if (hasFunctionCall(((ArrayStore)inst).getArrayRef())) return true;
            if (hasFunctionCall(((ArrayStore)inst).getIndex())) return true;
            if (hasFunctionCall(((ArrayStore)inst).getValue())) return true;
            
        } else if (inst instanceof EvalInstruction) {
            if (hasFunctionCall(((EvalInstruction)inst).getOp())) return true;
            
        } else if (inst instanceof Goto) {
            
        } else if (inst instanceof IfBinaryCompare) {
            if (hasFunctionCall(((IfBinaryCompare)inst).getLeft())) return true;
            if (hasFunctionCall(((IfBinaryCompare)inst).getRight())) return true;
            
        } else if (inst instanceof IfCompare) {
            if (hasFunctionCall(((IfCompare)inst).getOp())) return true;
            
        } else if (inst instanceof Increment) {
            
        } else if (inst instanceof Invoke) {
            if (hasFunctionCall(((Invoke)inst).getThis())) return true;
            for (Op op: ((Invoke)inst).getArgs()) {
                if (hasFunctionCall(op)) return true;
            }
            
        } else if (inst instanceof PutField) {
            if (hasFunctionCall(((PutField)inst).getThis())) return true;
            if (hasFunctionCall(((PutField)inst).getVal())) return true;
            
        } else if (inst instanceof PutStatic) {
            if (hasFunctionCall(((PutStatic)inst).getVal())) return true;
            
        } else if (inst instanceof Return) {

        } else if (inst instanceof ReturnValue) {
            if (hasFunctionCall(((ReturnValue)inst).getOp())) return true;
            
        } else if (inst instanceof StoreTemporaryVariable) {
            if (hasFunctionCall(((StoreTemporaryVariable)inst).getOp())) return true;

        } else if (inst instanceof StoreVariable) {
            if (hasFunctionCall(((StoreVariable)inst).getVal())) return true;
            
        } else if (inst instanceof SwitchInstruction) {
            if (hasFunctionCall(((SwitchInstruction)inst).getOp())) return true;
            
        } else if (inst instanceof TableInstruction) {
            if (hasFunctionCall(((TableInstruction)inst).getOp())) return true;
            
        } else if (inst instanceof ThrowInstruction) {
            if (hasFunctionCall(((ThrowInstruction)inst).getOp())) return true;
            
        }
        return false;
    }
    

}


