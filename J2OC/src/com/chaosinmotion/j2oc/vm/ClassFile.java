/*  ClassParser.java
 *
 *  Created on Feb 5, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.chaosinmotion.j2oc.vm.data.Field;
import com.chaosinmotion.j2oc.vm.data.Method;
import com.chaosinmotion.j2oc.vm.data.attributes.Attribute;
import com.chaosinmotion.j2oc.vm.data.attributes.CodeAttribute;
import com.chaosinmotion.j2oc.vm.data.attributes.ConstantAttribute;
import com.chaosinmotion.j2oc.vm.data.attributes.DeprecatedAttribute;
import com.chaosinmotion.j2oc.vm.data.attributes.ExceptionsAttribute;
import com.chaosinmotion.j2oc.vm.data.attributes.GenericAttribute;
import com.chaosinmotion.j2oc.vm.data.attributes.InnerClassAttribute;
import com.chaosinmotion.j2oc.vm.data.attributes.LineNumberTableAttribute;
import com.chaosinmotion.j2oc.vm.data.attributes.LocalVariableAttribute;
import com.chaosinmotion.j2oc.vm.data.attributes.SourceFileAttribute;
import com.chaosinmotion.j2oc.vm.data.attributes.SyntheticAttribute;
import com.chaosinmotion.j2oc.vm.data.constants.ClassConstant;
import com.chaosinmotion.j2oc.vm.data.constants.Constant;
import com.chaosinmotion.j2oc.vm.data.constants.DoubleConstant;
import com.chaosinmotion.j2oc.vm.data.constants.FMIConstant;
import com.chaosinmotion.j2oc.vm.data.constants.FloatConstant;
import com.chaosinmotion.j2oc.vm.data.constants.IntegerConstant;
import com.chaosinmotion.j2oc.vm.data.constants.LongConstant;
import com.chaosinmotion.j2oc.vm.data.constants.NameAndTypeConstant;
import com.chaosinmotion.j2oc.vm.data.constants.StringConstant;
import com.chaosinmotion.j2oc.vm.data.constants.UTF8Constant;

/**
 * Class object. Represents a Java class
 */
public class ClassFile
{
    private int magic;
    private int minorVersion;
    private int majorVersion;
    private Constant[] constantPool;
    private int accessFlags;
    private int thisClass;
    private int superClass;
    private int[] interfaces;
    private Field[] fields;
    private Method[] methods;
    private Attribute[] attributes;
    
    private String thisClassName;
    private String superClassName;
    
    /*
     * Access flags for all levels of access
     */
    
    public static final int ACC_PUBLIC      = 0x0001;
    public static final int ACC_PRIVATE     = 0x0002;
    public static final int ACC_PROTECTED   = 0x0004;
    public static final int ACC_STATIC      = 0x0008;
    public static final int ACC_FINAL       = 0x0010;
    public static final int ACC_SUPER       = 0x0020;   // treat super methods specially (class decl)
    public static final int ACC_SYNCHRONIZED= 0x0020;   // synchronized method (method decl)
    public static final int ACC_VOLATILE    = 0x0040;
    public static final int ACC_TRANSIENT   = 0x0080;
    public static final int ACC_NATIVE      = 0x0100;
    public static final int ACC_INTERFACE   = 0x0200;
    public static final int ACC_ABSTRACT    = 0x0400;
    public static final int ACC_STRICT      = 0x0800;
    
    // new for JVM 5
    public static final int ACC_SYNTHETIC   = 0x1000;
    public static final int ACC_ANNOTATION  = 0x2000;
    public static final int ACC_ENUM        = 0x4000;
    
