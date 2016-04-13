/*  SourceFileAttribute.java
 *
 *  Created on Feb 6, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.data.attributes;

import java.io.IOException;

import com.chaosinmotion.j2oc.vm.data.constants.Constant;
import com.chaosinmotion.j2oc.vm.data.constants.UTF8Constant;

/**
 * Names the source file for this
 */
public class SourceFileAttribute extends Attribute
{
    private int sourcefileIndex;
    private String sourceFileName;

    public SourceFileAttribute(String n, int src)
    {
        super(n);
        sourcefileIndex = src;
    }

    public int getSourcefileIndex()
    {
        return sourcefileIndex;
    }
    
    public String getSourceFileName()
    {
        return sourceFileName;
    }

    @Override
    public void resolveConstant(Constant[] constantPool) throws IOException
    {
        sourceFileName = ((UTF8Constant)constantPool[sourcefileIndex]).getValue();
    }
}


