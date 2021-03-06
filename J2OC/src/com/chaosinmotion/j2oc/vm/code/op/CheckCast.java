/*  CheckCast.java
 *
 *  Created on Feb 6, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.code.op;

import com.chaosinmotion.j2oc.vm.data.constants.Constant;

public class CheckCast extends Op
{
    private Constant constant;
    private Op op;

    public CheckCast(Op o, Constant c)
    {
        op = o;
        constant = c;
    }
    
    public Constant getConstant()
    {
        return constant;
    }

    public Op getOp()
    {
        return op;
    }

    @Override
    public boolean isWide()
    {
        return false;
    }
    
    @Override
    public int getPrimitiveType()
    {
        return op.getPrimitiveType();
    }
}


