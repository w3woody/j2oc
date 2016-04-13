/*  StoreTemporaryVariable.java
 *
 *  Created on Feb 6, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.code.inst;

import com.chaosinmotion.j2oc.vm.code.op.Op;

/**
 * Generate and store into a temporary variable. Created when we had to explicitly
 * explode an operation because of a jump into the middle
 */
public class StoreTemporaryVariable extends Instruction
{
    private int type;
    private int var;
    private boolean stmp;
    private Op op;

    public StoreTemporaryVariable(int t, int v, boolean s, Op o)
    {
        type = t;
        var = v;
        stmp = s;
        op = o;
    }

    public int getType()
    {
        return type;
    }

    public int getVar()
    {
        return var;
    }

    public Op getOp()
    {
        return op;
    }
    
    public boolean isSTmp()
    {
        return stmp;
    }
}


