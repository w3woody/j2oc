/*  InvokeInterface.java
 *
 *  Created on Feb 6, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.code.inst;

import java.util.LinkedList;

import com.chaosinmotion.j2oc.vm.code.op.Op;
import com.chaosinmotion.j2oc.vm.data.constants.FMIConstant;

public class Invoke extends Instruction
{
    public static final int INTERFACE = 1;
    public static final int SPECIAL = 2;
    public static final int STATIC = 3;
    public static final int VIRTUAL = 4;
    private int type;
    private Op pThis;
    private FMIConstant constant;
    private LinkedList<Op> args;

    public Invoke(int t, Op th, FMIConstant c, LinkedList<Op> a)
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
}


