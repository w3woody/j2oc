/*  ConstantOp.java
 *
 *  Created on Feb 6, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.code.op;

import java.io.IOException;

import com.chaosinmotion.j2oc.vm.ClassParserException;
import com.chaosinmotion.j2oc.vm.data.DataType;
import com.chaosinmotion.j2oc.vm.data.constants.Constant;
import com.chaosinmotion.j2oc.vm.data.constants.DoubleConstant;
import com.chaosinmotion.j2oc.vm.data.constants.FloatConstant;
import com.chaosinmotion.j2oc.vm.data.constants.IntegerConstant;
import com.chaosinmotion.j2oc.vm.data.constants.LongConstant;

/**
 * Constant operator; represents a constant
 */
public class ConstantOp extends Op
{
    private DataType type;
    private Constant constant;
    
    public ConstantOp(int val)
    {
        type = new DataType(DataType.T_INT);
        constant = new IntegerConstant(Constant.CONSTANT_Integer,val);
    }
    
    public ConstantOp(long val)
    {
        type = new DataType(DataType.T_LONG);
        constant = new LongConstant(Constant.CONSTANT_Long,val);
    }
    
    public ConstantOp(float val)
    {
        type = new DataType(DataType.T_LONG);
        constant = new FloatConstant(Constant.CONSTANT_Float,val);
    }
    
    public ConstantOp(double val)
    {
        type = new DataType(DataType.T_LONG);
        constant = new DoubleConstant(Constant.CONSTANT_Double,val);
    }
    
    public ConstantOp(Constant c) throws IOException
    {
        constant = c;
        
        switch (constant.getTag()) {
            case Constant.CONSTANT_Double:
                type = new DataType(DataType.T_DOUBLE);
                break;
            case Constant.CONSTANT_Float:
                type = new DataType(DataType.T_FLOAT);
                break;
            case Constant.CONSTANT_Integer:
                type = new DataType(DataType.T_INT);
                break;
            case Constant.CONSTANT_Long:
                type = new DataType(DataType.T_LONG);
                break;
            case Constant.CONSTANT_String:
                type = new DataType(DataType.T_CHARLIT);
                break;
            case Constant.CONSTANT_Class:
                // Class<T> foo = MyObject.class; will cause this
                type = new DataType(DataType.T_ADDR);
                break;
            default:
                throw new ClassParserException("Unknown constant " + constant.getTag());
        }
    }
    
    public ConstantOp()
    {
        type = new DataType(DataType.T_ADDR);
        constant = null;
    }
    
    public DataType getType()
    {
        return type;
    }
    
    public Constant getConstant()
    {
        return constant;
    }

    @Override
    public boolean isWide()
    {
        return type.isWide();
    }

    @Override
    public int getPrimitiveType()
    {
        return type.getPrimitiveType();
    }
}


