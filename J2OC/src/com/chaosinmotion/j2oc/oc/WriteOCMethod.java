/*  WriteOCMethod.java
 *
 *  Created on Feb 7, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.oc;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.LinkedList;

import com.chaosinmotion.j2oc.vm.ClassFile;
import com.chaosinmotion.j2oc.vm.code.Code;
import com.chaosinmotion.j2oc.vm.code.inst.*;
import com.chaosinmotion.j2oc.vm.code.op.*;
import com.chaosinmotion.j2oc.vm.data.DataType;
import com.chaosinmotion.j2oc.vm.data.Method;
import com.chaosinmotion.j2oc.vm.data.ParseUtil;
import com.chaosinmotion.j2oc.vm.data.attributes.CodeAttribute;
import com.chaosinmotion.j2oc.vm.data.constants.*;

/**
 * Given a Java VM code segment that has been parsed into intermediate instructions,
 * this writes the body of the method.
 */
public class WriteOCMethod
{
    private boolean fVirtual;
    private int fTmp;
    private LinkedList<String> decl;
    private HashSet<Var> varSet;
    private ClassFile fClassFile;
    private Missing fMissing;
    private boolean fNeedMemoryPreamble;
    private boolean fNeedExceptionPreamble;

    /*
     * Our VM has three types of variables: the variables that were declared
     * by Java, the temporary variables that are generated as part of stack
     * preservation across instruction invocation, and variables created with
     * the 'dup' operation copies and pastes an operator on the operator stack.
     */
    private static final int TEMPORARYVAR = 1;
    private static final int FUNCTIONVAR = 2;
    private static final int DUPTMPVAR = 3;
    private static final int RETVAR = 4;
    private static final int SECONDTEMPVAR = 5;

    /**
     * Used to track if an internal variable has already been declared or not.
     */
    private static class Var
    {
        private int varType;
        private int type;
        private int var;
        
        Var(int vt, int t, int v)
        {
            varType = vt;
            type = t;
            var = v;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + type;
            result = prime * result + var;
            result = prime * result + varType;
            return result;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            Var other = (Var)obj;
            if (type != other.type) return false;
            if (var != other.var) return false;
            if (varType != other.varType) return false;
            return true;
        }
    }
    
    /**
     * Construct an objective C writer
     * @param missing 
     * @param out
     */
    public WriteOCMethod(ClassFile cf, Missing missing)
    {
        fClassFile = cf;
        fMissing = missing;
    }
    
    /**
     * Find the precedence of this operator. This is, for binary operators,
     * the precedence of the binary operation. For all else, this returns a
     * very large number, indicating we don't need to be wrapped in parenthesis
     * @param op
     * @return
     */
//    private int precedence(Op op)
//    {
//        if (op instanceof BinaryOp) {
//            BinaryOp b = (BinaryOp)op;
//            
//            switch (b.getOperator()) {
//                case BinaryOp.ADD:
//                case BinaryOp.SUB:
//                    return 1;
//                case BinaryOp.MUL:
//                case BinaryOp.DIV:
//                    return 2;
//                default:
//                    return 0;   // dunno, which means surround with parens
//            }
//        }
//        
//        return 99;
//    }
//    
    /**
     * Perform compare operator; this compares two values and returns -1, 0 or 1.
     * @param i
     * @return
     */
    private String formatCompareOperator(BinaryOp i) throws IOException
    {
        int oper = i.getOperator();
        
        if (oper == BinaryOp.CMP) {
            /* Long compare operation from lcmp */
            return "j2oc_lcmp(" + formatOperator(i.getLeft()) + "," + formatOperator(i.getRight()) + ")";
        } else if (oper == BinaryOp.CMPG) {
            /* double or float compare */
            return "j2oc_fcmpg(" + formatOperator(i.getLeft()) + "," + formatOperator(i.getRight()) + ")";
        } else {
            return "j2oc_fcmpl(" + formatOperator(i.getLeft()) + "," + formatOperator(i.getRight()) + ")";
        }
    }
    
    /**
     * Floating point mod operation
     * @param i
     * @return
     */
    private String formatFloatModOperator(BinaryOp i) throws IOException
    {
        /* math.h */
        return "j2oc_fmod(" + formatOperator(i.getLeft()) + "," + formatOperator(i.getRight()) + ")";
    }
    
    /**
     * Unsigned shift integer or long integer operation
     * @param i
     * @return
     */
    private String formatUnsignedShiftOperator(BinaryOp i) throws IOException
    {
        int type = i.getPrimitiveType();
        if (type == DataType.T_LONG) {
            /* long integer right shift */
            return "(((uint64_t)(" + formatOperator(i.getLeft()) + ")) >> (" + formatOperator(i.getRight()) + "))";
        } else {
            /* integer right shift */
            return "(((uint32_t)(" + formatOperator(i.getLeft()) + ")) >> (" + formatOperator(i.getRight()) + "))";
        }
    }
    
    /**
     * Format a constant into a type description. This needs to be a symbolic
     * reference to a class, array or interface type.
     * @param c
     * @return
     */
    private String formatType(Constant c)
    {
        ClassConstant cc = (ClassConstant)c;
        return Util.formatClass(cc.getClassName());
    }
    