    /**
     * Read the contents of a .class file and construct the internal state for this
     * class to reflect the contents of the file. No validation takes place
     * beyond the basic validation steps
     * @param is
     * @throws IOException
     */
    public ClassFile(InputStream is) throws IOException
    {
        CountInputStream ct = new CountInputStream(is);
        DataInputStream dis = new DataInputStream(ct);
        
        /*
         * Read the class structure
         */
        
        try {
            magic = dis.readInt();
            if (magic != 0xCAFEBABE) throw new ClassParserException("Not a class file");
            minorVersion = dis.readUnsignedShort();
            majorVersion = dis.readUnsignedShort();

            /*
             * Read the constant pool
             */
            int len = dis.readUnsignedShort();
            if (len == 0) throw new ClassParserException("Illegal constant pool count");
            constantPool = new Constant[len];
            for (int i = 1; i < len; ++i) {
                constantPool[i] = readConstant(dis);
                if ((constantPool[i] instanceof LongConstant) || (constantPool[i] instanceof DoubleConstant)) {
                                // longs and doubles take two entries in the constant table
                    ++i;        // See Java VM v4.4.5, and footnote 2: "It was a poor choice."
                }
            }
            resolveConstants();

            /*
             * Read more fields
             */
            accessFlags = dis.readUnsignedShort();
            thisClass = dis.readUnsignedShort();
            superClass = dis.readUnsignedShort();

            /*
             * Resolve some of the attributes
             */

            thisClassName = ((ClassConstant)constantPool[thisClass]).getClassName();
            
            if (superClass != 0) {
                superClassName = ((ClassConstant)constantPool[superClass]).getClassName();
            }

            /*
             * Read the rest of the fields
             */
            len = dis.readUnsignedShort();        // interfaces
            interfaces = new int[len];
            for (int i = 0; i < len; ++i) {
                interfaces[i] = dis.readUnsignedShort();
            }

            len = dis.readUnsignedShort();
            fields = new Field[len];
            for (int i = 0; i < len; ++i) {
                fields[i] = readField(dis);
            }

            len = dis.readUnsignedShort();
            methods = new Method[len];
            for (int i = 0; i < len; ++i) {
                methods[i] = readMethod(dis);
            }

            attributes = readAttributeList(dis);
        }
        catch (Exception ex) {
            throw new ClassParserException("At " + ct.getReadPos() + ": " + ex.getMessage(),ex);
        }
    }
    

    /**
     * Get the class minor version
     * @return
     */
    public int getMinorVersion()
    {
        return minorVersion;
    }

    /**
     * Get the class major version
     * @return
     */
    public int getMajorVersion()
    {
        return majorVersion;
    }

    public String getThisClassName()
    {
        return thisClassName;
    }
    
    public String getSuperClassName()
    {
        return superClassName;
    }
    
    /**
     * Get the constant pool for this class file
     * @return
     */

    public Constant[] getConstantPool()
    {
        return constantPool;
    }

    /**
     * Get the class access flags
     * @return
     */

    public int getAccessFlags()
    {
        return accessFlags;
    }

    /**
     * Get the reference for 'this'
     * @return
     */

    public int getThisClass()
    {
        return thisClass;
    }

    /**
     * Get the reference for 'super'
     * @return
     */

    public int getSuperClass()
    {
        return superClass;
    }

    /**
     * Get the interfaces that this class implements
     * @return
     */

    public int[] getInterfaces()
    {
        return interfaces;
    }

    /**
     * Get the fields in this class
     * @return
     */

    public Field[] getFields()
    {
        return fields;
    }

    /**
     * Get the methods in this class
     * @return
     */

    public Method[] getMethods()
    {
        return methods;
    }

    /**
     * Get the attributes associated with this class
     * @return
     */

    public Attribute[] getAttributes()
    {
        return attributes;
    }

    /********************************************************************************/
    /*                                                                              */
    /*  Class parser                                                                */
    /*                                                                              */
    /********************************************************************************/

    /**
     * Read the attribute list. This parses an attribute list associated with any
     * point in the fle
     * @param dis
     * @return
     * @throws IOException 
     */
    private Attribute[] readAttributeList(DataInputStream dis) throws IOException
    {
        int len = dis.readUnsignedShort();
        Attribute[] attr = new Attribute[len];
        for (int i = 0; i < len; ++i) {
            attr[i] = readAttribute(dis);
        }
        for (Attribute a: attr) a.resolveConstant(constantPool);
        return attr;
    }

