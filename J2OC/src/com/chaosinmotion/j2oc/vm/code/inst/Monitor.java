/*  Monitor.java
 *
 *  Created on Feb 15, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.code.inst;

import com.chaosinmotion.j2oc.vm.code.op.Op;

public class Monitor extends Instruction
{
    Op val;
    boolean enterFlag;
    
    public Monitor(boolean ef, Op tmpOp)
    {
        enterFlag = ef;
        val = tmpOp;
    }
    
    public boolean isEnterFlag()
    {
        return enterFlag;
    }
    
    public Op getOp()
    {
        return val;
    }
}


