/*  NewRefArrayOp.java
 *
 *  Created on Feb 6, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.code.op;

import com.chaosinmotion.j2oc.vm.data.DataType;
import com.chaosinmotion.j2oc.vm.data.constants.Constant;

public class NewRefArrayOp extends Op
{
    private Constant constant;
    private Op op;

    public NewRefArrayOp(Constant c, Op tmpOp)
    {
        constant = c;
        op = tmpOp;
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

    public Op getOp()
    {
        return op;
    }

    @Override
    public int getPrimitiveType()
    {
        return DataType.T_ADDR;
    }
}