    private Attribute readAttribute(DataInputStream dis) throws IOException
    {
        int nameIndex = dis.readUnsignedShort();
        int len = dis.readInt();
        String nval = ((UTF8Constant)constantPool[nameIndex]).getValue();
        
        if (nval.equals("ConstantValue")) {
            /* Constants definition */
            return new ConstantAttribute(nval,dis.readUnsignedShort());

        } else if (nval.equals("Code")) {
            /* Code definition */
            int maxStack = dis.readUnsignedShort();
            int maxLocals = dis.readUnsignedShort();
            
            int tmp = dis.readInt();
            byte[] code = new byte[tmp];
            dis.read(code);
            
            tmp = dis.readUnsignedShort();
            CodeAttribute.ExceptionTableItem[] exceptionTable = new CodeAttribute.ExceptionTableItem[tmp];
            for (int i = 0; i < tmp; ++i) {
                int startPc = dis.readUnsignedShort();
                int endPc = dis.readUnsignedShort();
                int handlerPc = dis.readUnsignedShort();
                int catchType = dis.readUnsignedShort();
                String className;
                if (catchType == 0) {
                    className = null;
                } else {
                    className = ((ClassConstant)constantPool[catchType]).getClassName();
                }
                CodeAttribute.ExceptionTableItem e = new CodeAttribute.ExceptionTableItem(startPc,endPc,handlerPc,catchType,className);
                exceptionTable[i] = e;
            }
            Attribute[] attr = readAttributeList(dis);
            return new CodeAttribute(nval,maxStack,maxLocals,code,exceptionTable,attr);
            
        } else if (nval.equals("Exceptions")) {
            /* Exceptions definition */
            int tmp = dis.readUnsignedShort();
            int[] etable = new int[tmp];
            for (int i = 0; i < tmp; ++i) {
                etable[i] = dis.readUnsignedShort();
            }
            return new ExceptionsAttribute(nval,etable);
            
        } else if (nval.equals("InnerClasses")) {
            /* Inner classes */
            int tmp = dis.readUnsignedShort();
            InnerClassAttribute.InnerClass[] array = new InnerClassAttribute.InnerClass[tmp];
            for (int i = 0; i < tmp; ++i) {
                int innerClassInfoIndex = dis.readUnsignedShort();
                int outerClassInfoIndex = dis.readUnsignedShort();
                int innerNameIndex = dis.readUnsignedShort();
                int innerClassAccessFlags = dis.readUnsignedShort();
                array[i] = new InnerClassAttribute.InnerClass(innerClassInfoIndex,
                        outerClassInfoIndex,innerNameIndex,innerClassAccessFlags);
            }
            return new InnerClassAttribute(nval,array);
            
        } else if (nval.equals("Synthetic")) {
            /* Synthetic attribute */
            return new SyntheticAttribute(nval);
            
        } else if (nval.equals("SourceFile")) {
            /* Source file attribute */
            return new SourceFileAttribute(nval,dis.readUnsignedShort());
            
        } else if (nval.equals("LineNumberTable")) {
            /* Line number attribute */
            int tmp = dis.readUnsignedShort();
            LineNumberTableAttribute.LineNumberItem[] array = new LineNumberTableAttribute.LineNumberItem[tmp];
            for (int i = 0; i < tmp; ++i) {
                int startPc = dis.readUnsignedShort();
                int lineNumber = dis.readUnsignedShort();
                
                array[i] = new LineNumberTableAttribute.LineNumberItem(startPc,lineNumber);
            }
            return new LineNumberTableAttribute(nval,array);
            
        } else if (nval.equals("LocalVariableTable")) {
            /* LocalVariableTable */
            int tmp = dis.readUnsignedShort();
            LocalVariableAttribute.LocalVariableItem[] array = new LocalVariableAttribute.LocalVariableItem[tmp];
            for (int i = 0; i < tmp; ++i) {
                int startPc = dis.readUnsignedShort();
                int length = dis.readUnsignedShort();
                int nIndex = dis.readUnsignedShort();
                int descriptorIndex = dis.readUnsignedShort();
                int index = dis.readUnsignedShort();
                array[i] = new LocalVariableAttribute.LocalVariableItem(startPc,length,nIndex,descriptorIndex,index);
            }
            return new LocalVariableAttribute(nval,array);
                        
        } else if (nval.equals("Deprecated")) {
            /* Deprecated */
            return new DeprecatedAttribute(nval);
            
        } else {
            byte[] data = new byte[len];
            dis.read(data);
            
            return new GenericAttribute(nval,data);
        }
    }

