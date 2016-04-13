/*  GenericAttribute.java
 *
 *  Created on Feb 5, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.data.attributes;

import java.io.IOException;

import com.chaosinmotion.j2oc.vm.data.constants.Constant;

/**
 * Represents a generic attribute; a legal attribute which was not understood by teh
 * J2OC class parser
 */
public class GenericAttribute extends Attribute
{
    private byte[] data;

    public GenericAttribute(String n, byte[] d)
    {
        super(n);
        data = d;
    }

    public byte[] getData()
    {
        return data;
    }

    @Override
    public void resolveConstant(Constant[] constantPool) throws IOException
    {
    }
}


