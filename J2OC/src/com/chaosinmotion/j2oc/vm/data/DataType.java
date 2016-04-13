/*  FieldDescriptor.java
 *
 *  Created on Feb 6, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.data;


/**
 * Represents the type of a variable. In Java, the type of a variable is either a basic
 * type, a reference to a class object, or an array of the same. If it is an array, then
 * this also gives the dimensionality of that array.
 */
public class DataType
{
    public static final int T_BOOLEAN = 4;      // values similar to newarray constants
    public static final int T_CHAR = 5;
    public static final int T_FLOAT = 6;
    public static final int T_DOUBLE = 7;
    public static final int T_BYTE = 8;
    public static final int T_SHORT = 9;
    public static final int T_INT = 10;
    public static final int T_LONG = 11;
    
    public static final int T_ADDR = 2;         // synthetic
    public static final int T_CHARLIT = 1;      // from load constant on string
    public static final int T_VOID = 0;         // void return type for method

    private int type;           // type of data, taken from constants above
    private int array;          // if non-zero, represents dimension of this array
    private String className;   // if T_ADDR, can represents a class.
    
    /**
     * Create a data type which refers to a specified class. This will create a T_ADDR
     * type which holds a reference to the specified type
     * @param c
     * @param a
     */
    public DataType(String c, int a)
    {
        className = c;
        type = T_ADDR;
        array = a;
    }
    
    /**
     * Create the data type representing the specified types. Note that if this is
     * type T_ADDR, the resulting type will be an anonymous reference, similar to
     * those used in intermediate terms in code execution.
     * @param t Type (T_XXX)
     * @param a The dimension of this array: byte[][] would be 2.
     */
    public DataType(int t, int a)
    {
        type = t;
        array = a;
        className = null;
    }
    
    /**
     * Create a basic data type
     * @param t
     */
    public DataType(int t)
    {
        this(t,0);
    }

    /**
     * Returns the type of this object.
     * @return
     */
    public int getType()
    {
        return type;
    }

    /**
     * Returns true if this is void
     * @return
     */
    public boolean isVoid()
    {
        return (type == T_VOID);
    }
    
    /**
     * Returns true if the data type represented here is a wide object. It is a wide
     * object only if it is a primitive type of double or long.
     * @return
     */
    public boolean isWide()
    {
        return (array == 0) && ((type == T_DOUBLE) || (type == T_LONG));
    }
    
    /**
     * If non-zero, this actually represents an array of the basic type.
     */
    public int getArray()
    {
        return array;
    }

    /**
     * Returns the class if this is a T_ADDR type. This can be null if this is a
     * T_ADDR type, which then means this is an anonymous reference
     * @return
     */
    public String getClassName()
    {
        return className;
    }

    /**
     * Returns the primitive type; this is the storage type used to store this
     * internally in my virtual machine. Note that characters, bytes, booleans, and the
     * like are actually stored internally in my operator stack and register stack
     * as integers.
     * @return
     */
    public int getPrimitiveType()
    {
        if (array != 0) return T_ADDR;
        
        switch (type) {
            default:
            case T_INT:
                return T_INT;
            case T_FLOAT:
                return T_FLOAT;
            case T_LONG:
                return T_LONG;
            case T_DOUBLE:
                return T_DOUBLE;
            case T_CHARLIT:
            case T_ADDR:
                return T_ADDR;
        }
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + array;
        result = prime * result + ((className == null) ? 0 : className.hashCode());
        result = prime * result + type;
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        DataType other = (DataType)obj;
        if (array != other.array) return false;
        if (className == null) {
            if (other.className != null) return false;
        } else if (!className.equals(other.className)) return false;
        if (type != other.type) return false;
        return true;
    }
}



