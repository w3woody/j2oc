/*  VariableOp.java
 *
 *  Created on Feb 6, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.code.op;

import com.chaosinmotion.j2oc.vm.data.DataType;

public class VariableOp extends Op
{
    private DataType type;
    private int var;

    public VariableOp(int tAddr, int v)
    {
        type = new DataType(tAddr);
        var = v;
    }

    @Override
    public boolean isWide()
    {
        return type.isWide();
    }

    public DataType getType()
    {
        return type;
    }

    public int getVar()
    {
        return var;
    }

    @Override
    public int getPrimitiveType()
    {
        return type.getPrimitiveType();
    }
}


