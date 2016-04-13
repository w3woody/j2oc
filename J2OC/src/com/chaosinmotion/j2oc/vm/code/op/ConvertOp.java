/*  ConvertOp.java
 *
 *  Created on Feb 6, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.code.op;

import com.chaosinmotion.j2oc.vm.data.DataType;

public class ConvertOp extends Op
{
    private DataType type;
    private Op op;
    
    public ConvertOp(int t, Op o)
    {
        type = new DataType(t);
        op = o;
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

    public Op getOp()
    {
        return op;
    }

    @Override
    public int getPrimitiveType()
    {
        return type.getPrimitiveType();
    }
}


