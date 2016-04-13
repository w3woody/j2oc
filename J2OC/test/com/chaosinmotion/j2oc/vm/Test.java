/*  Test.java
 *
 *  Created on Feb 6, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.chaosinmotion.j2oc.oc.Util;
import com.chaosinmotion.j2oc.oc.WriteOCMethod;
import com.chaosinmotion.j2oc.vm.code.Code;
import com.chaosinmotion.j2oc.vm.data.Method;

public class Test
{
    private static ArrayList<ClassFile> classList = new ArrayList<ClassFile>();

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        try {
            File f = new File(".");
            recurseLoad(f);
            
            /*
             * Run the class list and write the functions
             */
            
            for (ClassFile cf: classList) {
                System.out.println("@implementation " + Util.formatClass(cf.getThisClassName()));
                System.out.println("");

                WriteOCMethod w = new WriteOCMethod(cf,null);
                for (Method m: cf.getMethods()) {
                    Code c = m.getCode();
                    
                    if (c == null) {
                        System.out.println("// Method " + cf.getThisClassName() + "." + m.getName() + " is null");
                    } else {
                        w.writeMethod(System.out, m, null);
                    }
                }

                System.out.println("@end");
                System.out.println("");
                System.out.println("");
            }
        }
        catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }

    private static void recurseLoad(File f) throws IOException
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
            FileInputStream fis = new FileInputStream(f);
            ClassFile cf = new ClassFile(fis);
            fis.close();
            classList.add(cf);
        }
    }

}


