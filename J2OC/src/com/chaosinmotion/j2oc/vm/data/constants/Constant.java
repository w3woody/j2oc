/*  Constant.java
 *
 *  Created on Feb 5, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.data.constants;

import java.io.IOException;

/**
 * The root class of the constant class hierarchy. This is the base class of all
 * declared constants
 */
public abstract class Constant
{
    public static final int CONSTANT_Class = 7;
    public static final int CONSTANT_Fieldref = 9;
    public static final int CONSTANT_Methodref = 10;
    public static final int CONSTANT_InterfaceMethodref = 11;
    public static final int CONSTANT_String = 8;
    public static final int CONSTANT_Integer = 3;
    public static final int CONSTANT_Float = 4;
    public static final int CONSTANT_Long = 5;
    public static final int CONSTANT_Double = 6;
    public static final int CONSTANT_NameAndType = 12;
    public static final int CONSTANT_Utf8 = 1;
    
    private int tag;
    
    public Constant(int t)
    {
        tag = t;
    }
    
    public int getTag()
    {
        return tag;
    }
    
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + tag;
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Constant other = (Constant)obj;
        if (tag != other.tag) return false;
        return true;
    }

    /**
     * Called to resolve a constant in the constant pool.
     * @param pool
     * @throws IOException 
     */
    public abstract void resolveConstant(Constant[] pool) throws IOException;
}


