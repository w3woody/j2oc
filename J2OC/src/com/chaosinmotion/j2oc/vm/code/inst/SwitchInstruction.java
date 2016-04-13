/*  SwitchInstruction.java
 *
 *  Created on Feb 6, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.code.inst;

import java.util.ArrayList;

import com.chaosinmotion.j2oc.vm.code.op.Op;

public class SwitchInstruction extends Instruction
{
    private int defaultByte;
    private ArrayList<MatchPair> pairs;
    private Op op;
    
    public static class MatchPair
    {
        private int match;
        private int pc;

        public MatchPair(int m, int p)
        {
            match = m;
            pc = p;
        }

        public int getMatch()
        {
            return match;
        }

        public int getPc()
        {
            return pc;
        }
    }

    public SwitchInstruction(int def, Op o)
    {
        defaultByte = def;
        pairs = new ArrayList<MatchPair>();
        op = o;
    }
    
    public Op getOp()
    {
        return op;
    }

    public void addPair(int m, int pc)
    {
        pairs.add(new MatchPair(m,pc));
    }

    public int getDefaultByte()
    {
        return defaultByte;
    }

    public ArrayList<MatchPair> getPairs()
    {
        return pairs;
    }
}


