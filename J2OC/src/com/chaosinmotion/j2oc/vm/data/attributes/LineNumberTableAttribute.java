/*  LineNumberTableAttribute.java
 *
 *  Created on Feb 6, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.data.attributes;

import java.io.IOException;

import com.chaosinmotion.j2oc.vm.data.constants.Constant;

/**
 * Stores the line numbers associated with this code for debug
 */
public class LineNumberTableAttribute extends Attribute
{
    public static class LineNumberItem
    {
        private int startPc;
        private int lineNumber;

        public LineNumberItem(int spc, int ln)
        {
            this.startPc = spc;
            this.lineNumber = spc;
        }

        public int getStartPc()
        {
            return startPc;
        }

        public int getLineNumber()
        {
            return lineNumber;
        }
    }
    
    private LineNumberItem[] lineNumberTable;

    public LineNumberTableAttribute(String n, LineNumberItem[] l)
    {
        super(n);
        lineNumberTable = l;
    }

    public LineNumberItem[] getLineNumberTable()
    {
        return lineNumberTable;
    }

    @Override
    public void resolveConstant(Constant[] constantPool) throws IOException
    {
    }
}


