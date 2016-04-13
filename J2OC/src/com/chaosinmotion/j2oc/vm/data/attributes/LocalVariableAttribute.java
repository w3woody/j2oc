/*  LocalVariableAttribute.java
 *
 *  Created on Feb 6, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.data.attributes;

import java.io.IOException;

import com.chaosinmotion.j2oc.vm.data.DataType;
import com.chaosinmotion.j2oc.vm.data.MethodDescriptor;
import com.chaosinmotion.j2oc.vm.data.ParseUtil;
import com.chaosinmotion.j2oc.vm.data.constants.Constant;
import com.chaosinmotion.j2oc.vm.data.constants.UTF8Constant;

public class LocalVariableAttribute extends Attribute
{
    public static class LocalVariableItem
    {
        private int startPc;
        private int length;
        private int nameIndex;
        private int descriptorIndex;
        private int index;
        
        private String varName;
        private DataType dataType;
        
        public LocalVariableItem(int spc, int len, int name, int desc, int ix)
        {
            this.startPc = spc;
            this.length = len;
            this.nameIndex = name;
            this.descriptorIndex = desc;
            this.index = ix;
        }

        public int getStartPc()
        {
            return startPc;
        }

        public int getLength()
        {
            return length;
        }

        public int getNameIndex()
        {
            return nameIndex;
        }

        public int getDescriptorIndex()
        {
            return descriptorIndex;
        }

        public int getIndex()
        {
            return index;
        }
        
        public String getName()
        {
            return varName;
        }
        
        public DataType getDataType()
        {
            return dataType;
        }
        
        private void resolve(String n, DataType d)
        {
            varName = n;
            dataType = d;
        }
    }

    private LocalVariableItem[] localVariableTable;
    
    public LocalVariableAttribute(String n, LocalVariableItem[] l)
    {
        super(n);
        localVariableTable = l;
    }

    public LocalVariableItem[] getLocalVariableTable()
    {
        return localVariableTable;
    }

    @Override
    public void resolveConstant(Constant[] constantPool) throws IOException
    {
        for (LocalVariableItem i: localVariableTable) {
            String name = ((UTF8Constant)constantPool[i.getNameIndex()]).getValue();
            String tmp = ((UTF8Constant)constantPool[i.getDescriptorIndex()]).getValue();
            MethodDescriptor d = ParseUtil.parseDescriptor(tmp);
            
            i.resolve(name, d.getRet());
        }
    }
}


