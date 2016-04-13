/*  CodeAttribute.java
 *
 *  Created on Feb 6, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.data.attributes;

import java.io.IOException;

import com.chaosinmotion.j2oc.vm.data.constants.Constant;

/**
 * Represents the code in the system
 */
public class CodeAttribute extends Attribute
{
    public static class ExceptionTableItem 
    {
        private int startPc;
        private int endPc;
        private int handlerPc;
        private int catchType;
        private String className;
        
        public ExceptionTableItem(int spc, int epc, int hpc, int ctype, String cname)
        {
            startPc = spc;
            endPc = epc;
            handlerPc = hpc;
            catchType = ctype;
            className = cname;
        }

        public int getStartPc()
        {
            return startPc;
        }

        public int getEndPc()
        {
            return endPc;
        }

        public int getHandlerPc()
        {
            return handlerPc;
        }

        public int getCatchType()
        {
            return catchType;
        }
        
        public String getCatchClassName()
        {
            return className;
        }
    }
    private int maxStack;
    private int maxLocals;
    private byte[] code;
    private ExceptionTableItem[] exceptionTable;
    private Attribute[] attributes;
    
    public CodeAttribute(String n, int stack, int locals, byte[] c, ExceptionTableItem[] etable, Attribute[] attr)
    {
        super(n);
        
        maxStack = stack;
        maxLocals = locals;
        code = c;
        exceptionTable = etable;
        attributes = attr;
    }

    public int getMaxStack()
    {
        return maxStack;
    }

    public int getMaxLocals()
    {
        return maxLocals;
    }

    public byte[] getCode()
    {
        return code;
    }

    public ExceptionTableItem[] getExceptionTable()
    {
        return exceptionTable;
    }

    public Attribute[] getAttributes()
    {
        return attributes;
    }

    @Override
    public void resolveConstant(Constant[] constantPool) throws IOException
    {
    }
}


