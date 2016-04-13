/*  GetStaticOp.java
 *
 *  Created on Feb 6, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.code.op;

import com.chaosinmotion.j2oc.vm.data.constants.Constant;
import com.chaosinmotion.j2oc.vm.data.constants.FMIConstant;

public class GetStaticOp extends Op
{
    private FMIConstant field;

    public GetStaticOp(Constant constant)
    {
        field = (FMIConstant)constant;
    }

    @Override
    public boolean isWide()
    {
        return field.getDescriptor().getRet().isWide();
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


