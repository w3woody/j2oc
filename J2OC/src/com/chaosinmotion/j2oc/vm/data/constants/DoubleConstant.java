/*  IntegerConstant.java
 *
 *  Created on Feb 5, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.data.constants;


public class DoubleConstant extends Constant
{
    private double value;

    public DoubleConstant(int tag, double v)
    {
        super(tag);
        value = v;
    }
    
    public double getValue()
    {
        return value;
    }

    @Override
    public void resolveConstant(Constant[] pool)
    {
    }
}


