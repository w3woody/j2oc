/*  NewOp.java
 *
 *  Created on Feb 6, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.code.op;

import com.chaosinmotion.j2oc.vm.data.DataType;
import com.chaosinmotion.j2oc.vm.data.constants.Constant;

public class NewOp extends Op
{
    private Constant constant;

    public NewOp(Constant c)
    {
        constant = c;
    }

    @Override
    public boolean isWide()
    {
        return false;
    }
    
    public Constant getConstant()
    {
        return constant;
    }

    @Override
    public int getPrimitiveType()
    {
        return DataType.T_ADDR;
    }
}


