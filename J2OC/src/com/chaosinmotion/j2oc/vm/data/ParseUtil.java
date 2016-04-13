/*  Util.java
 *
 *  Created on Feb 8, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.data;

import java.io.IOException;
import java.util.ArrayList;

import com.chaosinmotion.j2oc.vm.ClassParserException;

public class ParseUtil
{
    /**
     * Parse a single data type
     * @param desc
     * @param ref
     * @return
     * @throws IOException 
     */
    private static DataType parseDataType(String desc, int[] ref) throws IOException
    {
        int off = ref[0];
        int array;
        char c;
        DataType ret;
        StringBuffer buf;
        
        array = 0;
        while ('[' == (c = desc.charAt(off++))) {
            ++array;
        }
        switch (c) {
            case 'B':   ret = new DataType(DataType.T_BYTE,array);      break;
            case 'C':   ret = new DataType(DataType.T_CHAR,array);      break;
            case 'D':   ret = new DataType(DataType.T_DOUBLE,array);    break;
            case 'F':   ret = new DataType(DataType.T_FLOAT,array);     break;
            case 'I':   ret = new DataType(DataType.T_INT,array);       break;
            case 'J':   ret = new DataType(DataType.T_LONG,array);      break;
            case 'S':   ret = new DataType(DataType.T_SHORT,array);     break;
            case 'Z':   ret = new DataType(DataType.T_BOOLEAN,array);   break;
            case 'V':   ret = new DataType(DataType.T_VOID,array);      break;
            case 'L':
                buf = new StringBuffer();
                while (';' != (c = desc.charAt(off++))) {
                    buf.append(c);
                }
                ret = new DataType(buf.toString(),array);
                break;
            default:
                throw new ClassParserException("Illegal descriptor");
        }
        
        ref[0] = off;
        return ret;
    }
    
    /**
     * A ClassConstant record can contain a string representing either a class or
     * an array. What this means is that if we don't start with a '[', then the
     * string is the straght class name (i.e., java.util.Thing) instead of a
     * type of class (Ljava.util.Thing;). So if we start with a '[', parse as if
     * this is a field; else return the class type as a data type.
     * @param desc
     * @return
     * @throws IOException
     */
    public static DataType parseClassOrArrayType(String desc) throws IOException
    {
        if (desc.startsWith("[")) {
            return parseDataType(desc);
        } else {
            return new DataType(desc,0);
        }
    }
    
    /**
     * Parse a field descriptor data type.
     * @param desc
     * @return
     * @throws IOException
     */
    public static DataType parseDataType(String desc) throws IOException
    {
        int[] i = new int[1];
        i[0] = 0;
        
        return parseDataType(desc,i);
    }
    
    /**
     * This parses a method or a field descriptor, returning a parsed descriptor
     * object.
     * @param desc
     * @return
     */
    public static MethodDescriptor parseDescriptor(String desc) throws IOException
    {
        DataType ret;
        ArrayList<DataType> args = null;
        int[] i = new int[1];
        i[0] = 0;
        
        try {
            if ('(' == desc.charAt(i[0])) {
                i[0]++;     // skip '('
                args = new ArrayList<DataType>();
                while (')' != desc.charAt(i[0])) {
                    args.add(parseDataType(desc,i));
                }
                i[0]++;     // skip ')'
            }
            
            ret = parseDataType(desc,i);
            
            return new MethodDescriptor(ret,args);
        }
        catch (Exception ex) {
            throw new ClassParserException("Illegal descriptor",ex);
        }
    }
}


