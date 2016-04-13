/*  ExceptionsAttribute.java
 *
 *  Created on Feb 6, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.data.attributes;

import java.io.IOException;

import com.chaosinmotion.j2oc.vm.data.constants.ClassConstant;
import com.chaosinmotion.j2oc.vm.data.constants.Constant;

/**
 * The exceptions attribute; indicates checked exceptions that can be thrown by the
 * method associated with this
 */
public class ExceptionsAttribute extends Attribute
{
    private int[] exceptionsIndexTable;
    private String[] exceptions;
    
    public ExceptionsAttribute(String n, int[] etable)
    {
        super(n);
        exceptionsIndexTable = etable;
    }

    public int[] getExceptionsIndexTable()
    {
        return exceptionsIndexTable;
    }
    
    public String[] getExceptions()
    {
        return exceptions;
    }

    @Override
    public void resolveConstant(Constant[] constantPool) throws IOException
    {
        exceptions = new String[exceptionsIndexTable.length];
        for (int i = 0; i < exceptionsIndexTable.length; ++i) {
            int ix = exceptionsIndexTable[i];
            exceptions[i] = ((ClassConstant)constantPool[ix]).getClassName();
        }
    }
}


