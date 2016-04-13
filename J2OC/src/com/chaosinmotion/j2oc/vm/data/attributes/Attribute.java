/*  Attribute.java
 *
 *  Created on Feb 5, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.data.attributes;

import java.io.IOException;

import com.chaosinmotion.j2oc.vm.data.constants.Constant;

/**
 * Base attribute object represents an attribute in the system
 */
public abstract class Attribute
{
    private String attributeName;
    
    public Attribute(String n)
    {
        attributeName = n;
    }
    
    public String getAttributeName()
    {
        return attributeName;
    }

    public abstract void resolveConstant(Constant[] constantPool) throws IOException;
}


