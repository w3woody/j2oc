/*  ConstantAttribute.java
 *
 *  Created on Feb 5, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.data.attributes;

import java.io.IOException;

import com.chaosinmotion.j2oc.vm.data.constants.Constant;

/**
 * Constant attribute for this object; if associated with a field this represents the
 * value of that field if the field is a constant.
 */
public class ConstantAttribute extends Attribute
{
    private int constantIndex;
    private Constant constant;

    public ConstantAttribute(String n, int c)
    {
        super(n);
        constantIndex = c;
    }

    public int getConstantIndex()
    {
        return constantIndex;
    }
    
    public Constant getConstant()
    {
        return constant;
    }

    @Override
    public void resolveConstant(Constant[] constantPool) throws IOException
    {
        constant = constantPool[constantIndex];
    }
}


