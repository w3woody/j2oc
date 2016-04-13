/*  IntegerConstant.java
 *
 *  Created on Feb 5, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.data.constants;


public class IntegerConstant extends Constant
{
    private int value;

    public IntegerConstant(int tag, int v)
    {
        super(tag);
        value = v;
    }
    
    public int getValue()
    {
        return value;
    }

    @Override
    public void resolveConstant(Constant[] pool)
    {
    }
}