    /**
     * Read a method declaration
     * @param dis
     * @return
     * @throws IOException
     */
    private Method readMethod(DataInputStream dis) throws IOException
    {
        int flags = dis.readUnsignedShort();
        int nameIndex = dis.readUnsignedShort();
        int descIndex = dis.readUnsignedShort();
        Attribute[] attr = readAttributeList(dis);
        
        Method m = new Method(flags,nameIndex,descIndex,attr);
        
        m.resolveConstant(constantPool);
        return m;
    }

    /**
     * Read a field 
     * @param dis
     * @return
     * @throws IOException
     */
    private Field readField(DataInputStream dis) throws IOException
    {
        int flags = dis.readUnsignedShort();
        int nameIndex = dis.readUnsignedShort();
        int descIndex = dis.readUnsignedShort();
        Attribute[] attr = readAttributeList(dis);
        
        Field f = new Field(flags,nameIndex,descIndex,attr);
        f.resolveConstant(constantPool);
        return f;
    }

    /**
     * Internal routine reads the constants in the constant array
     * @param dis
     * @return
     * @throws IOException
     */
    private Constant readConstant(DataInputStream dis) throws IOException
    {
        int tmp1;
        int tmp2;
        
        int tag = dis.readUnsignedByte();
        switch (tag) {
            case Constant.CONSTANT_Class:
                return new ClassConstant(tag,dis.readUnsignedShort());
                
            case Constant.CONSTANT_Fieldref:
            case Constant.CONSTANT_Methodref:
            case Constant.CONSTANT_InterfaceMethodref:
                tmp1 = dis.readUnsignedShort();
                tmp2 = dis.readUnsignedShort();
                return new FMIConstant(tag,tmp1,tmp2);
                
            case Constant.CONSTANT_String:
                return new StringConstant(tag,dis.readUnsignedShort());
                
            case Constant.CONSTANT_Integer:
                return new IntegerConstant(tag,dis.readInt());
                
            case Constant.CONSTANT_Float:
                return new FloatConstant(tag,dis.readFloat());
                
            case Constant.CONSTANT_Long:
                return new LongConstant(tag,dis.readLong());
                
            case Constant.CONSTANT_Double:
                return new DoubleConstant(tag,dis.readDouble());
                
            case Constant.CONSTANT_NameAndType:
                tmp1 = dis.readUnsignedShort();
                tmp2 = dis.readUnsignedShort();
                return new NameAndTypeConstant(tag,tmp1,tmp2);

            case Constant.CONSTANT_Utf8:
                return new UTF8Constant(tag,dis.readUTF());
                
            default:
                throw new ClassParserException("Unknown tag " + tag);
        }
    }
    
    /**
     * This walks through all of the constants in the constants pool and resolves the
     * various links. At the end of this, each constant should be initialized with an
     * unfoled representation of the contents
     * @throws IOException 
     */
    private void resolveConstants() throws IOException
    {
        for (int i = 1; i < constantPool.length; ++i) {
            Constant c = constantPool[i];
            if (c != null) c.resolveConstant(constantPool);
        }
    }
}


