/*  GetFieldOp.java
 *
 *  Created on Feb 6, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.code.op;

import com.chaosinmotion.j2oc.vm.data.constants.Constant;
import com.chaosinmotion.j2oc.vm.data.constants.FMIConstant;

public class GetFieldOp extends Op
{
    private Op pThis;
    private FMIConstant field;

    public GetFieldOp(Op tmpOp, Constant constant)
    {
        pThis = tmpOp;
        field = (FMIConstant)constant;
    }

    @Override
    public boolean isWide()
    {
        return field.getDescriptor().getRet().isWide();
    }

    public Op getThis()
    {
        return pThis;
    }

    public FMIConstant getField()
    {
        return field;
    }

    @Override
    public int getPrimitiveType()
    {
        return field.getDescriptor().getRet().getPrimitiveType();
    }
}


