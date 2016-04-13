/*  ArrayStore.java
 *
 *  Created on Feb 6, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.code.inst;

import com.chaosinmotion.j2oc.vm.code.op.Op;
import com.chaosinmotion.j2oc.vm.data.DataType;

public class ArrayStore extends Instruction
{
    private Op arrayRef;
    private Op index;
    private Op value;
    private int dataType;

    public ArrayStore(Op a, Op i, Op v, int op)
    {
        arrayRef = a;
        index = i;
        value = v;
        
        switch (op) {
            case 0x53:  // aastore
                dataType = DataType.T_ADDR;
                break;
            case 0x54:  // bastore
                dataType = DataType.T_BYTE;
                break;
            case 0x55:  // castore
                dataType = DataType.T_CHAR;
                break;
            case 0x52:  // dastore
                dataType = DataType.T_DOUBLE;
                break;
            case 0x51:  // fastore
                dataType = DataType.T_FLOAT;
                break;
            case 0x4f:  // iastore
                dataType = DataType.T_INT;
                break;
            case 0x50:  // lastore
                dataType = DataType.T_LONG;
                break;
            case 0x56:  // sastore
                dataType = DataType.T_SHORT;
                break;
        }
    }

    public Op getArrayRef()
    {
        return arrayRef;
    }

    public Op getIndex()
    {
        return index;
    }

    public Op getValue()
    {
        return value;
    }
    
    public int getDataType()
    {
        return dataType;
    }
}


