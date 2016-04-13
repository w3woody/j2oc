/*  DeprecatedAttribute.java
 *
 *  Created on Feb 6, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.data.attributes;

import java.io.IOException;

import com.chaosinmotion.j2oc.vm.data.constants.Constant;

/**
 * Represents the deprecated attribute. There are no parameters
 */
public class DeprecatedAttribute extends Attribute
{
    public DeprecatedAttribute(String n)
    {
        super(n);
    }

    @Override
    public void resolveConstant(Constant[] constantPool) throws IOException
    {
    }
}


