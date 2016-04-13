/*  NameAndTypeConstant.java
 *
 *  Created on Feb 5, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.data.constants;

import java.io.IOException;

import com.chaosinmotion.j2oc.vm.data.MethodDescriptor;
import com.chaosinmotion.j2oc.vm.data.ParseUtil;

public class NameAndTypeConstant extends Constant
{
    private int nameIndex;
    private int descriptorIndex;
    
    private String name;
    private MethodDescriptor descriptor;
    
    public NameAndTypeConstant(int tag, int n, int d)
    {
        super(tag);
        nameIndex = n;
        descriptorIndex = d;
    }

    public int getNameIndex()
    {
        return nameIndex;
    }

    public int getDescriptorIndex()
    {
        return descriptorIndex;
    }
    
    /**
     * Return the name of this field, method or interface
     * @return
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * Return the descriptor string associated with this field, method or interface
     * @return
     */
    public MethodDescriptor getDescriptor()
    {
        return descriptor;
    }

    @Override
    public void resolveConstant(Constant[] pool) throws IOException
    {
        name = ((UTF8Constant)pool[nameIndex]).getValue();
        String tmp = ((UTF8Constant)pool[descriptorIndex]).getValue();
        descriptor = ParseUtil.parseDescriptor(tmp);
    }
}


