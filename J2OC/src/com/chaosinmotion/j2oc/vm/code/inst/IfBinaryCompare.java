/*  IfCompare.java
 *
 *  Created on Feb 6, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.code.inst;

import com.chaosinmotion.j2oc.vm.code.op.Op;

public class IfBinaryCompare extends Instruction
{
    public static final int EQ = 1;
    public static final int NE = 2;
    public static final int LT = 3;
    public static final int LE = 4;
    public static final int GT = 5;
    public static final int GE = 6;
    
    private int compare;
    private Op left;
    private Op right;
    private int pc;

    public IfBinaryCompare(int c, Op lhs, Op rhs, int i)
    {
        compare = c;
        left = lhs;
        right = rhs;
        pc = i;
    }


    public Op getLeft()
    {
        return left;
    }


    public Op getRight()
    {
        return right;
    }


    public int getPc()
    {
        return pc;
    }

    public int getCompare()
    {
        return compare;
    }
}


