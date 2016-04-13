/*  RecallTemporaryVariableOp.java
 *
 *  Created on Feb 6, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.code.op;

import com.chaosinmotion.j2oc.vm.data.DataType;

public class RecallTemporaryVariableOp extends Op
{
    private int type;
    private int var;
    private boolean stmp;
    
    public RecallTemporaryVariableOp(int t, int v)
    {
        type = t;
        var = v;
        stmp = false;
    }

    @Override
    public int getPrimitiveType()
    {
        return type;
    }

    @Override
    public boolean isWide()
    {
        return ((type == DataType.T_LONG) || (type == DataType.T_DOUBLE));
    }

    public int getType()
    {
        return type;
    }

    public int getVar()
    {
        return var;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + type;
        result = prime * result + var;
        result = prime * result + (stmp ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        RecallTemporaryVariableOp other = (RecallTemporaryVariableOp)obj;
        if (type != other.type) return false;
        if (var != other.var) return false;
        if (stmp != other.stmp) return false;
        return true;
    }

    public void setSTmp(boolean b)
    {
        stmp = true;
    }
    
    public boolean isSTmp()
    {
        return stmp;
    }
}


