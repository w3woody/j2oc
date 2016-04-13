/*  InnerClassAttribute.java
 *
 *  Created on Feb 6, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.data.attributes;

import java.io.IOException;

import com.chaosinmotion.j2oc.vm.data.constants.ClassConstant;
import com.chaosinmotion.j2oc.vm.data.constants.Constant;
import com.chaosinmotion.j2oc.vm.data.constants.UTF8Constant;

public class InnerClassAttribute extends Attribute
{
    public static class InnerClass
    {
        private int innerClassInfoIndex;
        private int outerClassInfoIndex;
        private int innerNameIndex;
        private int innerClassAccessFlags;
        
        private String innerClassName;
        private String outerClassName;
        private String innerName;
        
        public InnerClass(int ic, int oc, int in, int icf)
        {
            innerClassInfoIndex = ic;
            outerClassInfoIndex = oc;
            innerNameIndex = in;
            innerClassAccessFlags = icf;
        }

        public int getInnerClassInfoIndex()
        {
            return innerClassInfoIndex;
        }

        public int getOuterClassInfoIndex()
        {
            return outerClassInfoIndex;
        }

        public int getInnerNameIndex()
        {
            return innerNameIndex;
        }

        public int getInnerClassAccessFlags()
        {
            return innerClassAccessFlags;
        }
        
        public String getInnerClassName()
        {
            return innerClassName;
        }
        
        public String getOuterClassName()
        {
            return outerClassName;
        }
        
        public String getInnerName()
        {
            return innerName;
        }
        
        private void resolve(String ic, String oc, String i)
        {
            innerClassName = ic;
            outerClassName = oc;
            innerName = i;
        }
    }

    private InnerClass[] classes;
    
    public InnerClassAttribute(String n, InnerClass[] ic)
    {
        super(n);
        
        classes = ic;
    }

    public InnerClass[] getClasses()
    {
        return classes;
    }

    @Override
    public void resolveConstant(Constant[] constantPool) throws IOException
    {
        for (InnerClass ic: classes) {
            String innerClassName;
            String outerClassName;
            String innerName;
            
            int i = ic.getInnerClassInfoIndex();
            innerClassName = (i == 0) ? null : ((ClassConstant)constantPool[i]).getClassName();
            i = ic.getOuterClassInfoIndex();
            outerClassName = (i == 0) ? null : ((ClassConstant)constantPool[i]).getClassName();
            i = ic.getInnerNameIndex();
            innerName = (i == 0) ? null : ((UTF8Constant)constantPool[i]).getValue();

            ic.resolve(innerClassName, outerClassName, innerName);
        }
    }
}


