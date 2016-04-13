/*  ArrayLength.java
 *
 *  Created on Feb 6, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.code.op;

import com.chaosinmotion.j2oc.vm.data.DataType;


public class ArrayLength extends Op
{
    private Op op;
    
    public ArrayLength(Op o)
    {
        op = o;
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
        return DataType.T_INT;
    }
}


