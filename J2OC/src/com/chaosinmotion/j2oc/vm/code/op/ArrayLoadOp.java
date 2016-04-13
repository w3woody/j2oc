/*  ArrayLoadOp.java
 *
 *  Created on Feb 6, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.code.op;

import com.chaosinmotion.j2oc.vm.data.DataType;

public class ArrayLoadOp extends Op
{
    private Op arrayOp;
    private Op indexOp;
    private DataType type;

    public ArrayLoadOp(Op ix, Op a, int tAddr)
    {
        type = new DataType(tAddr);
        arrayOp = a;
        indexOp = ix;
    }

    @Override
    public boolean isWide()
    {
        return type.isWide();
    }

    public Op getArrayOp()
    {
        return arrayOp;
    }

    public Op getIndexOp()
    {
        return indexOp;
    }

    public DataType getType()
    {
        return type;
    }

    @Override
    public int getPrimitiveType()
    {
        return type.getPrimitiveType();
    }
}


