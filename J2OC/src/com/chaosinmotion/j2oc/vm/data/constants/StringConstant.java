/*  StringConstant.java
 *
 *  Created on Feb 5, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.data.constants;

/**
 * Read and store string constant
 */
public class StringConstant extends Constant
{
    private int stringIndex;
    private String string;
    
    public StringConstant(int tag, int s)
    {
        super(tag);
        stringIndex = s;
    }

    public int getStringIndex()
    {
        return stringIndex;
    }
    
    public String getString()
    {
        return string;
    }
    
    @Override
    public void resolveConstant(Constant[] pool)
    {
        string = ((UTF8Constant)pool[stringIndex]).getValue();
    }
}


