/*  IntegerConstant.java
 *
 *  Created on Feb 5, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.data.constants;


public class UTF8Constant extends Constant
{
    private String value;

    public UTF8Constant(int tag, String v)
    {
        super(tag);
        value = v;
    }
    
    public String getValue()
    {
        return value;
    }

    @Override
    public void resolveConstant(Constant[] pool)
    {
    }
}


