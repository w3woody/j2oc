/*  Method.java
 *
 *  Created on Feb 5, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.data;

import java.io.IOException;

import com.chaosinmotion.j2oc.vm.ClassParserException;
import com.chaosinmotion.j2oc.vm.code.Code;
import com.chaosinmotion.j2oc.vm.data.attributes.Attribute;
import com.chaosinmotion.j2oc.vm.data.attributes.CodeAttribute;
import com.chaosinmotion.j2oc.vm.data.constants.Constant;
import com.chaosinmotion.j2oc.vm.data.constants.UTF8Constant;

/**
 * Represents a method in this class. Once initialized this will have the
 * access flags, name of the method, and the method descriptor, which gives both
 * the function call parameters as well as the return parameter
 */
public class Method
{
    private int accessFlags;
    private int nameIndex;
    private int descriptorIndex;
    private Attribute[] attributes;
    
    private String name;
    private MethodDescriptor descriptor;
    
    private Code code;

    public Method(int af, int ni, int di, Attribute[] a)
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
    
    public MethodDescriptor getDescriptor()
    {
        return descriptor;
    }

    public void resolveConstant(Constant[] constantPool) throws IOException
    {
        try {
        name = ((UTF8Constant)constantPool[nameIndex]).getValue();
        
        String tmp = ((UTF8Constant)constantPool[descriptorIndex]).getValue();
        descriptor = ParseUtil.parseDescriptor(tmp);
        
        for (Attribute attr: attributes) {
            if (attr instanceof CodeAttribute) {
                CodeAttribute ca = (CodeAttribute)attr;
                code = new Code(ca,constantPool);
                code.parse();
                break;
            }
        }
        }
        catch (Exception ex) {
            throw new ClassParserException("In Method " + name + ", had exception " + ex.getMessage(),ex);
        }
    }
    
    public Code getCode()
    {
        return code;
    }
}


