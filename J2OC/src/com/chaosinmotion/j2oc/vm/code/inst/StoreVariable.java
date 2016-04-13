/*  StoreVariable.java
 *
 *  Created on Feb 6, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.code.inst;

import com.chaosinmotion.j2oc.vm.code.op.Op;
import com.chaosinmotion.j2oc.vm.data.DataType;

public class StoreVariable extends Instruction
{
    DataType type;
    int var;
    Op val;

    public StoreVariable(int tAddr, int tmp, Op tmpOp)
    {
        type = new DataType(tAddr);
        var = tmp;
        val = tmpOp;
    }

    public DataType getType()
    {
        return type;
    }

    public int getVar()
    {
        return var;
    }

    public Op getVal()
    {
        return val;
    }
}


