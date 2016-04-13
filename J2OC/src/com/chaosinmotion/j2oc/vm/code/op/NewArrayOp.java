/*  NewArrayOp.java
 *
 *  Created on Feb 6, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.code.op;

import com.chaosinmotion.j2oc.vm.data.DataType;

public class NewArrayOp extends Op
{
    private DataType type;
    private Op op;

    public NewArrayOp(int tmp, Op tmpOp)
    {
        type = new DataType(tmp,1);
        op = tmpOp;
    }
    
    @Override
    public boolean isWide()
    {
        return false;
    }

    public DataType getType()
    {
        return type;
    }

    public Op getOp()
    {
        return op;
    }

    @Override
    public int getPrimitiveType()
    {
        return DataType.T_ADDR;
    }
}


