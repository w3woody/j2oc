/*  PutField.java
 *
 *  Created on Feb 6, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.code.inst;

import com.chaosinmotion.j2oc.vm.code.op.Op;
import com.chaosinmotion.j2oc.vm.data.constants.Constant;
import com.chaosinmotion.j2oc.vm.data.constants.FMIConstant;

public class PutField extends Instruction
{
    private Op pThis;
    private Op val;
    private FMIConstant field;

    public PutField(Op tmpOp2, Op tmpOp, Constant f)
    {
        pThis = tmpOp2;
        val = tmpOp;
        field = (FMIConstant)f;
    }

    public Op getThis()
    {
        return pThis;
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


