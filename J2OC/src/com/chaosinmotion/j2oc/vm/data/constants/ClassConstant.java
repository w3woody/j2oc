/*  ClassConstant.java
 *
 *  Created on Feb 5, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.data.constants;

/**
 * Represents a class constant.
 */
public class ClassConstant extends Constant
{
    private int nameIndex;
    private String className;

    public ClassConstant(int tag, int nindex)
    {
        super(tag);
        
        nameIndex = nindex;
    }

    /**
     * Get the index of the name object holding this class name
     * @return
     */
    public int getNameIndex()
    {
        return nameIndex;
    }
    
    /**
     * Returns the class name itself
     * @return
     */
    public String getClassName()
    {
        return className;
    }

    @Override
    public void resolveConstant(Constant[] pool)
    {
        if ((className == null) && (nameIndex != 0)) {
            className = ((UTF8Constant)pool[nameIndex]).getValue();
        }
    }
}


