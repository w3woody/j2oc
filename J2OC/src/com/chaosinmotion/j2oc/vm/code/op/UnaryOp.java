/*  BinaryOp.java
 *
 *  Created on Feb 6, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.code.op;

import com.chaosinmotion.j2oc.vm.data.DataType;

public class UnaryOp extends Op
{
    public static final int NEGATE = 1;
    
    private int operator;
    private DataType type;
    private Op op;

    public UnaryOp(int o, int ty, Op v)
    {
        operator = o;
        type = new DataType(ty);
        op = v;
    }

    public int getOperator()
    {
        return operator;
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
    public boolean isWide()
    {
        return type.isWide();
    }

    @Override
    public int getPrimitiveType()
    {
        return type.getPrimitiveType();
    }
}


