/*  EvalInstruction.java
 *
 *  Created on Feb 6, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.code.inst;

import com.chaosinmotion.j2oc.vm.code.op.Op;

/**
 * Evalulates the operator and throws away the results
 */
public class EvalInstruction extends Instruction
{
    private Op op;

    public EvalInstruction(Op pop)
    {
        op = pop;
    }
    
    public Op getOp()
    {
        return op;
    }
}


