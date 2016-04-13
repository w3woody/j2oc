/*  ReturnValue.java
 *
 *  Created on Feb 6, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.code.inst;

import com.chaosinmotion.j2oc.vm.code.op.Op;

public class ReturnValue extends Instruction
{
    private Op op;

    public ReturnValue(Op pop)
    {
        op = pop;
    }

    public Op getOp()
    {
        return op;
    }
}


