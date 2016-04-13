package com.chaosinmotion.j2oc.vm.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.chaosinmotion.j2oc.vm.ClassParserException;

/**
 * Represents either a field or method descriptor. A field descriptor is represented
 * by storing the data type of the field in getRet; getArgs will return null. For a
 * method, getArgs will return a (potentially empty) list of arguments.
 */
public class MethodDescriptor
{
    private DataType ret;
    private List<DataType> args;
    
    public MethodDescriptor(DataType type)
    {
        ret = type;
        args = null;
    }
    
    public MethodDescriptor(DataType type, List<DataType> a)
    {
        ret = type;
        args = a;
    }
    
    /**
     * For a method this is the return value of this data type. For a
     * field this is the type of that field.
     */
    public DataType getRet()
    {
        return ret;
    }
    
    /**
     * For a method this is a (potentially empty) list of arguments. For a
     * field, this will return null.
     * @return
     */
    public List<DataType> getArgs()
    {
        return args;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((args == null) ? 0 : args.hashCode());
        result = prime * result + ((ret == null) ? 0 : ret.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        MethodDescriptor other = (MethodDescriptor)obj;
        if (args == null) {
            if (other.args != null) return false;
        } else if (!args.equals(other.args)) return false;
        if (ret == null) {
            if (other.ret != null) return false;
        } else if (!ret.equals(other.ret)) return false;
        return true;
    }
}