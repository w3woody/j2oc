/*  MissingMethods.java
 *
 *  Created on Feb 9, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.oc;

import java.util.HashMap;
import java.util.HashSet;

import com.chaosinmotion.j2oc.vm.data.constants.FMIConstant;

/**
 * Store the missing methods and fields as we build our code. This tracks what we
 * didn't declare, so we can generate the headers for those classes
 */
public class Missing
{
    private HashSet<String> definedClasses;
    private HashMap<String,HashSet<FMIConstant>> definedMethods;
    private HashSet<FMIConstant> staticMethods;
    
    public Missing()
    {
        definedClasses = new HashSet<String>();
        definedMethods = new HashMap<String,HashSet<FMIConstant>>();
        staticMethods = new HashSet<FMIConstant>();
    }
    
    /**
     * Add this class. This indicates the class is defined and is not missing
     * @param cname
     */
    public void addClass(String cname)
    {
        definedClasses.add(cname);
        definedMethods.remove(cname);
    }
    
    /**
     * Add this method. This determines if the FMI is not part of a defined class,
     * and if it is not, adds it.
     * @param fmi
     */
    public void addFMIConstant(FMIConstant fmi, boolean staticCall)
    {
        String cname = fmi.getClassName();
        if (definedClasses.contains(cname)) return;
        
        HashSet<FMIConstant> set = definedMethods.get(cname);
        if (set == null) {
            set = new HashSet<FMIConstant>();
            definedMethods.put(cname, set);
        }
        set.add(fmi);
        
        if (staticCall) staticMethods.add(fmi);
    }
    
    public boolean isStatic(FMIConstant c)
    {
        return staticMethods.contains(c);
    }
    
    public HashMap<String,HashSet<FMIConstant>> getDefinedMethods()
    {
        return definedMethods;
    }
}


