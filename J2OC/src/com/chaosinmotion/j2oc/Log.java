/*  Log.java
 *
 *  Created on Feb 9, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc;

public class Log
{
    private static boolean verbose;
    private static boolean warning;

    public static void l(String str)
    {
        if (verbose) {
            System.out.println(str);
        }
    }
    
    public static void m(String str)
    {
        System.out.println(str);
    }
    
    public static void w(String str)
    {
        System.out.println(str);
        if (warning) System.exit(-2);
    }

    public static void setState(Arguments args)
    {
        verbose = args.isVerbose();
        warning = args.isStopOnWarning();
    }
}


