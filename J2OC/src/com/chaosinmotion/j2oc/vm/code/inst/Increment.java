/*  Increment.java
 *
 *  Created on Feb 6, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.code.inst;

public class Increment extends Instruction
{
    private int val;
    private int var;

    public Increment(int v, int i)
    {
        var = v;
        val = i;
    }

    public int getVal()
    {
        return val;
    }

    public int getVar()
    {
        return var;
    }
}