    /**
     * Format a temporary. This is the primitive type used for temporary storage
     * and is always one of the basic stack types.
     * @param type
     * @param var
     * @return
     */
    private String formatVariable(int type, int var, int varType)
    {
        if (varType == FUNCTIONVAR) {
            if ((var == 0) && (type == DataType.T_ADDR) && fVirtual) {
                return "self";          // the self or this pointer
            }
        }
        
        /*
         * Format the name
         */
        
        StringBuffer buf = new StringBuffer();
        
        switch (varType) {
            case TEMPORARYVAR:  buf.append("tmp");  break;
            case SECONDTEMPVAR: buf.append("stmp"); break;
            case FUNCTIONVAR:   buf.append("v");    break;
            case DUPTMPVAR:     buf.append("d");    break;
            case RETVAR:        buf.append("r");    break;
        }
        buf.append(var).append("_");
        switch (type) {
            case DataType.T_ADDR:   buf.append('a');    break;
            case DataType.T_DOUBLE: buf.append('d');    break;
            case DataType.T_FLOAT:  buf.append('f');    break;
            case DataType.T_INT:    buf.append('i');    break;
            case DataType.T_LONG:   buf.append('l');    break;
            default:                buf.append('?');    break;
        }
        String name = buf.toString();
        
        /*
         * Determine if we've declared this yet. If we haven't, then prepend
         * our list
         */
        
        Var v = new Var(varType,type,var);
        if (!varSet.contains(v)) {
            varSet.add(v);
            
            StringBuffer st = new StringBuffer();
            st.append("    ");
            switch (type) {
                case DataType.T_ADDR:   st.append("id");        break;
                case DataType.T_DOUBLE: st.append("double");    break;
                case DataType.T_FLOAT:  st.append("float");     break;
                case DataType.T_INT:    st.append("int32_t");   break;
                case DataType.T_LONG:   st.append("int64_t");  break;
                default:                st.append("???");       break;
            }
            
            st.append(' ').append(name).append(';');
            decl.addFirst(st.toString());        // add declaration, like  int32_t v0_i;
        }
        
        /*
         * Return the variable.
         */
        return name;
    }
        
    /**
     *  Short cut for a format operator which writes the duplicate variable
     *  storage component if needed
     */
    private String formatOperator(Op op) throws IOException
    {
        return formatOperator(op,true);
    }
    
