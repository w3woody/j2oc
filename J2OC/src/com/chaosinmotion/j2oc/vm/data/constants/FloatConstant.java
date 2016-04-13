/*  IntegerConstant.java
 *
 *  Created on Feb 5, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.data.constants;


public class FloatConstant extends Constant
{
    private float value;

    public FloatConstant(int tag, float v)
    {
        super(tag);
        value = v;
    }
    
    public float getValue()
    {
        return value;
    }

    @Override
    public void resolveConstant(Constant[] pool)
    {
    }
}


