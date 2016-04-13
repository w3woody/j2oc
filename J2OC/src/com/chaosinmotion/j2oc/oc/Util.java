/*  Util.java
 *
 *  Created on Feb 7, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.oc;

import java.io.UnsupportedEncodingException;

import com.chaosinmotion.j2oc.vm.ClassFile;
import com.chaosinmotion.j2oc.vm.data.DataType;
import com.chaosinmotion.j2oc.vm.data.Method;
import com.chaosinmotion.j2oc.vm.data.MethodDescriptor;
import com.chaosinmotion.j2oc.vm.data.attributes.ConstantAttribute;
import com.chaosinmotion.j2oc.vm.data.constants.Constant;
import com.chaosinmotion.j2oc.vm.data.constants.DoubleConstant;
import com.chaosinmotion.j2oc.vm.data.constants.FMIConstant;
import com.chaosinmotion.j2oc.vm.data.constants.FloatConstant;
import com.chaosinmotion.j2oc.vm.data.constants.IntegerConstant;
import com.chaosinmotion.j2oc.vm.data.constants.LongConstant;
import com.chaosinmotion.j2oc.vm.data.constants.StringConstant;

public class Util
{
    private static boolean isIdentifierCharacter(char c)
    {
        if ((c >= 'A') && (c <= 'Z')) return true;
        if ((c >= 'a') && (c <= 'z')) return true;
        if ((c >= '0') && (c <= '9')) return true;
        if (c == '_') return true;
        return false;
    }
    
    /**
     * Format the constant value
     * @param v
     * @return
     */
    public static String formatConstant(ConstantAttribute v)
    {
        Constant c = v.getConstant();
        if (c instanceof DoubleConstant) {
            DoubleConstant val = (DoubleConstant)c;
            return formatDouble(val.getValue(),false);
        } else if (c instanceof FloatConstant) {
            FloatConstant val = (FloatConstant)c;
            return formatDouble(val.getValue(),true);
        } else if (c instanceof IntegerConstant) {
            IntegerConstant val = (IntegerConstant)c;
            return Integer.toString(val.getValue());
        } else if (c instanceof LongConstant) {
            LongConstant lval = (LongConstant)c;
            long val = lval.getValue();
            if (val == Long.MIN_VALUE) {
                // work around bug in GCC 4.2
                return "0x8000000000000000L";
            } else if (val == Long.MAX_VALUE) {
                // work around bug in GCC 4.2
                return "0x7FFFFFFFFFFFFFFFL";
            } else {
                return Long.toString(val);
            }
        } else if (c instanceof StringConstant) {
            StringConstant val = (StringConstant)c;
            return formatString(val);
        } else {
            return "???";
        }
    }
    
    private static final boolean ishex(byte b)
    {
        if ((b >= '0') && (b <= '9')) return true;
        if ((b >= 'a') && (b <= 'f')) return true;
        if ((b >= 'A') && (b <= 'F')) return true;
        return false;
    }
    
    /**
     * Convert to a Unicode string stream. We leverage the fact that if we
     * don't do anything else, the constants are stored as UTF-8.
     * @param sc
     * @return
     */
    public static String formatString(StringConstant sc)
    {
        String string = sc.getString();
        StringBuffer buffer = new StringBuffer();
        boolean flag = false;
        
        buffer.append("@\"");
        try {
            byte[] utf8 = string.getBytes("UTF-8");
            for (byte b: utf8) {
                if ((b == '"') || (b == '\\')) {
                    buffer.append('\\');
                    buffer.append((char)b);
                    flag = false;
                } else if (flag && ishex(b)) {
                    buffer.append('"');
                    buffer.append('"');
                    buffer.append((char)b);
                    flag = false;
                } else if ((b >= 0x20) && (b <= 0x7F)) {
                    buffer.append((char)b);
                    flag = false;
                } else {
                    buffer.append(String.format("\\x%02x",0x00FF & b));
                    flag = true;
                }
            }
            buffer.append('"');
            return buffer.toString();
        }
        catch (UnsupportedEncodingException e) {
            // Should never happen
            e.printStackTrace();
            return "";
        }
//        
//        int i,len = string.length();
//        for (i = 0; i < len; ++i) {
//            char c = string.charAt(i);
//            
//            if (c == '"') {
//                buffer.append("\\\"");
//            } else if (c == '\\') {
//                buffer.append("\\\\");
//            } else if ((c >= 0x20) && (c < 0x80)) {
//                buffer.append(c);
//            } else if ((c >= 0) && (c <= 0x007F)) {
//                buffer.append(String.format("\\x%02x", 0x00FF & c));
//            } else {
//                buffer.append(String.format("\\u%04x", 0x00FFFF & c));
//            }
//        }
//        buffer.append('"');
//        
//        return buffer.toString();

    }
    
    /**
     * Format field name. We do nothing here.
     * @param fname
     * @param cname
     * @return
     */
    public static String formatField(String fname, String cname)
    {
        if (fname.startsWith("this")) {
            return fname + "__" + formatClass(cname);
        } else {
            return fname + "_"; // + "__" + formatClass(cname);
        }
    }
    
    public static String formatField(FMIConstant f)
    {
        return formatField(f.getFieldName(),f.getClassName());
    }
    
    /**
     * Format the set field name
     * @param f
     * @return
     */
    public static String formatSetField(FMIConstant f)
    {
        String s = formatField(f);
        return "set" + Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
    
    public static String formatSetField(String fname, String cname)
    {
        String s = formatField(fname,cname);
        return "set" + Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
    
    /**
     * Format the class to something we use in our code generator. Note that this can
     * (for checkcast, for example) be a field name (i.e., '[[I'), so we need to format
     * this into what we're internally passing around: 'MultiArray'.)
     * @param cname
     * @return
     */
    public static String formatClass(String cname)
    {
        if (cname.startsWith("[")) {
            /*
             * Array type
             */
            
            char c = cname.charAt(1);
            switch (c) {
                case 'B':   return "J2OCByteArray";
                case 'C':   return "J2OCCharArray";
                case 'D':   return "J2OCDoubleArray";
                case 'F':   return "J2OCFloatArray";
                case 'I':   return "J2OCIntArray";
                case 'J':   return "J2OCLongArray";
                case 'L':   return "J2OCRefArray";
                case 'S':   return "J2OCShortArray";
                case 'Z':   return "J2OCBooleanArray";
                default:
                case '[':   return "J2OCRefArray";
            }
        }
        
        /*
         * Straight up class name
         */
        StringBuffer sbuf = new StringBuffer();
        int i,len = cname.length();
        for (i = 0; i < len; ++i) {
            char c = cname.charAt(i);
            if (isIdentifierCharacter(c)) sbuf.append(c);
            else sbuf.append('_');
        }
        
        return sbuf.toString();
    }
    
    /**
     * Create a fully qualified variable type declaration for this variable
     * @param dt
     * @return
     */
    public static String formatDataType(DataType dt)
    {
        String str = dt.getClassName();
        
        if (dt.getArray() == 0) {
            switch (dt.getType()) {
                case DataType.T_ADDR:
                    if (null != str) return formatClass(str) + " *";
                    return "id";
                case DataType.T_BOOLEAN:
                    return "BOOL";
                case DataType.T_CHAR:
                    return "unichar";
                case DataType.T_FLOAT:
                    return "float";
                case DataType.T_DOUBLE:
                    return "double";
                case DataType.T_BYTE:
                    return "int8_t";
                case DataType.T_SHORT:
                    return "int16_t";
                case DataType.T_INT:
                    return "int32_t";
                case DataType.T_LONG:
                    return "int64_t";
                case DataType.T_VOID:
                    return "void";
                default:
                    return "???";
            }
        }
        if (dt.getArray() == 1) {
            switch (dt.getType()) {
                case DataType.T_ADDR:
                    return "J2OCRefArray *";
                case DataType.T_BOOLEAN:
                    return "J2OCBooleanArray *";
                case DataType.T_CHAR:
                    return "J2OCCharArray *";
                case DataType.T_FLOAT:
                    return "J2OCFloatArray *";
                case DataType.T_DOUBLE:
                    return "J2OCDoubleArray *";
                case DataType.T_BYTE:
                    return "J2OCByteArray *";
                case DataType.T_SHORT:
                    return "J2OCShortArray *";
                case DataType.T_INT:
                    return "J2OCIntArray *";
                case DataType.T_LONG:
                    return "J2OCLongArray *";
                default:
                    return "???";
            }
        }
        return "MultiArray *";
    }
    
    /**
     * Format the double precision number with the correct value
     * @param v
     * @param floatFlag
     * @return
     */
    public static String formatDouble(double v, boolean floatFlag)
    {
        if (Double.isNaN(v)) {
            return "NAN";
        } else if (Double.isInfinite(v)) {
            if (v < 0) {
                return "-INFINITY";
            } else {
                return "INFINITY";
            }
        }
        String str = String.format("%.20g", v);
        if (floatFlag) return str + "f";
        else return str;
    }
    

    /**
     * Return format variable
     * @param type
     * @param var
     * @return
     */
    private static String formatVariable(int type, int var)
    {
        StringBuffer buf = new StringBuffer();
        
        buf.append('v').append(var).append("_");
        switch (type) {
            case DataType.T_ADDR:   buf.append('a');    break;
            case DataType.T_DOUBLE: buf.append('d');    break;
            case DataType.T_FLOAT:  buf.append('f');    break;
            case DataType.T_INT:    buf.append('i');    break;
            case DataType.T_LONG:   buf.append('l');    break;
            default:                buf.append('?');    break;
        }
        return buf.toString();
    }
    
    /**
     * Used to generate a method call from an FMI constant used during invoke.
     * @param fmi
     * @param staticFlag
     * @return
     */
    public static String formatMethodFromFMI(FMIConstant fmi, boolean staticFlag)
    {
        StringBuffer buffer = new StringBuffer();
        int index;
        
        if (staticFlag) {
            buffer.append("+ ");
            index = 0;
        } else {
            buffer.append("- ");
            index = 1;
        }
        
        buffer.append('(').append(formatDataType(fmi.getDescriptor().getRet())).append(')');
        
        String methodName = fmi.getFieldName();
        String className = fmi.getClassName();
        String name = Util.formatMethod(methodName, fmi.getDescriptor(), className);
        
        buffer.append(name);
        
        // parameters
        boolean first = true;
        for (DataType arg: fmi.getDescriptor().getArgs()) {
            if (!first) {
                buffer.append(' ');
            } else {
                first = false;
            }
            buffer.append(":(");
            buffer.append(formatDataType(arg));
            buffer.append(")");
            
            buffer.append(formatVariable(arg.getPrimitiveType(),index));
            index++;
            if (arg.isWide()) index++;  // align with java offset index
        }
        
        return buffer.toString();
    }
    
    /**
     * Return the method entry
     * @param c
     * @param m
     * @return
     */
    public static String formatMethodEntry(ClassFile c, Method m)
    {
        StringBuffer buffer = new StringBuffer();
        int index;
        
        /*
         * Write the method declaration
         */
        
        // Static/virtual marker
        if ((m.getAccessFlags() & ClassFile.ACC_STATIC) != 0) {
            buffer.append("+ ");
            index = 0;
        } else {
            buffer.append("- ");
            index = 1;
        }
        
        // return value
        buffer.append('(');
        buffer.append(formatDataType(m.getDescriptor().getRet()));
        buffer.append(')');
        
        // method name
        String methodName = m.getName();
        String className = c.getThisClassName();
        String name = Util.formatMethod(methodName, m.getDescriptor(), className);

        buffer.append(name);
        
        // parameters
        boolean first = true;
        for (DataType arg: m.getDescriptor().getArgs()) {
            if (!first) {
                buffer.append(' ');
            } else {
                first = false;
            }
            buffer.append(":(");
            buffer.append(formatDataType(arg));
            buffer.append(")");
            
            buffer.append(formatVariable(arg.getPrimitiveType(),index));
            index++;
            if (arg.isWide()) index++;  // align with java offset index
        }
        
        return buffer.toString();
    }
    
    private static String formatDataTypeMethodPart(DataType dt)
    {
        String str = null;
        
        switch (dt.getType()) {
            case DataType.T_BYTE:      str = "byte";       break;
            case DataType.T_CHAR:      str = "char";       break;
            case DataType.T_DOUBLE:    str = "double";     break;
            case DataType.T_FLOAT:     str = "float";      break;
            case DataType.T_INT:       str = "int";        break;
            case DataType.T_LONG:      str = "long";       break;
            case DataType.T_SHORT:     str = "short";      break;
            case DataType.T_BOOLEAN:   str = "boolean";    break;
            case DataType.T_ADDR:
                str = formatClass(dt.getClassName());
                break;
        }
        
        int arrayDepth = dt.getArray();
        if (arrayDepth > 0) {
            str = str + "_ARRAYTYPE";
            if (arrayDepth > 1) {
                str = str + arrayDepth;
            }
        }
        return str;
    }
    
    /**
     * Given a method, create the formatted method call
     * @param m
     * @return
     */
    public static String formatMethod(String name, MethodDescriptor desc, String c)
    {
        if (name.equals("<init>")) {
            name = "__init_" + formatClass(c);
        } else if (name.equals("<clinit>")) {
            return "initialize";
        }
        
        StringBuffer buf = new StringBuffer();
        buf.append(name);
        if (!desc.getRet().isVoid()) {
            buf.append("_");
            buf.append(formatDataTypeMethodPart(desc.getRet()));
        }
        buf.append("__");
        for (DataType dt: desc.getArgs()) {
            buf.append('_').append(formatDataTypeMethodPart(dt));
        }
        return buf.toString();
    }
}