    /**
     * Given an operator, generates the appropriate Objective C statement to
     * perform that operation.
     * @param op
     * @return
     */
    private String formatOperator(Op op, boolean writeDup) throws IOException
    {
        if (op.isDuplicate() && writeDup) {
            /*
             * This operator is a duplicate and we need to write it out as
             * a duplicate. This inserts the temporary variable declaration
             * in the header array (to be prepended to the output data), and
             * creates either a (var = op) or var, depending on if we've already
             * written this.
             */
            
            if (op.getTmpVariable() == 0) {
                op.setTmpVariable(++fTmp);
                
                /*
                 * Handle dup by inserting an instruction. This works around the
                 * problem of a dup in a single instruction.
                 */
                
                String prim = formatVariable(op.getPrimitiveType(),op.getTmpVariable(),DUPTMPVAR);
                String inst = "    " + prim + " = " + formatOperator(op,false) + ";";
                decl.add(inst);
            }
            return formatVariable(op.getPrimitiveType(),op.getTmpVariable(),DUPTMPVAR);
        }
        
        /*
         * If we get here, figure out the operator and return the correct string
         * to evaluate to this
         */
        StringBuffer buf = new StringBuffer();
        
        if (op instanceof ArrayLength) {
            ArrayLength i = (ArrayLength)op;
            
            /*
             * We assume all of our arrays in Objective C are objects which
             * implement the same protocols as NSMutableArray; thus, this would
             * be [(thing) count];
             */
            
            buf.append("[").append(formatOperator(i.getOp())).append(" arrayLength]");
        } else if (op instanceof ArrayLoadOp) {
            ArrayLoadOp i = (ArrayLoadOp)op;
            
            /*
             * Array load operator; does a get operation. In Objective C we
             * assume we're using something like NSArray
             */
            
            buf.append("[").append(formatOperator(i.getArrayOp()));
            String methodName = formatMethodName(i.getType());
            buf.append(' ').append(methodName);
            buf.append(": ");
            buf.append(formatOperator(i.getIndexOp()));
            buf.append("]");
        } else if (op instanceof BinaryOp) {
            /*
             * Binary operation; this takes a left and a right, and performs
             * the operation between the two.
             */
            BinaryOp i = (BinaryOp)op;
            int oper = i.getOperator();
            int ptype = i.getPrimitiveType();
            
            /* Trap special cases */
            if ((oper == BinaryOp.CMPG) || (oper == BinaryOp.CMPL) || (oper == BinaryOp.CMP)) {
                return formatCompareOperator(i);
            }
            if ((oper == BinaryOp.MOD) && ((ptype == DataType.T_FLOAT) || (ptype == DataType.T_DOUBLE))) {
                return formatFloatModOperator(i);
            }
            if (oper == BinaryOp.UNSIGNEDSHIFTRIGHT) {
                return formatUnsignedShiftOperator(i);
            }

            /* Grab the left, right, and sort out precedence */
            int myPrec = 0; // kludge: force write of parenthesis always.
            int lhs = 0;
            int rhs = 0;
//            int myPrec = precedence(i);
//            int lhs = precedence(i.getLeft());
//            int rhs = precedence(i.getRight());
            
            String left = formatOperator(i.getLeft());
            String right = formatOperator(i.getRight());
            
            /* Write the expression with parenthesis as needed */
            if ((myPrec == 0) || (lhs < myPrec)) {
                buf.append('(').append(left).append(')');
            } else {
                buf.append(left);
            }
            
            switch (i.getOperator()) {
                case BinaryOp.ADD:  buf.append(" + ");  break;
                case BinaryOp.SUB:  buf.append(" - ");  break;
                case BinaryOp.MUL:  buf.append(" * ");  break;
                case BinaryOp.DIV:  buf.append(" / ");  break;
                case BinaryOp.MOD:  buf.append(" % ");  break;
                case BinaryOp.SHIFTLEFT:
                    buf.append(" << ");
                    break;
                case BinaryOp.SHIFTRIGHT:
                    buf.append(" >> ");
                    break;
                case BinaryOp.AND:  buf.append(" & ");  break;
                case BinaryOp.OR:   buf.append(" | ");  break;
                case BinaryOp.XOR:  buf.append(" ^ ");  break;
            }
            
            if ((myPrec == 0) || (rhs < myPrec)) {
                buf.append('(').append(right).append(')');
            } else {
                buf.append(right);
            }
            
        } else if (op instanceof CheckCast) {
            /*
             * A checked cast does a cast operation to a different class type,
             * throwing an exception if the cast cannot be performed.
             */
            CheckCast i = (CheckCast)op;
            
            buf.append("((").append(formatType(i.getConstant()));
            buf.append(" *)(").append(formatOperator(i.getOp())).append("))");
        
        } else if (op instanceof ConstantOp) {
            ConstantOp i = (ConstantOp)op;
            
            Constant c = i.getConstant();
            if (c == null) {
                buf.append("nil");
            } else if (c instanceof IntegerConstant) {
                buf.append(((IntegerConstant)c).getValue());
            } else if (c instanceof LongConstant) {
                long val = ((LongConstant)c).getValue();
                if (val == Long.MIN_VALUE) {
                    // work around bug in GCC 4.2
                    buf.append("0x8000000000000000L");
                } else if (val == Long.MAX_VALUE) {
                    // work around bug in GCC 4.2
                    buf.append("0x7FFFFFFFFFFFFFFFL");
                } else {
                    buf.append(((LongConstant)c).getValue()).append('L');
                }
            } else if (c instanceof DoubleConstant) {
                double dval = ((DoubleConstant)c).getValue();
                String str = Util.formatDouble(dval,false);
                buf.append(str);
            } else if (c instanceof FloatConstant) {
                double dval = ((FloatConstant)c).getValue();
                String str = Util.formatDouble(dval,true);
                buf.append(str);
            } else if (c instanceof ClassConstant) {
                String className = Util.formatClass(((ClassConstant)c).getClassName());
                buf.append("[J2OCClass classWithClass:[").append(className).append(" class]]");
            } else {
                // j2oc_convert
                buf.append(Util.formatString((StringConstant)c));
            }
        
        } else if (op instanceof ConvertOp) {
            /*
             * Perform a cast from one type to another. For us, we simply
             * cast to the primitive type specified. This is only a convert
             * between int, long, float and double
             */
            ConvertOp i = (ConvertOp)op;
            buf.append("((");
            switch (i.getPrimitiveType()) {
                case DataType.T_INT:    buf.append("int32_t");  break;
                case DataType.T_LONG:   buf.append("int64_t");  break;
                case DataType.T_DOUBLE: buf.append("double");   break;
                case DataType.T_FLOAT:  buf.append("float");    break;
                case DataType.T_BYTE:   buf.append("int8_t");   break;
                case DataType.T_CHAR:   buf.append("unichar");  break;
                case DataType.T_SHORT:  buf.append("int16_t");  break;
            }
            buf.append(")(");
            buf.append(formatOperator(i.getOp()));
            buf.append("))");
        
        } else if (op instanceof GetFieldOp) {
            /**
             * Get the field for this object. We use the Objective C 
             * tools for getting and setting fields
             * 
             * [pThis fieldName];
             */
            
            GetFieldOp i = (GetFieldOp)op;
            
            String fname = Util.formatField(i.getField());
            buf.append('[').append(formatOperator(i.getThis()));
            buf.append(' ').append(fname).append(']');

            if (fMissing != null) {
                fMissing.addFMIConstant(i.getField(),false);
            }
        
        } else if (op instanceof GetStaticOp) {
            /*
             * Get static field. We convert these using getters and setters.
             * Our getter has the same name as the field; thus,
             * 
             * [ClassName field]
             */

            GetStaticOp i = (GetStaticOp)op;
            buf.append('[');
            buf.append(Util.formatClass(i.getField().getClassName()));
            buf.append(' ');
            buf.append(Util.formatField(i.getField()));
            buf.append(']');

            if (fMissing != null) {
                fMissing.addFMIConstant(i.getField(),true);
            }

        } else if (op instanceof InstanceOf) {
            /*
             * We determine if this is an instance of the specified type.
             * This is done using isKindOf:
             * 
             * [(op) isKindOfClass:[<class> class]]
             */
        
            InstanceOf i = (InstanceOf)op;
            
            buf.append("[(");
            buf.append(formatOperator(i.getOp()));
            buf.append(") isKindOfClass:[");
            buf.append(formatType(i.getConstant()));
            buf.append(" class]]");
            
        } else if (op instanceof InvokeOp) {
            /*
             * This invokes a function call.
             */
            InvokeOp i = (InvokeOp)op;
            
            /*
             * This invokes depending on the type. If the type is
             * invokevirtual or invokeinterface, we invoke looking like:
             * 
             * [pThis methodname :a :b ...];
             * 
             * for invokestatic we have:
             * 
             * [class methodname:a :b ...]
             */
            
            if (fMissing != null) {
                fMissing.addFMIConstant(i.getConstant(),(i.getType() == Invoke.STATIC));
            }
            
            String methodName = i.getConstant().getFieldName();
            String className = i.getConstant().getClassName();
            
            methodName = Util.formatMethod(methodName, i.getConstant().getDescriptor(), className);
            buf.append('[');
            if (i.getType() == Invoke.STATIC) {
                /* Static invocation */
                buf.append(Util.formatClass(className));
            } else if (i.getType() == Invoke.SPECIAL) {
                /*
                 * For special, if we have a special function <init>, we call
                 * like we call a virtual function
                 */
                
                if (methodName.equals("<init>") || !fVirtual) {
                    buf.append('(').append(formatOperator(i.getThis())).append(')');
                } else if (className.equals(fClassFile.getSuperClassName())) {
                    buf.append("super");
                } else {
//                    buf.append("self");
                    buf.append(formatOperator(i.getThis()));
                }
            } else {
                /* How do we handle special? */
                buf.append('(').append(formatOperator(i.getThis())).append(')');
            }
            buf.append(' ');
            buf.append(methodName);
            
            boolean first = true;
            for (Op arg: i.getArgs()) {
                if (!first) {
                    buf.append(' ');
                } else {
                    first = false;
                }
                buf.append(':');
                buf.append(formatOperator(arg));
            }
            buf.append(']');
        
        } else if (op instanceof MultiArrayOp) {
            MultiArrayOp i = (MultiArrayOp)op;
            
            /*
             * A multi-array is simply a basic array (created using the same
             * class as NewArrayOp), embedded into a multi-array. We create this
             * using a rather ugly little expression:
             * 
             * [[[J2OCRefArray alloc] initWithType:(type_index) dimensions:(len),...] autorelease]
             * 
             * It is the responsibility of the RTL to know the dimensionality of the
             * underlying object.
             * 
             * We use J2OCRefArray because we eventually build an array of arrays, which
             * are themselves objects.
             */
            
            Op[] args = i.getArgs();
            DataType dt = ParseUtil.parseClassOrArrayType(i.getConstant().getClassName());
            
            buf.append("[[[J2OCRefArray alloc] initArrayWithType:").append(dt.getType());
            buf.append(" dimensions:").append(args.length);
            for (Op arg: args) {
                buf.append(',').append(formatOperator(arg));
            }
            buf.append("] autorelease]");
        
        } else if (op instanceof NewArrayOp) {
            NewArrayOp i = (NewArrayOp)op;
            
            /*
             * Internally we use one of the following array classes in order
             * to internally implement my array:
             * 
             * J2OCBooleanArray
             * J2OCCharArray
             * J2OCFloatArray
             * J2OCDoubleArray
             * J2OCByteArray
             * J2OCShortArray
             * J2OCIntArray
             * J2OCLongArray
             * 
             * This creates an instance of my array object with the size required;
             * assuming all honor 'initWithSize:' method.
             */

            buf.append("[[[");
            switch (i.getType().getType()) {
                case DataType.T_BOOLEAN:
                    buf.append("J2OCBooleanArray");
                    break;
                case DataType.T_CHAR:
                    buf.append("J2OCCharArray");
                    break;
                case DataType.T_FLOAT:
                    buf.append("J2OCFloatArray");
                    break;
                case DataType.T_DOUBLE:
                    buf.append("J2OCDoubleArray");
                    break;
                case DataType.T_BYTE:
                    buf.append("J2OCByteArray");
                    break;
                case DataType.T_SHORT:
                    buf.append("J2OCShortArray");
                    break;
                case DataType.T_INT:
                    buf.append("J2OCIntArray");
                    break;
                case DataType.T_LONG:
                    buf.append("J2OCLongArray");
                    break;
                default:
                    buf.append("???");
            }
            buf.append(" alloc] initArrayWithSize:");
            buf.append(formatOperator(i.getOp()));
            buf.append("] autorelease]");
        
        } else if (op instanceof NewOp) {
            NewOp i = (NewOp)op;
            
            /*
             * Performs a new operation on a class type. This simply does an
             * alloc/init; a special call does the constructor call. Thus we
             * do
             * 
             * [[[<class> alloc] init] autorelease]
             * 
             * Note that if we're creating a string, we should call the initJ
             * special routine, which gives us a J2OCString override of the
             * NSString class.
             */
            
            String fop = formatType(i.getConstant());
            buf.append("[[[").append(formatType(i.getConstant()));
            if (fop.equals("java_lang_String")) {
                buf.append(" alloc] initJ] autorelease]");
            } else {
                buf.append(" alloc] init] autorelease]");
            }
        
        } else if (op instanceof NewRefArrayOp) {
            
            /*
             * Creates a reference array; an array of id. This
             * is returned as J2OCRefArray
             */
            NewRefArrayOp i = (NewRefArrayOp)op;
            buf.append("[[[J2OCRefArray alloc] initArrayWithSize:");
            buf.append(formatOperator(i.getOp()));
            buf.append("] autorelease]");
        
        } else if (op instanceof RecallTemporaryVariableOp) {
            RecallTemporaryVariableOp i = (RecallTemporaryVariableOp)op;
            
            buf.append(formatVariable(i.getType(),i.getVar(),(i.isSTmp() ? SECONDTEMPVAR : TEMPORARYVAR)));
        
        } else if (op instanceof UnaryOp) {
            /*
             * Unary operator. Thus far, all we have is negate
             */
            UnaryOp i = (UnaryOp)op;
            
            switch (i.getOperator()) {
                case UnaryOp.NEGATE:
                    buf.append("-");
            }
            buf.append("(").append(formatOperator(i.getOp())).append(")");
        
        } else if (op instanceof VariableOp) {
            /*
             * Recall variable contents.
             */
            
            VariableOp i = (VariableOp)op;
        
            buf.append(formatVariable(i.getType().getPrimitiveType(),i.getVar(),FUNCTIONVAR));
        }
        
        return buf.toString();
    }
    
