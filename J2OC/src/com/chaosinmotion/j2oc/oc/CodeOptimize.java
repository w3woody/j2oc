/*  CodeOptimize.java
 *
 *  Created on Feb 12, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.oc;

import com.chaosinmotion.j2oc.vm.code.Code;
import com.chaosinmotion.j2oc.vm.code.CodeSearch;
import com.chaosinmotion.j2oc.vm.code.op.InvokeOp;
import com.chaosinmotion.j2oc.vm.code.op.MultiArrayOp;
import com.chaosinmotion.j2oc.vm.code.op.NewArrayOp;
import com.chaosinmotion.j2oc.vm.code.op.NewOp;
import com.chaosinmotion.j2oc.vm.code.op.Op;
import com.chaosinmotion.j2oc.vm.data.DataType;

/**
 * In the future there may be a lot more logic here. But for now we simply scan the
 * parsed code in a method and determine (a) if I need a preamble for memory management,
 * and (b) if I need a try/catch preamble for exception handling.
 * 
 * If I require a memory preamble I always require a try/catch preamble. I can require
 * a try/catch preamble without a memory preamble if I do any exception handling, but
 * if I don't, then there is no reason why the stack cannot unwind while skiping me--
 * since I only need an unwind if I need to release memory.
 */
public class CodeOptimize
{
    private Code code;
    private boolean needMemoryPreamble;
    private boolean needExceptionPreamble;
    private boolean classInit;
    
    /**
     * Construct my object
     * @param c
     */
    public CodeOptimize(Code c, boolean ci)
    {
        code = c;
        classInit = ci;
    }
    
    
    /**
     * Run my optimize routine. Figures out if I need a memory preamble and if I
     * need an exception preamble.
     * 
     * A memory preamble is used to hold temporary memory passed around from an
     * invocation into another routine. If I invoke another routine I also need
     * an exception preamble in case it throws. I also need an exception preamble
     * if I handle exceptions regardless.
     */
    public void optimize()
    {
        needMemoryPreamble = false;
        needExceptionPreamble = false;
        
        CodeSearch search = new CodeSearch();
        
        /*
         * Figure out if I need a memory preamble. This is true if I'm dealing
         * with a method call that returns an address. Note that in the future if
         * we have code flow analysis we can discount a function call that returns
         * an address if I just pass that address straight up again.
         */
        
        search.doLambda(code,new CodeSearch.OpLambda() {
            public boolean invokeOnOperator(Op op)
            {
                if (op instanceof InvokeOp) {
                    if (DataType.T_ADDR == ((InvokeOp)op).getPrimitiveType()) {
                        needMemoryPreamble = true;
                        needExceptionPreamble = true;
                        return true;
                    }
                }
                if ((op instanceof NewOp) || (op instanceof NewArrayOp) || (op instanceof MultiArrayOp)) {
                    if (classInit) {
                        needMemoryPreamble = true;
                        return true;
                    }
                }
                return false;
            }
        });
        if (needMemoryPreamble) return;
        
        /*
         * If we get here, then we don't need a memory preamble. See if we need
         * a try/catch preamble
         */
        
        if (code.getCodeAttribute().getExceptionTable().length > 0) {
            needExceptionPreamble = true;
            return;
        }
    }


    public boolean needMemoryPreamble()
    {
        return needMemoryPreamble;
    }

    public boolean needExceptionPreamble()
    {
        return needExceptionPreamble;
    }
}


