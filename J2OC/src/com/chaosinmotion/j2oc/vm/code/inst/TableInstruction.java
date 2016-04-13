/*  TableInstruction.java
 *
 *  Created on Feb 6, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.code.inst;

import java.util.ArrayList;

import com.chaosinmotion.j2oc.vm.code.op.Op;

public class TableInstruction extends Instruction
{
    private int defaultByte;
    private int lowByte;
    private int highByte;
    private ArrayList<Integer> pc;
    private Op op;

    public TableInstruction(int d, int l, int h, Op o)
    {
        defaultByte = d;
        lowByte = l;
        highByte = h;
        pc = new ArrayList<Integer>();
        op = o;
    }
    
    public Op getOp()
    {
        return op;
    }

    public void add(int readInt)
    {
        pc.add(readInt);
    }

    public int getDefaultByte()
    {
        return defaultByte;
    }

    public int getLowByte()
    {
        return lowByte;
    }

    public int getHighByte()
    {
        return highByte;
    }

    public ArrayList<Integer> getPc()
    {
        return pc;
    }
}


