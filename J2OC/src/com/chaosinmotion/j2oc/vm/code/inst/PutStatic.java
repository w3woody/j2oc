/*  PutStatic.java
 *
 *  Created on Feb 6, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.code.inst;

import com.chaosinmotion.j2oc.vm.code.op.Op;
import com.chaosinmotion.j2oc.vm.data.constants.Constant;
import com.chaosinmotion.j2oc.vm.data.constants.FMIConstant;

public class PutStatic extends Instruction
{
    private Op val;
    private FMIConstant field;

    public PutStatic(Op tmpOp, Constant f)
    {
        val = tmpOp;
        field = (FMIConstant)f;
    }

    public Op getVal()
    {
        return val;
    }

    public FMIConstant getField()
    {
        return field;
    }

}


