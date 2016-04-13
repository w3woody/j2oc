/*  Goto.java
 *
 *  Created on Feb 6, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.code.inst;

public class Goto extends Instruction
{
    private int pc;
    
    public Goto(int i)
    {
        pc = i;
    }

    public int getPc()
    {
        return pc;
    }
}