    private static String formatCompareOp(IfBinaryCompare c)
    {
        switch (c.getCompare()) {
            default:                    return "??";
            case IfBinaryCompare.EQ:    return "==";
            case IfBinaryCompare.NE:    return "!=";
            case IfBinaryCompare.LT:    return "<";
            case IfBinaryCompare.LE:    return "<=";
            case IfBinaryCompare.GT:    return ">";
            case IfBinaryCompare.GE:    return ">=";
        }
    }
    
    private static String formatUnaryCompareOp(IfCompare c)
    {
        switch (c.getCompare()) {
            default:                    return "?? 0";
            case IfCompare.EQ:          return "== 0";
            case IfCompare.NE:          return "!= 0";
            case IfCompare.LT:          return "< 0";
            case IfCompare.LE:          return "<= 0";
            case IfCompare.GT:          return "> 0";
            case IfCompare.GE:          return ">= 0";
            case IfCompare.NONNULL:     return "!= nil";
            case IfCompare.NULL:        return "== nil";
        }
    }
    
    /**
     * Write the method header and method body for this method.
     * @param ps
     * @param m
     */
    public void writeMethod(PrintStream ps, Method m, PrintStream rewriteOut) throws IOException
    {
        /* Write the preamble header */
        String str = Util.formatMethodEntry(fClassFile,m);
        boolean virtual = (m.getAccessFlags() & ClassFile.ACC_STATIC) == 0;
        int index = virtual ? 1 : 0;
        
        if (str.equals("- (void)__init_com_chaosinmotion_mathcore_EvalEngine___com_chaosinmotion_mathcore_CalculatorCallback:(com_chaosinmotion_mathcore_CalculatorCallback *)v1_a")) {
            System.out.println("###");
        }
        
        ps.println("/** " + fClassFile.getThisClassName() + "." + m.getName() + " */");
        
        if (m.getCode() == null) {
            if (0 != (m.getAccessFlags() & ClassFile.ACC_NATIVE)) {
                /*
                 * Write rewrite XML rules
                 */
                
                String methodName = Util.formatMethod(m.getName(),m.getDescriptor(),fClassFile.getThisClassName());
                if (rewriteOut != null) {
                    rewriteOut.println("    <method name=\"" + methodName + "\" class=\"" + fClassFile.getThisClassName() + "\">");
                    rewriteOut.println("        <![CDATA[");
                    rewriteOut.println(str);
                    rewriteOut.println("{");
                    rewriteOut.println("    ### Native code");
                    rewriteOut.println("}");
                    rewriteOut.println("]]>");
                    rewriteOut.println("    </method>");
                    rewriteOut.println();
                }
                
                /*
                 * Get the rewrite XML if it exists, and use the stored method found
                 * there to do the rewrite
                 */
                
                RewriteSupport support = RewriteSupport.getRewrite();
                String rewrite = null;
                if (support != null) {
                    rewrite = support.getMethod(methodName,fClassFile.getThisClassName());
                }
                if (rewrite != null) {
                    ps.println(rewrite);
                } else {
                    ps.println(str);
                    ps.println("{");
                    ps.println("#warning Unimplemented native code method");
                    ps.println("}");
                }
            } else {
                ps.println(str);
                ps.println("{");
                ps.println("    NSLog(@\"ERROR: Called into abstract method\");");
                ps.println("    NSLog(@\"'" + str + "' is abstract.\");");
                
                DataType dt = m.getDescriptor().getRet();
                if (dt.getType() != DataType.T_VOID) {
                    if (dt.getPrimitiveType() == DataType.T_ADDR) {
                        ps.println("    return nil;");
                    } else {
                        ps.println("    return 0;");
                    }
                }
                
                ps.println("}");
            }
            ps.println();
            return;
        }
        
        // Get code optimization information
        boolean classInit = m.getName().equalsIgnoreCase("<clinit>");
        CodeOptimize coptimize = new CodeOptimize(m.getCode(),classInit);
        coptimize.optimize();
        
        fNeedMemoryPreamble = coptimize.needMemoryPreamble();
        fNeedExceptionPreamble = coptimize.needExceptionPreamble();
        
        // Write function header
        ps.println(str);
        ps.println("{");

        // Build the variables and predefine
        varSet = new HashSet<Var>();            // track if variables declared
        for (DataType arg: m.getDescriptor().getArgs()) {
            Var v = new Var(FUNCTIONVAR,arg.getPrimitiveType(),index);
            varSet.add(v);
            ++index;
            if (arg.isWide()) ++index;
        }
        
        LinkedList<String> l = write(m.getCode(), virtual);
        for (String s: l) {
            ps.println(s);
        }
        ps.println("}");
        ps.println("");
    }
    
