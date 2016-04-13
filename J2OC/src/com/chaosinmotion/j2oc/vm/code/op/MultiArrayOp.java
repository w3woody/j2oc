/*  MultiArrayOp.java
 *
 *  Created on Feb 6, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.code.op;

import java.util.LinkedList;

import com.chaosinmotion.j2oc.vm.data.DataType;
import com.chaosinmotion.j2oc.vm.data.constants.ClassConstant;
import com.chaosinmotion.j2oc.vm.data.constants.Constant;

public class MultiArrayOp extends Op
{
    private ClassConstant constant;
    private Op[] args;

    public MultiArrayOp(Constant c, LinkedList<Op> a)
    {
        constant = (ClassConstant)c;
        args = a.toArray(new Op[a.size()]);
    }

    @Override
    public boolean isWide()
    {
        return false;
    }

    public ClassConstant getConstant()
    {
        return constant;
    }

    public Op[] getArgs()
    {
        return args;
    }

    @Override
    public int getPrimitiveType()
    {
        return DataType.T_ADDR;
    }
}


