/*  ThrowInstruction.java
 *
 *  Created on Feb 6, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.code.inst;

import com.chaosinmotion.j2oc.vm.code.op.Op;

public class ThrowInstruction extends Instruction
{
    private Op op;

    public ThrowInstruction(Op pop)
    {
        op = pop;
    }

    public Op getOp()
    {
        return op;
    }
}