    /**
     * Write the code body provided
     * @param code
     */
    private LinkedList<String> write(Code code, boolean virtual) throws IOException
    {
        fTmp = 0;
        fVirtual = virtual;
        
        decl = new LinkedList<String>();        // used to format the function
        
        /*
         * Write the Objective C prefix. We set up several things here: the
         * pool for autorelease, the variable to track exception state, and
         * the exception code itself.
         * 
         * NSAutoreleasePool *pool = [[NSAutoreleasePool alloc] init];
         * int exceptionState = 0;
         * set up exception stack and handle exception table
         */
        
        boolean hasExceptions = (code.getCodeAttribute().getExceptionTable().length != 0);
        if (hasExceptions && fNeedExceptionPreamble) {
            // We only need to write this if we handle exceptions
            decl.add("    int exceptionState = 0;");
        }

        if (fNeedMemoryPreamble) {
            decl.add("    NSAutoreleasePool *pool = [[NSAutoreleasePool alloc] init];");
        }
        
        if (fNeedExceptionPreamble) {
            decl.add("    jmp_buf store;");
            decl.add("    memcpy(store, GExceptState, sizeof(jmp_buf));");
            decl.add("    if (setjmp(GExceptState)) {");
            if (hasExceptions) {
                decl.add("        tmp0_a = GException;");
                decl.add("");
            }
            for (CodeAttribute.ExceptionTableItem e: code.getCodeAttribute().getExceptionTable()) {
                StringBuffer b = new StringBuffer();

                b.append("        ");
                b.append("if ((").append(e.getStartPc());
                b.append(" <= exceptionState) && (exceptionState < ").append(e.getEndPc());
                b.append(")");
                if (e.getCatchType() != 0) {
                    b.append(" && ([GException isKindOfClass:[");
                    b.append(Util.formatClass(e.getCatchClassName()));
                    b.append(" class]])");
                }
                b.append(") goto l_").append(e.getHandlerPc()).append(";");

                decl.add(b.toString());
            }
            if (hasExceptions) {
                decl.add("");
            }
            
            decl.add("        // default handling");
            if (fNeedMemoryPreamble) {
                decl.add("        [GException retain];");
                decl.add("        [pool release];");
                decl.add("        [GException autorelease];");
            }
            decl.add("        memcpy(GExceptState,store,sizeof(jmp_buf));");
            decl.add("        j2oc_longjmp(GExceptState,1);");
            decl.add("    }");
        }
        
        /*
         * We have two possibilities to handle try/catch exceptions.
         * 
         * The first possibility is to rearrange the code segments below,
         * reordering things so we can use structured exception try/catch
         * code.
         * 
         * We chickened out and used the second possibility of using setjmp/
         * longjmp--and building our own handler. We simply track the various
         * instruction ranges and on catch we run through and process the
         * exception explicitly ourselves.
         * 
         * We do this by noting the current goto state if we have exception
         * handling taking place.
         */
        boolean writeGotoFlag = code.getCodeAttribute().getExceptionTable().length > 0;

        /*
         * Write the instructions. This inserts the correct sequence at each point
         */
        
        for (Code.CodeSeg cs: code.getInst()) {
            if (cs.getInst() != null) {
                if (cs.isJump() || cs.isHandler()) {
                    if (cs.isHandler()) {
                        decl.add("// Exception handler entry point");
                    }
                    decl.add("l_" + cs.getStartPc() + ":");
                }
                if (writeGotoFlag) {
                    decl.add("    exceptionState = " + cs.getStartPc() + "; // exception state");
                }
                
                for (Instruction i: cs.getInst()) {
                    /*
                     * Now write the instructions themselves
                     */
                    if (i instanceof ArrayStore) {
                        /*
                         * Array store operation: results in a[index] = value;
                         * 
                         * Because arrays are objects, we use
                         * 
                         * [a replaceObjectAtIndex:index withObject:vallue]
                         */
                        ArrayStore as = (ArrayStore)i;
                        
                        String array = formatOperator(as.getArrayRef());
                        String index = formatOperator(as.getIndex());
                        String value = formatOperator(as.getValue());
                        String methodName = formatReplaceMethodName(as.getDataType());
                        decl.add("    [" + array + ' ' + methodName + ": " + index + " withObject:" + value + "];");
                        
                    } else if (i instanceof EvalInstruction) {
                        /*
                         * Eval; evaluates operator: results in op;
                         */
                        EvalInstruction e = (EvalInstruction)i;
                        decl.add("    " + formatOperator(e.getOp()) + ";");
                        
                    } else if (i instanceof Goto) {
                        /*
                         * Goto: results in goto l_xxx;
                         */
                        Goto g = (Goto)i;
                        decl.add("    goto l_" + g.getPc() + ";");
                        
                    } else if (i instanceof IfBinaryCompare) {
                        /*
                         * Binary compare and goto; generates 
                         * 
                         * if (cmp) goto l_xxx;
                         */
                        IfBinaryCompare g = (IfBinaryCompare)i;
                        
                        String statement = "    if ((" + formatOperator(g.getLeft()) + ") " +
                                formatCompareOp(g) + " (" + formatOperator(g.getRight()) +
                                ")) goto l_" + g.getPc() + ";";
                        decl.add(statement);
                        
                    } else if (i instanceof IfCompare) {
                        /*
                         * Goto: results in goto label;
                         */
                        IfCompare g = (IfCompare)i;
                        
                        String statement = "    if ((" + formatOperator(g.getOp()) + ") " +
                                formatUnaryCompareOp(g) + ") goto l_" + g.getPc() + ";";
                        decl.add(statement);
                        
                    } else if (i instanceof Increment) {
                        /*
                         * Increment. This is always type integer, and increments
                         * the variable by the amount given. This generates the
                         * statement vN_i += val;
                         */
                        Increment g = (Increment)i;
                        
                        decl.add("    " + formatVariable(DataType.T_INT,g.getVar(),FUNCTIONVAR) + " += " + g.getVal() + ";");
                        
                    } else if (i instanceof Invoke) {
                        /*
                         * This invokes a function call.
                         */
                        Invoke g = (Invoke)i;
                        if (fMissing != null) {
                            fMissing.addFMIConstant(g.getConstant(),(g.getType() == Invoke.STATIC));
                        }

                        /*
                         * This invokes depending on the type. If the type is
                         * invokevirtual or invokeinterface, we invoke looking like:
                         * 
                         * [pThis methodname :a :b ...];
                         * 
                         * for invokestatic we have:
                         * 
                         * [class methodname:a :b ...]
                         */
                        
                        StringBuffer buf = new StringBuffer();
                        
                        String methodName = g.getConstant().getFieldName();
                        String className = g.getConstant().getClassName();
                        
                        String name = Util.formatMethod(methodName, g.getConstant().getDescriptor(), className);
                        buf.append("    [");
                        if (g.getType() == Invoke.STATIC) {
                            /* Static invocation */
                            buf.append(Util.formatClass(className));
                        } else if (g.getType() == Invoke.SPECIAL) {
                            /*
                             * For special, if we have a special function <init>, we call
                             * like we call a virtual function
                             */
                            
                            if (methodName.equals("<init>") || !fVirtual) {
                                buf.append('(').append(formatOperator(g.getThis())).append(')');
                            } else if (className.equals(fClassFile.getSuperClassName())) {
                                buf.append("super");
                            } else {
//                                buf.append("self");
                                buf.append(formatOperator(g.getThis()));
                            }
                        } else {
                            /* How do we handle special? */
                            buf.append('(').append(formatOperator(g.getThis())).append(')');
                        }
                        buf.append(' ');
                        buf.append(name);
                        
                        boolean first = true;
                        for (Op arg: g.getArgs()) {
                            if (!first) {
                                buf.append(' ');
                            } else {
                                first = false;
                            }
                            buf.append(':');
                            buf.append(formatOperator(arg));
                        }
                        buf.append(']');
                        buf.append(';');
                        decl.add(buf.toString());
                        
                    } else if (i instanceof PutField) {
                        /*
                         * field = value; does [pThis setField:value];
                         */

                        PutField g = (PutField)i;
                        
                        String setFieldName = Util.formatSetField(g.getField());
                        
                        StringBuffer buf = new StringBuffer();
                        buf.append("    [").append(formatOperator(g.getThis())).append(' ');
                        buf.append(setFieldName).append(':');
                        buf.append(formatOperator(g.getVal()));
                        buf.append("];");
                        decl.add(buf.toString());

                        if (fMissing != null) {
                            fMissing.addFMIConstant(g.getField(),false);
                        }
                        
                    } else if (i instanceof PutStatic) {
                        /*
                         * put static; [Class setFieldName:op];
                         */

                        PutStatic g = (PutStatic)i;
                        
                        String setFieldName = Util.formatSetField(g.getField());
                        
                        StringBuffer buf = new StringBuffer();
                        buf.append("    [");
                        buf.append(Util.formatClass(g.getField().getClassName()));
                        buf.append(' ');
                        buf.append(setFieldName);
                        buf.append(':');
                        buf.append(formatOperator(g.getVal()));
                        buf.append(']');
                        buf.append(';');
                        decl.add(buf.toString());

                        if (fMissing != null) {
                            fMissing.addFMIConstant(g.getField(),true);
                        }

                    } else if (i instanceof Return) {
                        /*
                         * Perform a return statement with no return value;
                         */
//                      Return g = (Return)i;
                        if (fNeedMemoryPreamble) {
                            decl.add("    [pool release];");
                        }
                        if (fNeedExceptionPreamble) {
                            decl.add("    memcpy(GExceptState,store,sizeof(jmp_buf));");
                        }
                        decl.add("    return;");
                        
                    } else if (i instanceof ReturnValue) {
                        /*
                         * Perform a return statement;
                         */
                        ReturnValue g = (ReturnValue)i;
                        
                        /*
                         * If this is type T_ADDR, then we need
                         * to write:
                         * 
                         * ret = op;
                         * [ret retain];
                         * [pool release];
                         * [retain autorelease];
                         * return ret;
                         */
                        
                        if (g.getOp().getPrimitiveType() == DataType.T_ADDR) {
                            /*
                             * If this is an address object, we need to retain the object,
                             * release the pool, then autorelease the object, so we don't
                             * accidently delete when we return
                             */
                            decl.add("    " + formatVariable(DataType.T_ADDR,0,RETVAR) + " = " + formatOperator(g.getOp()) + ";");
                            if (fNeedMemoryPreamble) {
                                decl.add("    [" + formatVariable(DataType.T_ADDR,0,RETVAR) + " retain];");
                                decl.add("    [pool release];");
                                decl.add("    [" + formatVariable(DataType.T_ADDR,0,RETVAR) + " autorelease];");
                            }
                            if (fNeedExceptionPreamble) {
                                decl.add("    memcpy(GExceptState,store,sizeof(jmp_buf));");
                            }
                            decl.add("    return " + formatVariable(DataType.T_ADDR,0,RETVAR) + ";");
                        } else {
                            Op op = g.getOp();
                            decl.add("    " + formatVariable(op.getPrimitiveType(),0,RETVAR) + " = " + formatOperator(op) + ";");
                            if (fNeedMemoryPreamble) {
                                decl.add("    [pool release];");
                            }
                            if (fNeedExceptionPreamble) {
                                decl.add("    memcpy(GExceptState,store,sizeof(jmp_buf));");
                            }
                            decl.add("    return " + formatVariable(op.getPrimitiveType(),0,RETVAR) + ";");
                        }
                        
                    } else if (i instanceof StoreTemporaryVariable) {
                        /*
                         * Store in temporary variable;
                         */
                        StoreTemporaryVariable g = (StoreTemporaryVariable)i;
                        
                        decl.add("    " + formatVariable(g.getType(),g.getVar(),g.isSTmp() ? SECONDTEMPVAR : TEMPORARYVAR) + " = " + formatOperator(g.getOp()) + ";");
                        
                    } else if (i instanceof StoreVariable) {
                        /*
                         * Store in variable;
                         */
                        StoreVariable g = (StoreVariable)i;

                        decl.add("    " + formatVariable(g.getType().getPrimitiveType(),g.getVar(),FUNCTIONVAR) + " = " + formatOperator(g.getVal()) + ";");

                    } else if (i instanceof SwitchInstruction) {
                        /*
                         * Switch statement;
                         */
                        SwitchInstruction g = (SwitchInstruction)i;
                        
                        /*
                         * This will generate
                         * switch (op) {
                         *    case match: goto l_xxx;
                         *    ...
                         *    default: goto l_xxx;
                         * }
                         */

                        decl.add("    switch (" + formatOperator(g.getOp()) + ") {");
                        for (SwitchInstruction.MatchPair m: g.getPairs()) {
                            decl.add("        case " + m.getMatch() + ": goto l_" + m.getPc() + ";");
                        }
                        decl.add("        default: goto l_" + g.getDefaultByte() + ";");
                        decl.add("    }");
                        
                    } else if (i instanceof TableInstruction) {
                        /*
                         * Table instruction; gnerates a continuous switch;
                         */
                        TableInstruction g = (TableInstruction)i;
                        
                        /*
                         * This will generate the following:
                         * 
                         * switch (op) {
                         *     case low: goto l_xxx;
                         *     case low+1: goto l_xxx;
                         *     ...
                         *     case high: goto l_xxx;
                         *     default: goto l_xxx;
                         * }
                         */
                        decl.add("    switch (" + formatOperator(g.getOp()) + ") {");
                        
                        int v = g.getLowByte();
                        for (int pc: g.getPc()) {
                            decl.add("        case " + v + ": goto l_" + pc + ";");
                            ++v;
                        }
                        
                        decl.add("        default: goto l_" + g.getDefaultByte() + ";");
                        decl.add("    }");
                        
                    } else if (i instanceof ThrowInstruction) {
                        /*
                         * Throw exception. Note that this isn't handled very well in
                         * Objective C; what I really should do is insert code to
                         * release my shared pool.
                         * 
                         * For our process we use
                         *      GException = op;
                         *      [GException retain];
                         *      [pool release];
                         *      [GException autorelease];
                         *      memcpy(GExceptState,store,sizeof(jmp_buf));
                         *      longjmp(GExceptState,1);
                         */
                        ThrowInstruction g = (ThrowInstruction)i;
                        
                        decl.add("    GException = " + formatOperator(g.getOp()) + ";");
                        if (fNeedMemoryPreamble) {
                            decl.add("    [GException retain];");
                            decl.add("    [pool release];");
                            decl.add("    [GException autorelease];");
                        }
                        decl.add("    j2oc_longjmp(GExceptState,1);");
                        
                    } else if (i instanceof Monitor) {
                        /*
                         * Monitor instruction. We generate a monitor instruction,
                         * though we don't do anything with that.
                         */
                        
                        Monitor m = (Monitor)i;
                        if (m.isEnterFlag()) {
                            decl.add("    j2oc_monitor_enter(" + formatOperator(m.getOp()) + ");");
                        } else {
                            decl.add("    j2oc_monitor_exit(" + formatOperator(m.getOp()) + ");");
                        }

                    }
                }
            }
        }
        
        /*
         * Once we get here, dump into print stream
         */
        return decl;
    }

