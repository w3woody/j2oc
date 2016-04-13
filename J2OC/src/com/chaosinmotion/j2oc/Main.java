/*  Main.java
 *
 *  Created on Feb 9, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.chaosinmotion.j2oc.oc.Missing;
import com.chaosinmotion.j2oc.oc.RewriteSupport;
import com.chaosinmotion.j2oc.oc.Util;
import com.chaosinmotion.j2oc.oc.WriteOCMethod;
import com.chaosinmotion.j2oc.vm.ClassFile;
import com.chaosinmotion.j2oc.vm.data.DataType;
import com.chaosinmotion.j2oc.vm.data.Field;
import com.chaosinmotion.j2oc.vm.data.Method;
import com.chaosinmotion.j2oc.vm.data.MethodDescriptor;
import com.chaosinmotion.j2oc.vm.data.ParseUtil;
import com.chaosinmotion.j2oc.vm.data.attributes.Attribute;
import com.chaosinmotion.j2oc.vm.data.attributes.ConstantAttribute;
import com.chaosinmotion.j2oc.vm.data.constants.ClassConstant;
import com.chaosinmotion.j2oc.vm.data.constants.Constant;
import com.chaosinmotion.j2oc.vm.data.constants.FMIConstant;

public class Main
{
    private static Arguments args;
    private static Missing missing;
    private static PrintStream fRewriteOut;
    
    /**
     * @param args
     */
    public static void main(String[] a)
    {
        try {
            args = new Arguments(a);
        }
        catch (Arguments.ParserError e) {
            System.out.println("Problem parsing arguments: " + e.getMessage());
            System.exit(-1);
        }
        
        Log.setState(args);
        long startTime = System.currentTimeMillis();
        Log.l("Starting parser");
        
        /*
         * Set up for the rewrite generator.
         */
        
        if (args.isGenerateRewriteXML()) {
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(args.getRewriteXMLOut());
            }
            catch (FileNotFoundException e) {
                System.err.println("Unable to open file " + args.getRewriteXMLOut().getAbsolutePath());
                e.printStackTrace();
                return;
            }
            fRewriteOut = new PrintStream(fos);
            fRewriteOut.println("<rewrite>");
        }
        if (args.isUseRewriteXML()) {
            try {
                RewriteSupport.getRewrite().loadRewriteRules(args.getRewriteXMLIn());
            }
            catch (Exception e) {
                System.err.println("Unable to open file " + args.getRewriteXMLIn().getAbsolutePath());
                e.printStackTrace();
                return;
            }
        }
        
        /*
         * Set up for missing call storage
         */

        if (args.isMissingCalls()) {
            missing = new Missing();
        }
        
        recurseLoad(args.getInDirectory());
        
        /*
         * Write relevent files
         */
        if (fRewriteOut != null) {
            fRewriteOut.println("</rewrite>");
            fRewriteOut.close();
        }
        
        if (missing != null) {
            try {
                writeMissing();
            }
            catch (IOException ex) {
                System.out.println("Problem writing missing headers: " + ex.getMessage());
                System.out.println("Exception");
                ex.printStackTrace();
            }
        }
        long time = System.currentTimeMillis() - startTime;
        Log.l("Ending parser: " + time + "ms");
    }

    /**
     * Recursive load
     * @param f
     */
    private static void recurseLoad(File f)
    {
        if (f.isDirectory()) {
            File[] list = f.listFiles();
            for (File i: list) {
                recurseLoad(i);
            }
            return;
        }

        String str = f.getName();
        if (str.endsWith(".class")) {
            Log.l("Processing " + f.getAbsoluteFile());
            try {
                FileInputStream fis = new FileInputStream(f);
                ClassFile cf = new ClassFile(fis);
                fis.close();
                
                String fname = Util.formatClass(cf.getThisClassName());
                if (!RewriteSupport.getRewrite().skipRewrite(fname)) {
                    writeClassFile(cf);
                } else {
                    System.out.println("  Skipping write of " + fname);
                }
            }
            catch (IOException ex) {
                System.out.println("Error processing " + f.getAbsoluteFile());
                System.out.println("Exception");
                ex.printStackTrace();
            }
        } else {
            Log.m("Unable to process file " + f.getAbsoluteFile());
        }
    }

    private static boolean isSynthetic(Method m)
    {
        return 0 != (ClassFile.ACC_SYNTHETIC & m.getAccessFlags());
    }
    
    /**
     * Write the class file. This generates the '.h' and '.m' files
     * @param cf
     */
    private static void writeClassFile(ClassFile cf) throws IOException
    {
        if (missing != null) {
            missing.addClass(cf.getThisClassName());
        }
        
        /**
         * Here's the problem: in some cases Java synthesizes a method whose
         * signature is the same as a non-synthesized method. We need to screen
         * those out without screening out the synthesized methods we need to keep.
         * 
         * So we scan the list of methods in this class, and if there is a
         * duplicate with a non-synthesized method, we drop the synthesized version.
         * 
         * Note that this also puts the methods into alphabetical order as a side
         * effect of our processing
         */
        TreeMap<String,Method> methods = new TreeMap<String,Method>();
        for (Method m: cf.getMethods()) {
            String mname = Util.formatMethod(m.getName(), m.getDescriptor(), cf.getThisClassName());
            
            Method dup = methods.get(mname);
            if (dup == null) {
                methods.put(mname,m);
            } else if (isSynthetic(dup) && !isSynthetic(m)) {
                methods.put(mname,m);
            }
        }
        
        /*
         * Write the methods we want
         */
        writeClassHeader(cf,methods.values());
        writeClassCode(cf,methods.values(),fRewriteOut);
    }
    
    /**
     * Write the actual header 
     * @param cf
     * @param methods 
     */
    private static void writeClassHeader(ClassFile cf, Collection<Method> methods) throws IOException
    {
        String name = Util.formatClass(cf.getThisClassName()) + ".h";
        File out = new File(args.getOutHeaders(),name);
        FileOutputStream fos = new FileOutputStream(out);
        PrintStream ps = new PrintStream(fos);
        
        /*
         * Determine if we're dealing with an interface or with a class. If an
         * interface, we write a @protocol declaration and we don't write fields
         */
        
        boolean protocol = (cf.getAccessFlags() & ClassFile.ACC_INTERFACE) != 0;
        
        /*
         * Write the standard header
         */
        
        ps.println("/*  " + name);
        ps.println(" *");
        ps.println(" *      Autogenerated by j2oc (http://www.j2oc.com) at " + new Date().toString());
        ps.println(" *");
        ps.println(" *      Generated from class " + cf.getThisClassName());
        ps.println(" *      Do not change here; change in original class.");
        ps.println(" */");
        ps.println();
        ps.println();
        
        /*
         * Build list of classes with associated interfaces
         */
        TreeSet<String> ilist = new TreeSet<String>();
        for (int iface: cf.getInterfaces()) {
            ClassConstant cc = ((ClassConstant)cf.getConstantPool()[iface]);
            ilist.add(cc.getClassName());
        }
        
        /*
         * Generate the include for our headers and for the super header, as well
         * as each of the interfaces
         */
        
        ps.println("// includes");
        ps.println("#import <Foundation/Foundation.h>");
        ps.println("#import \"j2oc.h\"");
        if (!protocol && (cf.getSuperClassName() != null)) {
            ps.println("#import \"" + Util.formatClass(cf.getSuperClassName()) + ".h\"");
        }
        for (String str: ilist) {
            ps.println("#import \"" + Util.formatClass(str) + ".h\"");
        }
        ps.println();
        
        /*
         * Walk all of the classes mentioned in the fields and methods, and
         * create a @class declaration for each. We only need to write the
         * class forward declarations for those that appear in our methods
         * and fields; in the include file of the sources we'll write an include
         * for all classes.
         */
        
        TreeSet<String> classes = new TreeSet<String>();
        for (Field f: cf.getFields()) {
            String cname = f.getDescriptor().getClassName();
            if (cname != null) classes.add(cname);
        }
        for (Method m: methods) {
            MethodDescriptor md = m.getDescriptor();
            String cname = md.getRet().getClassName();
            if (cname != null) classes.add(cname);
            
            for (DataType dt: md.getArgs()) {
                cname = dt.getClassName();
                if (cname != null) classes.add(cname);
            }
        }
        if ((cf.getSuperClassName() != null)) {
            classes.remove(cf.getSuperClassName());
        }
        classes.remove(cf.getThisClassName());
        
        if (!classes.isEmpty() || protocol) {
            ps.println("// class forwards");
            for (String str: classes) {
                ps.println("@class " + Util.formatClass(str) + ";");
            }
            ps.println("@class " + Util.formatClass(cf.getThisClassName()) + ";");
            ps.println();
        }
        
        /*
         * Create the header delcaration. For Objective C it looks like:
         * 
         *  @interface thisClass: superClass < protocol, protocol, ... >
         *  {
         *      fields
         *  }
         *  methods
         *  @end
         */
        
        ps.println("// class declaration");
        StringBuffer b = new StringBuffer();
        if (protocol) {
            b.append("@protocol ");
        } else {
            b.append("@interface ");
        }
        b.append(Util.formatClass(cf.getThisClassName()));
        if (!protocol && (cf.getSuperClassName() != null)) {
            b.append(": ").append(Util.formatClass(cf.getSuperClassName()));
        }
        ps.println(b.toString());

        if (!ilist.isEmpty() || protocol) {
            b = new StringBuffer();
            b.append("        <");
            boolean flag = false;
            for (String str: ilist) {
                if (flag) b.append(", ");
                else flag = true;
                b.append(Util.formatClass(str));
            }
            
            if (protocol) {
                if (flag) b.append(", ");
                b.append("NSObject");
            }
            
            b.append('>');
            ps.println(b.toString());
        }
        
        /*
         * Write the fields
         */
        if (!protocol) {
            /* Write the fields of this class */
            ps.println("{");
            for (Field f: cf.getFields()) {
                if ((f.getAccessFlags() & ClassFile.ACC_STATIC) == 0) {
                    String fieldName = Util.formatField(f.getName(),cf.getThisClassName());

                    b = new StringBuffer();
                    DataType dt = f.getDescriptor();
                    b.append("    ").append(Util.formatDataType(dt));
                    if (b.charAt(b.length()-1) != '*') {
                        b.append(' ');
                    }
                    b.append(fieldName).append(";");
                    ps.println(b.toString());
                }
            }
            ps.println("}");
            
            /* Write the property accessors for those fields */
            ps.println();
            for (Field f: cf.getFields()) {
                if ((f.getAccessFlags() & ClassFile.ACC_STATIC) == 0) {
                    String fieldName = Util.formatField(f.getName(),cf.getThisClassName());

                    b = new StringBuffer();
                    b.append("@property ");
                    DataType dt = f.getDescriptor();
                    if (dt.getPrimitiveType() == DataType.T_ADDR) {
                        b.append("(retain) ");
                    }
                    b.append(Util.formatDataType(dt));
                    if (b.charAt(b.length()-1) != '*') {
                        b.append(' ');
                    }
                    b.append(fieldName).append(";");
                    ps.println(b.toString());
                }
            }
            if (cf.getFields().length != 0) {
                ps.println();
            }
        }

        /* Write the static fields as global getters and setters for this class */
        for (Field f: cf.getFields()) {
            if ((f.getAccessFlags() & ClassFile.ACC_STATIC) != 0) {
                String fieldName = Util.formatField(f.getName(),cf.getThisClassName());
                String setFieldName = Util.formatSetField(f.getName(),cf.getThisClassName());

                /*
                 * This is a static field.
                 * 
                 * write + (op)field
                 * write + (void)setField:(op)v;
                 */
                
                b = new StringBuffer();
                DataType dt = f.getDescriptor();
                
                b.append("+ (").append(Util.formatDataType(dt)).append(')');
                b.append(fieldName).append(';');
                ps.println(b);
                
                b = new StringBuffer();
                b.append("+ (void)");
                b.append(setFieldName).append(":(");
                b.append(Util.formatDataType(dt)).append(")value;");
                ps.println(b);
            }
        }

        /*
         * Write the methods
         */
        for (Method m: methods) {
            ps.println(Util.formatMethodEntry(cf, m) + ";");
        }
        
        // Write the end
        
        ps.println("@end");
        ps.println();
        
        // If this is a protocol we also write the object implementing the
        // declaration
        if (protocol) {
            String cname = Util.formatClass(cf.getThisClassName());
            ps.println("@interface " + cname + ": java_lang_Object <" + cname + ">");
            ps.println("@end");
        }
        
        ps.close();
        
        
    }
    
    /**
     * Write the body of this code.
     * @param cf
     * @param methods 
     * @throws IOException
     */
    private static void writeClassCode(ClassFile cf, Collection<Method> methods, PrintStream rewriteOut) throws IOException
    {
        String name = Util.formatClass(cf.getThisClassName()) + ".m";
        File out = new File(args.getOutDirectory(),name);
        FileOutputStream fos = new FileOutputStream(out);
        PrintStream ps = new PrintStream(fos);
        
        /*
         * Generate the header
         */
        
        ps.println("/*  " + name);
        ps.println(" *");
        ps.println(" *      Autogenerated by j2oc (http://www.j2oc.com) at " + new Date().toString());
        ps.println(" *");
        ps.println(" *      Generated from class " + cf.getThisClassName());
        ps.println(" *      Do not change here; change in original class.");
        ps.println(" */");
        ps.println();
        ps.println();

        /*
         * Generate the include for me, for all classes that are mentioned in the
         * class directory
         */
        
        TreeSet<String> set = new TreeSet<String>();
        for (Constant c: cf.getConstantPool()) {
            if (c instanceof ClassConstant) {
                ClassConstant cc = (ClassConstant)c;
                int pos = 0;
                String cname = cc.getClassName();
                if (cname.charAt(pos) == '[') {
                    DataType dt = ParseUtil.parseDataType(cname);
                    cname = dt.getClassName();
                }
                if (cname != null) set.add(cname);
            }
        }
        
        // remove this, include object and string
        set.remove(cf.getThisClassName());
        set.add("java/lang/String");
        set.add("java/lang/Object");
        
        /* Write the includes */
        
        ps.println("// includes");
        ps.println("#import <Foundation/Foundation.h>");
        ps.println("#import \"j2oc.h\"");
        ps.println("#import \"" + Util.formatClass(cf.getThisClassName()) + ".h\"");
        for (String str: set) {
            ps.println("#import \"" + Util.formatClass(str) + ".h\"");
        }
        ps.println();
        
        /*
         * Write static field storage declarations
         */
        
        for (Field f: cf.getFields()) {
            if ((f.getAccessFlags() & ClassFile.ACC_STATIC) != 0) {
                String fname = Util.formatField(f.getName(),cf.getThisClassName());
                
                /*
                 * Do we have a constant associated with this?
                 */
                
                StringBuffer b = new StringBuffer();
                b.append("static ").append(Util.formatDataType(f.getDescriptor()));
                b.append(' ').append(fname);

                for (Attribute a: f.getAttributes()) {
                    if (a instanceof ConstantAttribute) {
                        b.append(" = ");
                        b.append(Util.formatConstant((ConstantAttribute)a));
                        break;
                    }
                }
                b.append(';');
                ps.println(b.toString());
            }
        }
        ps.println();
        
        /*
         * Generate the class header
         */
        
        ps.println("@implementation " + Util.formatClass(cf.getThisClassName()) + ";");
        for (Field f: cf.getFields()) {
            if ((f.getAccessFlags() & ClassFile.ACC_STATIC) == 0) {
                String fname = Util.formatField(f.getName(),cf.getThisClassName());
                ps.println("@synthesize " + fname + ";");
            }
        }

        ps.println();

        /*
         * Generate static getters/setters
         */
        for (Field f: cf.getFields()) {
            if ((f.getAccessFlags() & ClassFile.ACC_STATIC) != 0) {
                String fieldName = Util.formatField(f.getName(),cf.getThisClassName());
                String setFieldName = Util.formatSetField(f.getName(),cf.getThisClassName());

                /*
                 * This is a static field.
                 * 
                 * write + (op)field
                 * write + (void)setField:(op)v;
                 */
                
                StringBuffer b = new StringBuffer();
                DataType dt = f.getDescriptor();
                
                b.append("+ (").append(Util.formatDataType(dt)).append(')');
                b.append(fieldName);
                ps.println(b);
                ps.println("{");
                ps.println("    return " + fieldName + ";");
                ps.println("}");
                ps.println();
                
                b = new StringBuffer();
                b.append("+ (void)");
                b.append(setFieldName).append(":(");
                b.append(Util.formatDataType(dt)).append(")value");
                ps.println(b);
                ps.println("{");
                
                if (dt.getPrimitiveType() == DataType.T_ADDR) {
                    ps.println("    [value retain];");
                    ps.println("    [" + fieldName + " release];");
                }
                ps.println("    " + fieldName + " = value;");
                ps.println("}");
                ps.println();
            }
        }
        
        ps.println();
        
        /*
         * Write field constant initializers if any exist
         */
        boolean constInit = false;
        for (Field f: cf.getFields()) {
            if ((f.getAccessFlags() & ClassFile.ACC_STATIC) == 0) {
                for (Attribute a: f.getAttributes()) {
                    if (a instanceof ConstantAttribute) {
                        constInit = true;
                        break;
                    }
                }
            }
        }
        if (constInit) {
            /*
             * Generate the default -(id)init method which populates the field
             * values with the constant values specified above.
             */
            
            ps.println("- (id)init");
            ps.println("{");
            ps.println("    if (nil != (self = [super init])) {");
            
            for (Field f: cf.getFields()) {
                if ((f.getAccessFlags() & ClassFile.ACC_STATIC) == 0) {
                    for (Attribute a: f.getAttributes()) {
                        if (a instanceof ConstantAttribute) {
                            
                            /*
                             * Write 'field = value'
                             */

                            StringBuffer b = new StringBuffer();
                            b.append("        ");
                            b.append(Util.formatField(f.getName(), cf.getThisClassName()));
                            b.append(" = ");
                            b.append(Util.formatConstant((ConstantAttribute)a));
                            b.append(';');
                            ps.println(b.toString());
                            break;
                        }
                    }
                }
            }
            
            ps.println("    }");
            ps.println("    return self;");
            ps.println("}");
        }
        
        
        /* Write the dealloc object for fields that are address references */
        boolean flag = false;
        for (Field f: cf.getFields()) {
            if ((f.getAccessFlags() & ClassFile.ACC_STATIC) == 0) {
                if (f.getDescriptor().getPrimitiveType() == DataType.T_ADDR) {
                    flag = true;
                    break;
                }
            }
        }
        if (flag) {
            ps.println("- (void)dealloc");
            ps.println("{");
            for (Field f: cf.getFields()) {
                if ((f.getAccessFlags() & ClassFile.ACC_STATIC) == 0) {
                    if (f.getDescriptor().getPrimitiveType() == DataType.T_ADDR) {
                        String fieldName = Util.formatField(f.getName(),cf.getThisClassName());
                        ps.println("    [" + fieldName + " release];");
                    }
                }
            }
            ps.println("    [super dealloc];");
            ps.println("}");
            ps.println("");
        }

        /*
         * Write methods
         */
        
        WriteOCMethod wm = new WriteOCMethod(cf,missing);
        for (Method m: methods) {
            wm.writeMethod(ps, m, rewriteOut);
        }
        
        ps.println();
        ps.println("@end");
        ps.println();
        ps.close();
    }
    
    /**
     * Into the missing directory create headers for all of the missing methods
     * @throws IOException 
     */
    private static void writeMissing() throws IOException
    {
        for (String cname: missing.getDefinedMethods().keySet()) {
            HashSet<FMIConstant> fmiSet = missing.getDefinedMethods().get(cname);
            
            /*
             * Write header for this missing class
             */
            
            String name = Util.formatClass(cname) + ".h";
            File out = new File(args.getMissingDirectory(),name);
            FileOutputStream fos = new FileOutputStream(out);
            PrintStream ps = new PrintStream(fos);
            
            ps.println("/*  " + name);
            ps.println(" *");
            ps.println(" *      Autogenerated by j2oc (http://www.j2oc.com) at " + new Date().toString());
            ps.println(" *");
            ps.println(" *      Generated from missing methods and fields for " + cname);
            ps.println(" *      Generated template should be completed to make this work.");
            ps.println(" */");
            ps.println();
            ps.println("// includes");
            ps.println("#import <Foundation/Foundation.h>");
            ps.println("#import \"j2oc.h\"");
            ps.println();
            
            /*
             * Write missing classes
             */
            
            TreeSet<String> classes = new TreeSet<String>();
            for (FMIConstant fmi: fmiSet) {
                classes.add(fmi.getClassName());
                
                MethodDescriptor d = fmi.getDescriptor();
                DataType dt = d.getRet();
                if (dt.getClassName() != null) classes.add(dt.getClassName());
                if (d.getArgs() != null) {
                    for (DataType dn: d.getArgs()) {
                        if (dn.getClassName() != null) classes.add(dn.getClassName());
                    }
                }
            }
            classes.remove(cname);
            
            if (!classes.isEmpty()) {
                for (String str: classes) {
                    ps.println("@class " + Util.formatClass(str) + ";");
                }
                ps.println();
            }
            
            /*
             * Write the interface
             */
            
            ps.println("@interface " + Util.formatClass(cname) + ": NSObject");

            /*
             * Iterate fields
             */
            
            ps.println("{");
            for (FMIConstant fmi: fmiSet) {
                MethodDescriptor m = fmi.getDescriptor();
                if ((m.getArgs() == null) && (!missing.isStatic(fmi))) {
                    String fieldName = Util.formatField(fmi);

                    /*
                     * This is a field.
                     */
                    
                    StringBuffer b = new StringBuffer();
                    DataType dt = m.getRet();
                    b.append("    ").append(Util.formatDataType(dt));
                    if (b.charAt(b.length()-1) != '*') {
                        b.append(' ');
                    }
                    b.append(fieldName).append(";");
                    ps.println(b.toString());
                }
            }
            ps.println("}");
            for (FMIConstant fmi: fmiSet) {
                MethodDescriptor m = fmi.getDescriptor();
                if ((m.getArgs() == null) && (!missing.isStatic(fmi))) {
                    String fieldName = Util.formatField(fmi);

                    /*
                     * This is a field.
                     */
                    
                    StringBuffer b = new StringBuffer();
                    DataType dt = m.getRet();
                    b.append("@property ");
                    if (dt.getPrimitiveType() == DataType.T_ADDR) {
                        b.append("(retain) ");
                    }
                    b.append(Util.formatDataType(dt));
                    if (b.charAt(b.length()-1) != '*') {
                        b.append(' ');
                    }
                    b.append(fieldName).append(";");
                    ps.println(b.toString());
                }
            }
            for (FMIConstant fmi: fmiSet) {
                MethodDescriptor m = fmi.getDescriptor();
                if ((m.getArgs() == null) && (missing.isStatic(fmi))) {
                    String fieldName = Util.formatField(fmi);
                    String setFieldName = Util.formatSetField(fmi);
                    
                    /*
                     * This is a static field.
                     * 
                     * write + (op)field
                     * write + (void)setField:(op)v;
                     */
                    
                    StringBuffer b = new StringBuffer();
                    DataType dt = m.getRet();
                    
                    b.append("+ (").append(Util.formatDataType(dt)).append(')');
                    b.append(fieldName).append(';');
                    ps.println(b);
                    
                    b = new StringBuffer();
                    b.append("+ (void)");
                    b.append(setFieldName).append(":(");
                    b.append(Util.formatDataType(dt)).append(")value;");
                    ps.println(b);
                }
            }
            
            /*
             * Iterate methods
             */
            
            ps.println();
            
            for (FMIConstant fmi: fmiSet) {
                MethodDescriptor m = fmi.getDescriptor();
                if (m.getArgs() != null) {
                    /*
                     * This is a method
                     */
                    
                    ps.println(Util.formatMethodFromFMI(fmi,missing.isStatic(fmi)) + ";");
                }
            }
            
            ps.println();
            ps.println("@end");
            ps.println();
        }
    }

}


