/*  FMIConstant.java
 *
 *  Created on Feb 5, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.data.constants;

import java.io.IOException;

import com.chaosinmotion.j2oc.vm.data.MethodDescriptor;

/**
 * Represents a field, method or interface declaration
 */
public class FMIConstant extends Constant
{
    private int classIndex;
    private int nameAndTypeIndex;
    
    private ClassConstant classConst;
    private NameAndTypeConstant nameConst;
    
    private String className;
    private String fieldName;
    private MethodDescriptor descriptor;

    public FMIConstant(int tag, int c, int n)
    {
        super(tag);
        
        classIndex = c;
        nameAndTypeIndex = n;
    }

    public int getClassIndex()
    {
        return classIndex;
    }

    public int getNameAndTypeIndex()
    {
        return nameAndTypeIndex;
    }
    
    /**
     * The class name of the class containing this field
     * @return
     */
    public String getClassName()
    {
        return className;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public MethodDescriptor getDescriptor()
    {
        return descriptor;
    }

    public ClassConstant getClassConst()
    {
        return classConst;
    }

    public NameAndTypeConstant getNameConst()
    {
        return nameConst;
    }

    @Override
    public void resolveConstant(Constant[] pool) throws IOException
    {
        classConst = (ClassConstant)pool[classIndex];
        classConst.resolveConstant(pool);
        
        nameConst = (NameAndTypeConstant)pool[nameAndTypeIndex];
        nameConst.resolveConstant(pool);
        
        className = classConst.getClassName();
        fieldName = nameConst.getName();
        descriptor = nameConst.getDescriptor();
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((className == null) ? 0 : className.hashCode());
        result = prime * result + ((descriptor == null) ? 0 : descriptor.hashCode());
        result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        FMIConstant other = (FMIConstant)obj;
        if (className == null) {
            if (other.className != null) return false;
        } else if (!className.equals(other.className)) return false;
        if (descriptor == null) {
            if (other.descriptor != null) return false;
        } else if (!descriptor.equals(other.descriptor)) return false;
        if (fieldName == null) {
            if (other.fieldName != null) return false;
        } else if (!fieldName.equals(other.fieldName)) return false;
        return true;
    }
}