    private String formatReplaceMethodName(int dataType)
    {
        switch (dataType) {
            case DataType.T_ADDR:
                return "replaceRefAtIndex";
            case DataType.T_BOOLEAN:
            case DataType.T_BYTE:
                return "replaceByteAtIndex";
            case DataType.T_CHAR:
                return "replaceCharAtIndex";
            case DataType.T_DOUBLE:
                return "replaceDoubleAtIndex";
            case DataType.T_FLOAT:
                return "replaceFloatAtIndex";
            case DataType.T_INT:
                return "replaceIntAtIndex";
            case DataType.T_LONG:
                return "replaceLongAtIndex";
            case DataType.T_SHORT:
                return "replaceShortAtIndex";
            default:
                return "???";
        }
    }

    private String formatMethodName(DataType dataType)
    {
        if (dataType.getArray() != 0) return "objectAtIndex";
        
        switch (dataType.getType()) {
            case DataType.T_ADDR:
                return "refAtIndex";
            case DataType.T_BOOLEAN:
            case DataType.T_BYTE:
                return "byteAtIndex";
            case DataType.T_CHAR:
                return "charAtIndex";
            case DataType.T_DOUBLE:
                return "doubleAtIndex";
            case DataType.T_FLOAT:
                return "floatAtIndex";
            case DataType.T_INT:
                return "intAtIndex";
            case DataType.T_LONG:
                return "longAtIndex";
            case DataType.T_SHORT:
                return "shortAtIndex";
            default:
                return "???";
        }
    }
}


