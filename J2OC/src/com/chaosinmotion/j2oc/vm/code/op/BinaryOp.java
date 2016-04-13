/*  BinaryOp.java
 *
 *  Created on Feb 6, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.code.op;

import com.chaosinmotion.j2oc.vm.data.DataType;

public class BinaryOp extends Op
{
    public static final int ADD = 1;
    public static final int SUB = 2;
    public static final int MUL = 3;
    public static final int DIV = 4;
    public static final int MOD = 5;
    
    public static final int SHIFTLEFT = 6;
    public static final int SHIFTRIGHT = 7;
    public static final int UNSIGNEDSHIFTRIGHT = 8;
    public static final int AND = 9;
    public static final int OR = 10;
    public static final int XOR = 11;
    
    public static final int CMP = 12;
    public static final int CMPG = 13;
    public static final int CMPL = 14;
    
    private int operator;
    private DataType type;
    private Op left;
    private Op right;

    public BinaryOp(int o, int ty, Op lhs, Op rhs)
    {
        operator = o;
        type = new DataType(ty);
        left = lhs;
        right = rhs;
    }

    public int getOperator()
    {
        return operator;
    }

    public DataType getType()
    {
        return type;
    }

    public Op getLeft()
    {
        return left;
    }

    public Op getRight()
    {
        return right;
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


