/*  IfCompare.java
 *
 *  Created on Feb 6, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.code.inst;

import com.chaosinmotion.j2oc.vm.code.op.Op;

public class IfCompare extends Instruction
{
    public static final int EQ = 1;
    public static final int NE = 2;
    public static final int LT = 3;
    public static final int LE = 4;
    public static final int GT = 5;
    public static final int GE = 6;
    public static final int NULL = 7;
    public static final int NONNULL = 8;
    
    private int compare;
    private Op op;
    private int pc;

    public IfCompare(int c, Op pop, int i)
    {
        compare = c;
        op = pop;
        pc = i;
    }

    public Op getOp()
    {
        return op;
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


