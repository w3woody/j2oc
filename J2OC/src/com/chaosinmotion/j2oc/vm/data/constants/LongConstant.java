/*  IntegerConstant.java
 *
 *  Created on Feb 5, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.data.constants;


public class LongConstant extends Constant
{
    private long value;

    public LongConstant(int tag, long v)
    {
        super(tag);
        value = v;
    }
    
    public long getValue()
    {
        return value;
    }

    @Override
    public void resolveConstant(Constant[] pool)
    {
    }
}


