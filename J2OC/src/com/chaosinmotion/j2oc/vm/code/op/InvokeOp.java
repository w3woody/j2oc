/*  InvokeOp.java
 *
 *  Created on Feb 6, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.code.op;

import java.util.LinkedList;

import com.chaosinmotion.j2oc.vm.data.constants.FMIConstant;

public class InvokeOp extends Op
{
    private int type;
    private Op pThis;
    private FMIConstant constant;
    private LinkedList<Op> args;

    public InvokeOp(int t, Op th, FMIConstant c, LinkedList<Op> a)
    {
        type = t;
        pThis = th;
        constant = c;
        args = a;
    }

    public int getType()
    {
        return type;
    }

    public Op getThis()
    {
        return pThis;
    }

    public FMIConstant getConstant()
    {
        return constant;
    }

    public LinkedList<Op> getArgs()
    {
        return args;
    }

    @Override
    public boolean isWide()
    {
        return constant.getDescriptor().getRet().isWide();
    }

    @Override
    public int getPrimitiveType()
    {
        return constant.getDescriptor().getRet().getPrimitiveType();
    }
}


