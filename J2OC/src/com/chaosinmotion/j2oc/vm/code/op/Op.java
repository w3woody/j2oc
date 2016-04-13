/*  Op.java
 *
 *  Created on Feb 6, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.code.op;

/**
 * Represents an operator. An operator is an intermediate operation (generally a math
 * operation) that makes up an expression. Operators are not top-level items, but are
 * intermediate terms.
 */
public abstract class Op
{
    private int tmp;                // temporary variable created for duplicate
    private boolean duplicate;      // op was duplicated; signal we need temporary register
    
    /**
     * Return true if this is a wide operator
     * @return
     */
    public abstract boolean isWide();

    /**
     * Mark this operator as a duplicate; it means we duplicated this on the stack
     * during evaluation
     */
    public void markDuplicate()
    {
        duplicate = true;
    }
    
    /**
     * Is this a duplicate operator--and will we need a temporary variable to store
     * the results here for later evaluation?
     * @return
     */
    public boolean isDuplicate()
    {
        return duplicate;
    }
    
    public int getTmpVariable()
    {
        return tmp;
    }
    
    public void setTmpVariable(int i)
    {
        tmp = i;
    }

    /**
     * Returns the primitive storage type used for temporary variables for holding the
     * results of this operation.
     * @return
     */
    public abstract int getPrimitiveType();
}


