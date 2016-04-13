/*  Field.java
 *
 *  Created on Feb 5, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.data;

import java.io.IOException;

import com.chaosinmotion.j2oc.vm.data.attributes.Attribute;
import com.chaosinmotion.j2oc.vm.data.constants.Constant;
import com.chaosinmotion.j2oc.vm.data.constants.UTF8Constant;

/**
 * Represents a field in the class. Once initialized after parsing, this will
 * contain routines to get the field name and data type of that field, as well
 * as the access flags for that field.
 */
public class Field
{
    private int accessFlags;
    private int nameIndex;
    private int descriptorIndex;
    private Attribute[] attributes;
    
    private String name;
    private DataType type;
    
    public Field(int af, int ni, int di, Attribute[] a)
    {
        this.accessFlags = af;
        this.nameIndex = ni;
        this.descriptorIndex = di;
        this.attributes = a;
    }

    public int getAccessFlags()
    {
        return accessFlags;
    }

    public int getNameIndex()
    {
        return nameIndex;
    }

    public int getDescriptorIndex()
    {
        return descriptorIndex;
    }

    public Attribute[] getAttributes()
    {
        return attributes;
    }
    
    public String getName()
    {
        return name;
    }
    
    public DataType getDescriptor()
    {
        return type;
    }

    public void resolveConstant(Constant[] constantPool) throws IOException
    {
        name = ((UTF8Constant)constantPool[nameIndex]).getValue();
        
        String tmp = ((UTF8Constant)constantPool[descriptorIndex]).getValue();
        type = ParseUtil.parseDescriptor(tmp).getRet();
    }
}


