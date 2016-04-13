/*  Code.java
 *
 *  Created on Feb 6, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm.code;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.TreeSet;

import com.chaosinmotion.j2oc.vm.ClassParserException;
import com.chaosinmotion.j2oc.vm.code.inst.ArrayStore;
import com.chaosinmotion.j2oc.vm.code.inst.EvalInstruction;
import com.chaosinmotion.j2oc.vm.code.inst.Goto;
import com.chaosinmotion.j2oc.vm.code.inst.IfBinaryCompare;
import com.chaosinmotion.j2oc.vm.code.inst.IfCompare;
import com.chaosinmotion.j2oc.vm.code.inst.Increment;
import com.chaosinmotion.j2oc.vm.code.inst.Instruction;
import com.chaosinmotion.j2oc.vm.code.inst.Invoke;
import com.chaosinmotion.j2oc.vm.code.inst.Monitor;
import com.chaosinmotion.j2oc.vm.code.inst.PutField;
import com.chaosinmotion.j2oc.vm.code.inst.PutStatic;
import com.chaosinmotion.j2oc.vm.code.inst.Return;
import com.chaosinmotion.j2oc.vm.code.inst.ReturnValue;
import com.chaosinmotion.j2oc.vm.code.inst.StoreTemporaryVariable;
import com.chaosinmotion.j2oc.vm.code.inst.StoreVariable;
import com.chaosinmotion.j2oc.vm.code.inst.SwitchInstruction;
import com.chaosinmotion.j2oc.vm.code.inst.TableInstruction;
import com.chaosinmotion.j2oc.vm.code.inst.ThrowInstruction;
import com.chaosinmotion.j2oc.vm.code.op.ArrayLength;
import com.chaosinmotion.j2oc.vm.code.op.ArrayLoadOp;
import com.chaosinmotion.j2oc.vm.code.op.BinaryOp;
import com.chaosinmotion.j2oc.vm.code.op.CheckCast;
import com.chaosinmotion.j2oc.vm.code.op.ConstantOp;
import com.chaosinmotion.j2oc.vm.code.op.ConvertOp;
import com.chaosinmotion.j2oc.vm.code.op.GetFieldOp;
import com.chaosinmotion.j2oc.vm.code.op.GetStaticOp;
import com.chaosinmotion.j2oc.vm.code.op.InstanceOf;
import com.chaosinmotion.j2oc.vm.code.op.InvokeOp;
import com.chaosinmotion.j2oc.vm.code.op.MultiArrayOp;
import com.chaosinmotion.j2oc.vm.code.op.NewArrayOp;
import com.chaosinmotion.j2oc.vm.code.op.NewOp;
import com.chaosinmotion.j2oc.vm.code.op.NewRefArrayOp;
import com.chaosinmotion.j2oc.vm.code.op.Op;
import com.chaosinmotion.j2oc.vm.code.op.RecallTemporaryVariableOp;
import com.chaosinmotion.j2oc.vm.code.op.UnaryOp;
import com.chaosinmotion.j2oc.vm.code.op.VariableOp;
import com.chaosinmotion.j2oc.vm.data.DataType;
import com.chaosinmotion.j2oc.vm.data.attributes.CodeAttribute;
import com.chaosinmotion.j2oc.vm.data.constants.Constant;
import com.chaosinmotion.j2oc.vm.data.constants.FMIConstant;

/**
 * Base class for each code instruction. For Java, each instruction can be
 * characterized as to the number of operators pulled from the stack, the type
 * of operator pushed back onto the stack, and the next instruction to be
 * executed.
 */
public class Code
{
    //private ClassFile cls;
    private Constant[] pool;
    private CodeAttribute codeAttribute;
    private byte[] code;
    private int pc;
    private ArrayList<CodeSeg> fInst;       // generated instructions
    private int fTmpIndex;                  // temporary variables
        
    /**
     * Initializes this code object with the context of the class, and with the
     * byte array containing our code. This will then reformat the code into
     * an intermediate structure suitable for code generation.
     * @param c
     * @param cl
     */
    public Code(CodeAttribute ca, Constant[] p)
    {
        codeAttribute = ca;
        code = ca.getCode();
        pc = 0;
        pool = p;
        fInst = null;
    }
    
    /**
     * Get the array of code segments representing this code. Each code segment
     * is a chunk of instructions.
     * @return
     */
    public ArrayList<CodeSeg> getInst()
    {
        return fInst;
    }
    
    public CodeAttribute getCodeAttribute()
    {
        return codeAttribute;
    }
    

    /**
     *  Represents a temporary variable used to store and recall operator
     *  stack state across jump boundaries. For code generation this is
     *  unnecessary other than giving the name of the variable that are
     *  used in the code segment associated with this object.
     *  
     *  Internally this is used during intermediate code generate to track
     *  the temporary variables that need to be generated to preserve function
     *  state
     */
    public static class TmpVar
    {
        private int type;
        private int var;
        
        private TmpVar(int t, int v)
        {
            type = t;
            var = v;
        }
        
        /**
         * The fundamental type (T_ADDR, T_INT, etc.) of the operator that should
         * be pushed onto the operation stack prior to the execution of this
         * code segment. This translates into the temporary variable type of
         * the temporary variable used in the instruction stream.
         */
        public int getType()
        {
            return type;
        }
        
        /**
         * The fundamental type (T_ADDR, T_INT, etc.) of the operator that should
         * be pushed onto the operation stack prior to the execution of this
         * code segment. This translates into the temporary variable name of
         * the temporary variable used in the instruction stream.
         */
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
            return result;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            TmpVar other = (TmpVar)obj;
            if (type != other.type) return false;
            if (var != other.var) return false;
            return true;
        }
    }

    /**
     * Stores the state at each label. The state is given by the start PC
     * reference, the end PC reference, and the state. The state is the
     * collection of variables that were used to store the stack when the
     * code segment was exited.
     * 
     * This essentially boils down to tracking a code segment. A code segment
     * is a segment of code which enters at the start instruction and exits
     * at the last instruction; no jump points flow into or out of a segment.
     * 
     * A segment also has one exception catch state defined for it.
     * 
     * Segments may have any number of operators on the operator stack when
     * the code segment is entered, and operators when the segment is left.
     * If this is the case, we unroll the operators into temporary variables.
     * 
     * Our code generator also guarantees all operators are unrolled off the
     * stack when we execute an instruction by copying into temporary variables.
     */
    public static class CodeSeg
    {
        private int startPc;
        private int endPc;
        private LinkedList<TmpVar> state;       // if uninitialized, this segment was not reached
        private ArrayList<Instruction> inst;    // instructions in this segment
        private boolean jump;                   // was this jumped to?
        private boolean handler;                // True if this is a handler segment
        
        private CodeSeg(int s, int e, boolean h)
        {
            startPc = s;
            endPc = e;
            handler = h;
        }

        /**
         * This returns the PC address of the start of this code segment.
         * @return
         */
        public int getStartPc()
        {
            return startPc;
        }

        /**
         *  This returns the PC address for the instruction past this PC
         */
        public int getEndPc()
        {
            return endPc;
        }

        /**
         * REturns a linked list of the temporary variables that are used
         * to enter into this state. For code generation this is unnecessary,
         * other than by giving the variable state when this segment is
         * entered. (If for some reason you pull this segment out and put it
         * into a subroutine, these would be the parameters to pass.)
         * 
         * The variables are also referred to in the instruction operators, so
         * you don't normally need to refer to this.
         * @return
         */
        public LinkedList<TmpVar> getState()
        {
            return state;
        }

        /**
         * The array of instructions to execute in order to execute this
         * segment.
         * @return
         */
        public ArrayList<Instruction> getInst()
        {
            return inst;
        }

        /**
         * True if this is a jump destination
         * @return
         */
        public boolean isJump()
        {
            return jump;
        }

        /**
         * True if this is an exception handler destination
         * @return
         */
        public boolean isHandler()
        {
            return handler;
        }
    }

    /****************************************************************************/
    /*                                                                          */
    /*  Internal support                                                        */
    /*                                                                          */
    /****************************************************************************/

    private int readShort()
    {
        int n = 0x00FF & code[pc++];
        return ((n << 8) | (0x00FF & code[pc++]));
    }
    
    private int readByte()
    {
        return 0x00FF & code[pc++];
    }
    
    private int readInt()
    {
        int n = 0x00FF & code[pc++];
        n = (n << 8) | (0x00FF & code[pc++]);
        n = (n << 8) | (0x00FF & code[pc++]);
        n = (n << 8) | (0x00FF & code[pc++]);
        return n;
    }
    
    private int readWide(boolean wideFlag)
    {
        return wideFlag ? readShort() : readByte();
    }
    
    /****************************************************************************/
    /*                                                                          */
    /*  Instruction Parser                                                      */
    /*                                                                          */
    /****************************************************************************/
    
    /// The last pushed state.
    private LinkedList<TmpVar> fLastState;
    
    /**
     * When this routine is called, we pass in the instruction list and operator
     * list, and add the instruction to the list.
     * 
     * An instruction corresponds to a line of code. Which means we cannot pass
     * an instruction into the list if there are intermediate operations on the
     * stack.
     * 
     * So this pops the operator stack into temporary variables before emitting
     * the instruction, then pushes the operators back onto the stack after the
     * instruction is emitted.
     */
    private void addInstruction(ArrayList<Instruction> inst, final LinkedList<Op> stack, Instruction i)
    {
        /*
         * If there are any operators in the stack, we push them and add the
         * instructions to save them first.
         */
        int index = 0;
        fLastState = new LinkedList<TmpVar>();
        LinkedList<Instruction> storeStack = new LinkedList<Instruction>();
        
        /*
         * Interesting defect:
         * 
         * If we have an instruction in i that is specified in a recall temporary
         * variable operator that we are about to store into the stack entry
         * operator, we need to copy into a secondary temporary variable before we
         * rearrange the stack. Otherwise our saving the operator context will screw up
         * the state for my instruction operation.
         */
        
        final int stackDepth = stack.size();
        final HashSet<TmpVar> recall = new HashSet<TmpVar>();
        if ((i != null) && (stackDepth > 0)) {
            CodeSearch cs = new CodeSearch();
            cs.doLambda(i, new CodeSearch.OpLambda() {
                public boolean invokeOnOperator(Op op)
                {
                    if (op instanceof RecallTemporaryVariableOp) {
                        RecallTemporaryVariableOp rtvo = (RecallTemporaryVariableOp)op;
                        if (rtvo.getVar() < stackDepth) {
                            Op stackOp = stack.get(rtvo.getVar());
                            if (stackOp.getPrimitiveType() == rtvo.getType()) {
                                // We are using a variable we're about to overwrite.
                                // Note that variable and mark that it's coming from an
                                // stmp_var.
                                rtvo.setSTmp(true);
                                recall.add(new TmpVar(rtvo.getType(),rtvo.getVar()));
                            }
                        }
                    }
                    return false;
                }
            });
        }
        
        /*
         * Now walk the stack and see if we're about to overwrite ourselves.
         */
        
        for (Op opRef: stack) {
            CodeSearch cs = new CodeSearch();
            cs.doLambda(opRef,new CodeSearch.OpLambda() {
                public boolean invokeOnOperator(Op op)
                {
                    if (op instanceof RecallTemporaryVariableOp) {
                        RecallTemporaryVariableOp rtvo = (RecallTemporaryVariableOp)op;
                        if (rtvo.getVar() < stackDepth) {
                            Op stackOp = stack.get(rtvo.getVar());
                            if (stackOp.getPrimitiveType() == rtvo.getType()) {
                                // We are using a variable we're about to overwrite.
                                // Note that variable and mark that it's coming from an
                                // stmp_var.
                                rtvo.setSTmp(true);
                                recall.add(new TmpVar(rtvo.getType(),rtvo.getVar()));
                            }
                        }
                    }
                    return false;
                }
            });
        }
        
        /*
         * Write the stack state out to variables prior to calling my instruction,
         * so we can store state
         */
        while (!stack.isEmpty()) {
            Op op = stack.pop();
            
            TmpVar v = new TmpVar(op.getPrimitiveType(),index++);
            
            if (recall.contains(v)) {
                /*
                 * We are about to overwrite a variable that we are using inside
                 * our expression. We need to create an immediate store temporary
                 * variable/recall to copy into an stmp what we pulled from tmp.
                 */
                
                Instruction copy = new StoreTemporaryVariable(v.type,v.var,true,new RecallTemporaryVariableOp(v.type,v.var));
                inst.add(copy);
            }
            
            storeStack.push(new StoreTemporaryVariable(v.type,v.var,false,op));
            fLastState.push(v);
        }
        inst.addAll(storeStack);

        /*
         * We now add our instruction
         */
        
        if (i != null) inst.add(i);
        
        /*
         * Now we recover the stored stack by pushing load operators. This
         * has the advantage of storing stack items across the instruction
         * invocation
         */

        extractStateIntoStack(stack, fLastState);
    }

    /**
     * Given the state object, push recall operators
     * @param stack
     * @param state
     */
    private void extractStateIntoStack(LinkedList<Op> stack, LinkedList<TmpVar> state)
    {
        for (TmpVar t: state) {        // scans in pop order
            stack.push(new RecallTemporaryVariableOp(t.type,t.var));
        }
    }
    
    /**
     * Return value for parseGoto
     */
    private class GotoRet
    {
        // label entry points
        TreeSet<Integer> labels;
        
        // This maps the goto locations for the end of a label segment. Thus
        // this maps the instruction _after_ the goto instruction, and the
        // list of labels that are jumped to.
        //
        // This allows me to chop my code into segments [startPc,endPc), and
        // find where we're jumping to by doing a lookup by endPc.
        TreeMap<Integer,Collection<Integer>> gotos;
        
        // The list of exception handler locations
        TreeSet<Integer> handlers;
        
        GotoRet(TreeSet<Integer> l, 
                TreeMap<Integer,Collection<Integer>> g, 
                TreeSet<Integer> h)
        {
            labels = l;
            gotos = g;
            handlers = h;
        }
    }
    
    /**
     * This runs through the instruction set finding all of the jump instructions,
     * and returns a list of jump label locations. This essentially divides the
     * instruction stream into segments of code which must start with an empty
     * operator stack (meaning it enters between expression boundaries), and
     * ends with one jump instruction. (We also label the instruction after a
     * jump instruction.) This implies all code segments optionally end with
     * a jump or goto instruction.
     * @throws IOException
     */
    private GotoRet parseGoto() throws IOException
    {
        TreeSet<Integer> handlers = new TreeSet<Integer>();
        TreeSet<Integer> ret = new TreeSet<Integer>();
        TreeMap<Integer,Collection<Integer>> g = new TreeMap<Integer,Collection<Integer>>();
        Collection<Integer> tmp;
        
        /*
         * Insert exception start, end points into this thing.
         */
        
        for (CodeAttribute.ExceptionTableItem e: codeAttribute.getExceptionTable()) {
            ret.add(e.getStartPc());
            ret.add(e.getEndPc());
            ret.add(e.getHandlerPc());
            handlers.add(e.getHandlerPc());
        }
        
        /*
         * Parse the instructions, scanning for jump, table and gotos
         */
        pc = 0;
        while (pc < code.length) {
            boolean wide = false;
            
            int curpc = pc;
            int op = 0x00FF & code[pc++];
            if (op == 0xC4) {
                wide = true;
                op = 0x00FF & code[pc++];
            }

            switch (op) {
                // one byte skip instructions
                case 0x10:          // bipush
                case 0x12:          // ldc
                case 0xbc:          // newarray
                    ++pc;
                    break;
                    
                // two byte skip instructions
                case 0x11:  // sipush
                case 0x13:  // ldc_w
                case 0x14:  // ldc2_w
                case 0xbd:  // anewarray
                case 0xbb:  // new
                case 0xb5:  // putfield
                case 0xb4:  // getfield
                case 0xb3:  // putstatic
                case 0xb2:  // getstatic
                case 0xc0:  // checkcast
                case 0xc1:  // instanceof
                case 0xb7:  // invokespecial
                case 0xb8:  // invokestatic
                case 0xb6:  // invokevirtual
                    pc += 2;
                    break;
                
                // three byte skip instructions
                case 0xc5:  // multianewarray
                    pc += 3;
                    break;
                    
                // four byte skip instructions
                case 0xb9:  // invokeinterface
                    pc += 4;
                    break;
                                        
                // byte/short wide instructions
                case 0x19:  // aload
                case 0x18:  // dload
                case 0x17:  // fload
                case 0x15:  // iload
                case 0x16:  // lload
                case 0x3a:  // astore
                case 0x39:  // dstore
                case 0x38:  // fstore
                case 0x36:  // istore
                case 0x37:  // lstore
                    if (wide) {
                        pc += 2;
                    } else {
                        ++pc;
                    }
                    break;
                
                // short/int wide instructions
                case 0x84:  // iinc
                    if (wide) {
                        pc += 4;
                    } else {
                        pc += 2;
                    }
                    break;
                    
                // return instructions (insert label after instruction)
                case 0xac:  // ireturn
                case 0xad:  // lreturn
                case 0xae:  // freturn
                case 0xaf:  // dreturn
                case 0xb0:  // areturn
                case 0xb1:  // return
                    ret.add(pc);
                    g.put(pc, new ArrayList<Integer>());    // mark not falling through
                    break;
                    
                // throw instruction (insert label after instruction)
                case 0xbf:  // athrow
                    ret.add(pc);
                    g.put(pc, new ArrayList<Integer>());    // mark not falling through
                    break;
                    
                // two byte relative jump instructions. We note both
                // where we are and where we're jumping to.
                case 0x99:  // ifeq
                case 0x9a:  // ifne
                case 0x9b:  // iflt
                case 0x9e:  // ifle
                case 0x9c:  // ifge
                case 0x9d:  // ifgt
                case 0xc6:  // ifnull
                case 0xc7:  // ifnonnull
                case 0x9f:  // if_icmpeq
                case 0xa0:  // if_icmpne
                case 0xa1:  // if_icmplt
                case 0xa2:  // if_icmpge
                case 0xa3:  // if_icmpgt
                case 0xa4:  // if_icmple
                case 0xa5:  // if_acmpeq
                case 0xa6:  // if_acmpne
                    // note goto locations
                    tmp = new ArrayList<Integer>();
                    tmp.add(curpc + (short)readShort());
                    tmp.add(pc);
                    g.put(pc, tmp);
                    
                    // Note label locations: at destination and after me
                    ret.addAll(tmp);
                    break;

                case 0xa7:  // goto
                    // note goto locations -- only destination
                    tmp = new ArrayList<Integer>();
                    tmp.add(curpc + (short)readShort());
                    g.put(pc, tmp);
                    
                    // Note label locations: at destination and after me
                    ret.addAll(tmp);
                    ret.add(pc);
                    break;

                    // four byte relative jump instructions
                case 0xc8:  // goto_w
                    // note goto locations
                    tmp = new ArrayList<Integer>();
                    tmp.add(curpc + (short)readShort());
                    g.put(pc,tmp);

                    // note label locations
                    ret.addAll(tmp);
                    ret.add(pc);
                    break;
                    
                case 0xaa:  // tableswitch
                    // note goto locations
                    pc = (pc + 3) & ~0x03;
                    tmp = processTableSwitch(curpc);
                    g.put(pc,tmp);
                    
                    // note label locations
                    ret.addAll(tmp);
                    ret.add(pc);
                    break;
                    
                case 0xab:  // lookupswitch
                    // note goto locations
                    pc = (pc + 3) & ~0x03;
                    tmp = processLookupSwitch(curpc);
                    g.put(pc,tmp);
                    
                    // note label locations
                    ret.addAll(tmp);
                    ret.add(pc);
                    break;
                    
                default:
                    break;          // most instructions are one wide
            }
        }
        
        return new GotoRet(ret,g,handlers);
    }
    
    /**
     * Parse the lookup switch instruction and return a list of the labels we're
     * jumping to
     * @param curpc
     * @return
     */
    private Collection<Integer> processLookupSwitch(int curpc)
    {
        ArrayList<Integer> ret = new ArrayList<Integer>();
        int defaultbyte = readInt() + curpc;
        int npairs = readInt();
        
        ret.add(defaultbyte);
        for (int i = 0; i < npairs; ++i) {
            readInt();  // skip match
            ret.add(readInt() + curpc);
        }
        return ret;
    }

    /**
     * Parse the table switch and return a list of labels we're jumping to
     * @param curpc
     * @return
     */
    private Collection<Integer> processTableSwitch(int curpc)
    {
        ArrayList<Integer> ret = new ArrayList<Integer>();
        int defaultbyte = readInt() + curpc;
        int lowbyte = readInt();
        int highbyte = readInt();
        
        ret.add(defaultbyte);
        for (int x = lowbyte; x <= highbyte; ++x) {
            ret.add(readInt() + curpc);
        }
        return ret;
    }
    

    /**
     * Parse the instruction set
     */
    public void parse() throws IOException
    {
        /*
         * Step 1: Get a list of the labels where we're jumping to. We also
         * add an implicit 0 label and end label. This creates an ordered list
         * of code segments which all obey the property that
         * 
         * (a) each enters with a certain number of operators
         * (b) each should end with an instruction set and push all operators
         * into variables
         * (c) flow may go from one segment to zero or more other segments.
         */
        
        GotoRet r = parseGoto();
        TreeSet<Integer> labels = r.labels;
        
        ArrayList<CodeSeg> l = new ArrayList<CodeSeg>();
        
        int pos = 0;
        int startpc,endpc = 0;
        for (int lpc: labels) {
            startpc = endpc;
            endpc = lpc;
            if (endpc == startpc) continue;
            
            l.add(new CodeSeg(startpc,endpc,r.handlers.contains(startpc)));
            
            ++pos;
        }
        if (endpc != code.length) {             // we had jump to end.
            l.add(new CodeSeg(endpc,code.length,r.handlers.contains(endpc))); // last segment
        }
        
        /*
         * Populate known segments
         */
        l.get(0).state = new LinkedList<TmpVar>();   // start segment has no op stack
        
        for (int i = 1; i < l.size(); ++i) {
            if (l.get(i).handler) {
                // If this is a handler, we know handlers are called with one
                // reference on the stack: the exception that took us here.
                l.get(i).state = new LinkedList<TmpVar>();
                l.get(i).state.push(new TmpVar(DataType.T_ADDR,0));
            }
        }
        
        /*
         * Step 2: run the PC parser along each of the segments. Within
         * each segment, the segment must be entered with an empty operator
         * stack. We achieve this by 
         */
        
        for (;;) {
            boolean update = false;
            
            for (int i = 0; i < l.size(); ++i) {
                CodeSeg cs = l.get(i);
                
                /*
                 * Skip if we've already done this segment
                 */
                if (cs.inst != null) continue;    // already did this
                if (cs.state == null) continue;   // don't have state for this
                
                /*
                 *  Create the state operator stack for this segment. 
                 */
                LinkedList<Op> stack = new LinkedList<Op>();
                extractStateIntoStack(stack, cs.state);
                
                /*
                 * Parse the instructions in this segment
                 */
                
                ArrayList<Instruction> inst = new ArrayList<Instruction>();
                pc = cs.startPc;
                while (pc < cs.endPc) {
                    parseInstruction(inst,stack);
                }
                
                /*
                 * Pop and create 
                 */
                
                /*
                 * At the completion of a segment of instructions we determine
                 * if we have a current stack state object. If we do, then we
                 * just use that state at the start of other code segments. If
                 * we don't, we need to create the state first.
                 */
                
                if (fLastState == null) {
                    // Causes operator state to be saved. This is true only
                    // if there were some stack operations prior to the
                    // completion of this code segment.
                    addInstruction(inst,stack,null);
                }
                
                /*
                 * Promulgate our state to the goto destinations.
                 */
                Collection<Integer> dest = r.gotos.get(pc);
                if (dest == null) {
                    dest = new ArrayList<Integer>();
                    dest.add(pc);
                }
                for (int locpc: dest) {
                    int x;
                    for (x = 0; x < l.size(); ++x) {
                        CodeSeg csx = l.get(x);
                        if (csx.startPc == locpc) {
                            if (csx.state == null) {
                                csx.state = fLastState;
                            }
                            csx.jump = true;
                            break;
                        }
                    }
                    if (x >= l.size()) {
                        System.out.println("Should never get here.");
                    }
                }
                
                /*
                 * Copy the instructions into our list
                 */
                cs.inst = inst;
                
                /*
                 * Now construct a temporary stack state object so we can
                 * populate our goto locations
                 */
                
                update = true;
            }
            
            if (update == false) break;
        }
        
        
        /*
         * Step 3: Build the final instruction set construction by running
         * our labels and assembling the instructions (with intersperced
         * labels)
         */
        
        fInst = l;
//        fInst = new ArrayList<Instruction>();
//        for (CodeSeg cs: l) {
//            if (cs.inst != null) {
//                if (cs.jump || cs.handler) {    
//                    fInst.add(new GotoLabelInstruction(cs.startPc));
//                }
//                if (writeGotoFlag) {
//                    fInst.add(new ExceptionMarker(cs.startPc));
//                }
//                fInst.addAll(cs.inst);
//            }
//        }
    }

    /**
     * Internal routine which parses the current instruction at the instruction
     * PC counter. This generates and updates the appropriate inner state
     * contents and advances the PC to the next instruction.
     */
    private void parseInstruction(ArrayList<Instruction> inst, LinkedList<Op> stack) throws IOException
    {
        /*
         * Clear the last state. Works in conjunction with the addInstruction 
         * routine to signal if we have the proper state of the universe
         * pushed prior to the completion of a code segment.
         */
        fLastState = null;
        
        /*
         * Start the parsing
         */
        boolean wide = false;
        int tmp,tmp2,x;
        Op tmpOp,tmpOp2,tmpOp3;
        LinkedList<Op> args;

        /*
         * Get the operator. If wide, set the wide flag and fetch the next
         */
        
        int curpc = pc;
        int op = 0x00FF & code[pc++];
        if (op == 0xC4) {
            wide = true;
            op = 0x00FF & code[pc++];
        }
        
        
        switch (op) {
            /*
             * Stack operations
             */
            
            /*
             * Stack operations: push contents
             */
            case 0x10:  // bipush
                stack.push(new ConstantOp((int)(byte)readByte()));
                break;
            case 0x11:  // sipush
                stack.push(new ConstantOp((int)(short)readShort()));
                break;
            case 0x12:  // ldc
                stack.push(new ConstantOp(pool[readByte()]));
                break;
            case 0x13:  // ldc_w
            case 0x14:  // ldc2_w
                stack.push(new ConstantOp(pool[readShort()]));
                break;
                
            case 0x01:  // aconst_null
                stack.push(new ConstantOp());
                break;
            case 0x02:  // iconst -1
                stack.push(new ConstantOp((int)-1));
                break;
            case 0x03:  // iconst 0
                stack.push(new ConstantOp((int)0));
                break;
            case 0x04:  // iconst 1
                stack.push(new ConstantOp((int)1));
                break;
            case 0x05:  // iconst 2
                stack.push(new ConstantOp((int)2));
                break;
            case 0x06:  // iconst 3
                stack.push(new ConstantOp((int)3));
                break;
            case 0x07:  // iconst 4
                stack.push(new ConstantOp((int)4));
                break;
            case 0x08:  // iconst 5
                stack.push(new ConstantOp((int)5));
                break;
            case 0x09:  // lconst 0
                stack.push(new ConstantOp((long)0));
                break;
            case 0x0a:  // lconst 1
                stack.push(new ConstantOp((long)1));
                break;
            case 0x0b:  // fconst 0
                stack.push(new ConstantOp((float)0));
                break;
            case 0x0c:  // fconst 1
                stack.push(new ConstantOp((float)1));
                break;
            case 0x0d:  // fconst 2
                stack.push(new ConstantOp((float)2));
                break;
            case 0x0e:  // dconst 0
                stack.push(new ConstantOp((double)0));
                break;
            case 0x0f:  // dconst 1
                stack.push(new ConstantOp((double)1));
                break;

            /*
             * Stack manipulation
             */
            
            case 0x00:  // nop
                break;
            case 0x57:  // pop
                addInstruction(inst,stack,new EvalInstruction(stack.pop()));
                break;
            case 0x58:  // pop2
                // Note: order is important here: evaluate the deeper item
                // first, since it was pushed first.
                tmpOp = stack.pop();
                if (!tmpOp.isWide()) {
                    addInstruction(inst,stack,new EvalInstruction(stack.pop()));
                }
                addInstruction(inst,stack,new EvalInstruction(tmpOp));
                break;
            case 0x59:  // dup
                tmpOp = stack.peek();
                tmpOp.markDuplicate();
                stack.push(tmpOp);
                break;
            case 0x5c:  // dup2
                tmpOp = stack.get(0);
                if (tmpOp.isWide()) {
                    tmpOp.markDuplicate();
                    stack.push(tmpOp);
                } else {
                    tmpOp2 = stack.get(1);
                    tmpOp2.markDuplicate();
                    stack.push(tmpOp2);
                    tmpOp.markDuplicate();
                    stack.push(tmpOp);
                }
                break;
            case 0x5a:  // dup_x1
                tmpOp = stack.peek();
                tmpOp.markDuplicate();
                stack.add(2, tmpOp);
                break;
            case 0x5b:  // dup_x2
                tmpOp = stack.peek();
                tmpOp.markDuplicate();
                stack.add(3, tmpOp);
                break;
            case 0x5d:  // dup2_x1
                tmpOp = stack.peek();
                if (tmpOp.isWide()) {
                    tmpOp.markDuplicate();
                    stack.add(2, tmpOp);
                } else {
                    tmpOp2 = stack.get(1);
                    tmpOp.markDuplicate();
                    stack.add(3, tmpOp);
                    tmpOp.markDuplicate();
                    stack.add(4, tmpOp2);
                }
                break;
            case 0x5e:  // dup2_x2
                tmpOp = stack.peek();
                if (tmpOp.isWide()) {
                    tmpOp2 = stack.get(1);
                    if (tmpOp2.isWide()) {
                        tmpOp.markDuplicate();
                        stack.add(2,tmpOp);
                    } else {
                        tmpOp.markDuplicate();
                        stack.add(3,tmpOp);
                    }
                } else {
                    tmpOp2 = stack.get(1);
                    tmpOp3 = stack.get(2);
                    if (tmpOp3.isWide()) {
                        tmpOp.markDuplicate();
                        stack.add(3,tmpOp);
                        tmpOp2.markDuplicate();
                        stack.add(4,tmpOp2);
                    } else {
                        tmpOp.markDuplicate();
                        stack.add(4,tmpOp);
                        tmpOp2.markDuplicate();
                        stack.add(5,tmpOp2);
                    }
                }
                break;
            case 0x5f:  // swap
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(tmpOp);
                stack.push(tmpOp2);
                break;

            /*
             * Local variables manipulation: push local
             */
            
            case 0x19:  // aload
                tmp = readWide(wide);
                stack.push(new VariableOp(DataType.T_ADDR,tmp));
                break;
            case 0x2a:  // aload_0
                stack.push(new VariableOp(DataType.T_ADDR,0));
                break;
            case 0x2b:  // aload_1
                stack.push(new VariableOp(DataType.T_ADDR,1));
                break;
            case 0x2c:  // aload_2
                stack.push(new VariableOp(DataType.T_ADDR,2));
                break;
            case 0x2d:  // aload_3
                stack.push(new VariableOp(DataType.T_ADDR,3));
                break;
                
            case 0x18:  // dload
                tmp = readWide(wide);
                stack.push(new VariableOp(DataType.T_DOUBLE,tmp));
                break;
            case 0x26:  // dload_0
                stack.push(new VariableOp(DataType.T_DOUBLE,0));
                break;
            case 0x27:  // dload_1
                stack.push(new VariableOp(DataType.T_DOUBLE,1));
                break;
            case 0x28:  // dload_2
                stack.push(new VariableOp(DataType.T_DOUBLE,2));
                break;
            case 0x29:  // dload_3
                stack.push(new VariableOp(DataType.T_DOUBLE,3));
                break;
                
            case 0x17:  // fload
                tmp = readWide(wide);
                stack.push(new VariableOp(DataType.T_FLOAT,tmp));
                break;
            case 0x22:  // fload_0
                stack.push(new VariableOp(DataType.T_FLOAT,0));
                break;
            case 0x23:  // fload_1
                stack.push(new VariableOp(DataType.T_FLOAT,1));
                break;
            case 0x24:  // fload_2
                stack.push(new VariableOp(DataType.T_FLOAT,2));
                break;
            case 0x25:  // fload_3
                stack.push(new VariableOp(DataType.T_FLOAT,3));
                break;
                
            case 0x15:  // iload
                tmp = readWide(wide);
                stack.push(new VariableOp(DataType.T_INT,tmp));
                break;
            case 0x1a:  // iload_0
                stack.push(new VariableOp(DataType.T_INT,0));
                break;
            case 0x1b:  // iload_1
                stack.push(new VariableOp(DataType.T_INT,1));
                break;
            case 0x1c:  // iload_2
                stack.push(new VariableOp(DataType.T_INT,2));
                break;
            case 0x1d:  // iload_3
                stack.push(new VariableOp(DataType.T_INT,3));
                break;
                
            case 0x16:  // lload
                tmp = readWide(wide);
                stack.push(new VariableOp(DataType.T_LONG,tmp));
                break;
            case 0x1e:  // lload_0
                stack.push(new VariableOp(DataType.T_LONG,0));
                break;
            case 0x1f:  // lload_1
                stack.push(new VariableOp(DataType.T_LONG,1));
                break;
            case 0x20:  // lload_2
                stack.push(new VariableOp(DataType.T_LONG,2));
                break;
            case 0x21:  // lload_3
                stack.push(new VariableOp(DataType.T_LONG,3));
                break;

            /*
             * Local variables: pop stack into local variable
             */
            case 0x3a:  // astore
                tmp = readWide(wide);
                tmpOp = stack.pop();
                addInstruction(inst,stack,new StoreVariable(DataType.T_ADDR,tmp,tmpOp));
                break;
            case 0x4b:  // astore_0
                tmpOp = stack.pop();
                addInstruction(inst,stack,new StoreVariable(DataType.T_ADDR,0,tmpOp));
                break;
            case 0x4c:  // astore_1
                tmpOp = stack.pop();
                addInstruction(inst,stack,new StoreVariable(DataType.T_ADDR,1,tmpOp));
                break;
            case 0x4d:  // astore_2
                tmpOp = stack.pop();
                addInstruction(inst,stack,new StoreVariable(DataType.T_ADDR,2,tmpOp));
                break;
            case 0x4e:  // astore_3
                tmpOp = stack.pop();
                addInstruction(inst,stack,new StoreVariable(DataType.T_ADDR,3,tmpOp));
                break;

            case 0x39:  // dstore
                tmp = readWide(wide);
                tmpOp = stack.pop();
                addInstruction(inst,stack,new StoreVariable(DataType.T_DOUBLE,tmp,tmpOp));
                break;
            case 0x47:  // dstore_0
                tmpOp = stack.pop();
                addInstruction(inst,stack,new StoreVariable(DataType.T_DOUBLE,0,tmpOp));
                break;
            case 0x48:  // dstore_1
                tmpOp = stack.pop();
                addInstruction(inst,stack,new StoreVariable(DataType.T_DOUBLE,1,tmpOp));
                break;
            case 0x49:  // dstore_2
                tmpOp = stack.pop();
                addInstruction(inst,stack,new StoreVariable(DataType.T_DOUBLE,2,tmpOp));
                break;
            case 0x4a:  // dstore_3
                tmpOp = stack.pop();
                addInstruction(inst,stack,new StoreVariable(DataType.T_DOUBLE,3,tmpOp));
                break;
                
            case 0x38:  // fstore
                tmp = readWide(wide);
                tmpOp = stack.pop();
                addInstruction(inst,stack,new StoreVariable(DataType.T_FLOAT,tmp,tmpOp));
                break;
            case 0x43:  // fstore_0
                tmpOp = stack.pop();
                addInstruction(inst,stack,new StoreVariable(DataType.T_FLOAT,0,tmpOp));
                break;
            case 0x44:  // fstore_1
                tmpOp = stack.pop();
                addInstruction(inst,stack,new StoreVariable(DataType.T_FLOAT,1,tmpOp));
                break;
            case 0x45:  // fstore_2
                tmpOp = stack.pop();
                addInstruction(inst,stack,new StoreVariable(DataType.T_FLOAT,2,tmpOp));
                break;
            case 0x46:  // fstore_3
                tmpOp = stack.pop();
                addInstruction(inst,stack,new StoreVariable(DataType.T_FLOAT,3,tmpOp));
                break;

            case 0x36:  // istore
                tmp = readWide(wide);
                tmpOp = stack.pop();
                addInstruction(inst,stack,new StoreVariable(DataType.T_INT,tmp,tmpOp));
                break;
            case 0x3b:  // istore_0
                tmpOp = stack.pop();
                addInstruction(inst,stack,new StoreVariable(DataType.T_INT,0,tmpOp));
                break;
            case 0x3c:  // istore_1
                tmpOp = stack.pop();
                addInstruction(inst,stack,new StoreVariable(DataType.T_INT,1,tmpOp));
                break;
            case 0x3d:  // istore_2
                tmpOp = stack.pop();
                addInstruction(inst,stack,new StoreVariable(DataType.T_INT,2,tmpOp));
                break;
            case 0x3e:  // istore_3
                tmpOp = stack.pop();
                addInstruction(inst,stack,new StoreVariable(DataType.T_INT,3,tmpOp));
                break;
            case 0x37:  // lstore
                tmp = readWide(wide);
                tmpOp = stack.pop();
                addInstruction(inst,stack,new StoreVariable(DataType.T_LONG,tmp,tmpOp));
                break;
            case 0x3f:  // lstore_0
                tmpOp = stack.pop();
                addInstruction(inst,stack,new StoreVariable(DataType.T_LONG,0,tmpOp));
                break;
            case 0x40:  // lstore_1
                tmpOp = stack.pop();
                addInstruction(inst,stack,new StoreVariable(DataType.T_LONG,1,tmpOp));
                break;
            case 0x41:  // lstore_2
                tmpOp = stack.pop();
                addInstruction(inst,stack,new StoreVariable(DataType.T_LONG,2,tmpOp));
                break;
            case 0x42:  // lstore_3
                tmpOp = stack.pop();
                addInstruction(inst,stack,new StoreVariable(DataType.T_LONG,3,tmpOp));
                break;

            /*
             * Arrays: Creating
             */
            case 0xbc:  // newarray
                tmp = readByte();
                tmpOp = stack.pop();
                stack.push(new NewArrayOp(tmp,tmpOp));
                break;
            case 0xbd:  // anewarray
                tmp = readShort();
                tmpOp = stack.pop();
                stack.push(new NewRefArrayOp(pool[tmp],tmpOp));
                break;
            case 0xc5:  // multianewarray
                args = new LinkedList<Op>();
                tmp = readShort();
                tmp2 = readByte();
                for (x = 0; x < tmp2; ++x) {
                    args.addFirst(stack.pop());
                }
                stack.push(new MultiArrayOp(pool[tmp],args));
                break;
                
            /*
             * Arays: Pushing array values
             */
            
            case 0x32:  // aaload
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new ArrayLoadOp(tmpOp,tmpOp2,DataType.T_ADDR));
                break;
            case 0x33:  // baload
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new ArrayLoadOp(tmpOp,tmpOp2,DataType.T_BYTE));
                break;
            case 0x34:  // caload
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new ArrayLoadOp(tmpOp,tmpOp2,DataType.T_CHAR));
                break;
            case 0x2e:  // iaload
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new ArrayLoadOp(tmpOp,tmpOp2,DataType.T_INT));
                break;
            case 0x35:  // saload
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new ArrayLoadOp(tmpOp,tmpOp2,DataType.T_SHORT));
                break;
            case 0x2f:  // laload
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new ArrayLoadOp(tmpOp,tmpOp2,DataType.T_LONG));
                break;
            case 0x31:  // daload
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new ArrayLoadOp(tmpOp,tmpOp2,DataType.T_DOUBLE));
                break;
            case 0x30:  // faload
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new ArrayLoadOp(tmpOp,tmpOp2,DataType.T_FLOAT));
                break;

            /*
             * Arrays: Storing values in arrays
             */
            case 0x53:  // aastore
            case 0x54:  // bastore
            case 0x55:  // castore
            case 0x52:  // dastore
            case 0x51:  // fastore
            case 0x4f:  // iastore
            case 0x50:  // lastore
            case 0x56:  // sastore
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                tmpOp3 = stack.pop();
                addInstruction(inst,stack,new ArrayStore(tmpOp3,tmpOp2,tmpOp,op));
                break;

            /*
             * Objects
             */
            case 0xbe:  // arraylength
                stack.push(new ArrayLength(stack.pop()));
                break;
            case 0xbb:  // new
                tmp = readShort();
                stack.push(new NewOp(pool[tmp]));
                break;
            case 0xb5:  // putfield
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                addInstruction(inst,stack,new PutField(tmpOp2,tmpOp,pool[readShort()]));
                break;
            case 0xb4:  // getfield
                tmpOp = stack.pop();
                stack.push(new GetFieldOp(tmpOp,pool[readShort()]));
                break;
                
            case 0xb3:  // putstatic
                tmpOp = stack.pop();
                addInstruction(inst,stack,new PutStatic(tmpOp,pool[readShort()]));
                break;
            case 0xb2:  // getstatic
                stack.push(new GetStaticOp(pool[readShort()]));
                break;

            case 0xc0:  // checkcast
                tmpOp = stack.pop();
                stack.push(new CheckCast(tmpOp,pool[readShort()]));
                break;
            case 0xc1:  // instanceof
                tmpOp = stack.pop();
                stack.push(new InstanceOf(tmpOp,pool[readShort()]));
                break;

                /*
             * Transformations: arithmetic
             */
            case 0x84:  // iinc
                tmp = readWide(wide);
                tmp2 = readWide(wide);
                if (wide) {
                    tmp2 = (int)(short)tmp2;    // sign extend
                } else {
                    tmp2 = (int)(byte)tmp2;
                }
                addInstruction(inst,stack,new Increment(tmp,tmp2));
                break;

            case 0x63:  // dadd
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new BinaryOp(BinaryOp.ADD,DataType.T_DOUBLE,tmpOp2,tmpOp));
                break;
            case 0x62:  // fadd
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new BinaryOp(BinaryOp.ADD,DataType.T_FLOAT,tmpOp2,tmpOp));
                break;
            case 0x60:  // iadd
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new BinaryOp(BinaryOp.ADD,DataType.T_INT,tmpOp2,tmpOp));
                break;
            case 0x61:  // ladd
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new BinaryOp(BinaryOp.ADD,DataType.T_LONG,tmpOp2,tmpOp));
                break;

            case 0x67:  // dsub
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new BinaryOp(BinaryOp.SUB,DataType.T_DOUBLE,tmpOp2,tmpOp));
                break;
            case 0x66:  // fsub
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new BinaryOp(BinaryOp.SUB,DataType.T_FLOAT,tmpOp2,tmpOp));
                break;
            case 0x64:  // isub
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new BinaryOp(BinaryOp.SUB,DataType.T_INT,tmpOp2,tmpOp));
                break;
            case 0x65:  // lsub
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new BinaryOp(BinaryOp.SUB,DataType.T_LONG,tmpOp2,tmpOp));
                break;

            case 0x6b:  // dmul
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new BinaryOp(BinaryOp.MUL,DataType.T_DOUBLE,tmpOp2,tmpOp));
                break;
            case 0x6a:  // fmul
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new BinaryOp(BinaryOp.MUL,DataType.T_FLOAT,tmpOp2,tmpOp));
                break;
            case 0x68:  // imul
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new BinaryOp(BinaryOp.MUL,DataType.T_INT,tmpOp2,tmpOp));
                break;
            case 0x69:  // lmul
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new BinaryOp(BinaryOp.MUL,DataType.T_LONG,tmpOp2,tmpOp));
                break;

            case 0x6f:  // ddiv
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new BinaryOp(BinaryOp.DIV,DataType.T_DOUBLE,tmpOp2,tmpOp));
                break;
            case 0x6e:  // fdiv
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new BinaryOp(BinaryOp.DIV,DataType.T_FLOAT,tmpOp2,tmpOp));
                break;
            case 0x6c:  // idiv
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new BinaryOp(BinaryOp.DIV,DataType.T_INT,tmpOp2,tmpOp));
                break;
            case 0x6d:  // ldiv
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new BinaryOp(BinaryOp.DIV,DataType.T_LONG,tmpOp2,tmpOp));
                break;

            case 0x73:  // drem
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new BinaryOp(BinaryOp.MOD,DataType.T_DOUBLE,tmpOp2,tmpOp));
                break;
            case 0x72:  // frem
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new BinaryOp(BinaryOp.MOD,DataType.T_FLOAT,tmpOp2,tmpOp));
                break;
            case 0x70:  // irem
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new BinaryOp(BinaryOp.MOD,DataType.T_INT,tmpOp2,tmpOp));
                break;
            case 0x71:  // lrem
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new BinaryOp(BinaryOp.MOD,DataType.T_LONG,tmpOp2,tmpOp));
                break;
                
            case 0x77:  // dneg
                tmpOp = stack.pop();
                stack.push(new UnaryOp(UnaryOp.NEGATE,DataType.T_DOUBLE,tmpOp));
                break;
            case 0x76:  // fneg
                tmpOp = stack.pop();
                stack.push(new UnaryOp(UnaryOp.NEGATE,DataType.T_FLOAT,tmpOp));
                break;
            case 0x74:  // ineg
                tmpOp = stack.pop();
                stack.push(new UnaryOp(UnaryOp.NEGATE,DataType.T_INT,tmpOp));
                break;
            case 0x75:  // lneg
                tmpOp = stack.pop();
                stack.push(new UnaryOp(UnaryOp.NEGATE,DataType.T_LONG,tmpOp));
                break;

            /*
             * Transformations: bit operations
             */
            case 0x78:  // ishl
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new BinaryOp(BinaryOp.SHIFTLEFT,DataType.T_INT,tmpOp2,tmpOp));
                break;
            case 0x7a:  // ishr
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new BinaryOp(BinaryOp.SHIFTRIGHT,DataType.T_INT,tmpOp2,tmpOp));
                break;
            case 0x7c:  // iushr
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new BinaryOp(BinaryOp.UNSIGNEDSHIFTRIGHT,DataType.T_INT,tmpOp2,tmpOp));
                break;

            case 0x79:  // lshl
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new BinaryOp(BinaryOp.SHIFTLEFT,DataType.T_LONG,tmpOp2,tmpOp));
                break;
            case 0x7b:  // lshr
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new BinaryOp(BinaryOp.SHIFTRIGHT,DataType.T_LONG,tmpOp2,tmpOp));
                break;
            case 0x7d:  // lushr
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new BinaryOp(BinaryOp.UNSIGNEDSHIFTRIGHT,DataType.T_LONG,tmpOp2,tmpOp));
                break;
                
            case 0x7e:  // iand
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new BinaryOp(BinaryOp.AND,DataType.T_INT,tmpOp2,tmpOp));
                break;
            case 0x7f:  // land
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new BinaryOp(BinaryOp.AND,DataType.T_LONG,tmpOp2,tmpOp));
                break;
                
            case 0x80:  // ior
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new BinaryOp(BinaryOp.OR,DataType.T_INT,tmpOp2,tmpOp));
                break;
            case 0x81:  // lor
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new BinaryOp(BinaryOp.OR,DataType.T_LONG,tmpOp2,tmpOp));
                break;
                
            case 0x82:  // ixor
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new BinaryOp(BinaryOp.XOR,DataType.T_INT,tmpOp2,tmpOp));
                break;
            case 0x83:  // lxor
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new BinaryOp(BinaryOp.XOR,DataType.T_LONG,tmpOp2,tmpOp));
                break;
                
            /*
             * Transformations: Type conversion
             */
            case 0x85:  // i2l
            case 0x8c:  // f2l
            case 0x8f:  // d2l
                stack.push(new ConvertOp(DataType.T_LONG,stack.pop()));
                break;

            case 0x86:  // i2f
            case 0x89:  // l2f
            case 0x90:  // d2f
                stack.push(new ConvertOp(DataType.T_FLOAT,stack.pop()));
                break;

            case 0x87:  // i2d
            case 0x8a:  // l2d
            case 0x8d:  // f2d
                stack.push(new ConvertOp(DataType.T_DOUBLE,stack.pop()));
                break;

            case 0x88:  // l2i
            case 0x8b:  // f2i
            case 0x8e:  // d2i
                stack.push(new ConvertOp(DataType.T_INT,stack.pop()));
                break;

            case 0x91:  // i2b
                stack.push(new ConvertOp(DataType.T_BYTE,stack.pop()));
                break;

            case 0x92:  // i2c
                stack.push(new ConvertOp(DataType.T_CHAR,stack.pop()));
                break;

            case 0x93:  // i2s
                stack.push(new ConvertOp(DataType.T_SHORT,stack.pop()));
                break;

                
            /*
             * Process control: transfer (conditional branching)
             */

            case 0x99:  // ifeq
                addInstruction(inst,stack,new IfCompare(IfCompare.EQ,stack.pop(),curpc + (short)readShort()));
                break;
            case 0x9a:  // ifne
                addInstruction(inst,stack,new IfCompare(IfCompare.NE,stack.pop(),curpc + (short)readShort()));
                break;
            case 0x9b:  // iflt
                addInstruction(inst,stack,new IfCompare(IfCompare.LT,stack.pop(),curpc + (short)readShort()));
                break;
            case 0x9e:  // ifle
                addInstruction(inst,stack,new IfCompare(IfCompare.LE,stack.pop(),curpc + (short)readShort()));
                break;
            case 0x9c:  // ifge
                addInstruction(inst,stack,new IfCompare(IfCompare.GE,stack.pop(),curpc + (short)readShort()));
                break;
            case 0x9d:  // ifgt
                addInstruction(inst,stack,new IfCompare(IfCompare.GT,stack.pop(),curpc + (short)readShort()));
                break;
            case 0xc6:  // ifnull
                addInstruction(inst,stack,new IfCompare(IfCompare.NULL,stack.pop(),curpc + (short)readShort()));
                break;
            case 0xc7:  // ifnonnull
                addInstruction(inst,stack,new IfCompare(IfCompare.NONNULL,stack.pop(),curpc + (short)readShort()));
                break;

            case 0x9f:  // if_icmpeq
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                addInstruction(inst,stack,new IfBinaryCompare(IfBinaryCompare.EQ,tmpOp2,tmpOp,curpc + (short)readShort()));
                break;
            case 0xa0:  // if_icmpne
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                addInstruction(inst,stack,new IfBinaryCompare(IfBinaryCompare.NE,tmpOp2,tmpOp,curpc + (short)readShort()));
                break;
            case 0xa1:  // if_icmplt
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                addInstruction(inst,stack,new IfBinaryCompare(IfBinaryCompare.LT,tmpOp2,tmpOp,curpc + (short)readShort()));
                break;
            case 0xa2:  // if_icmpge
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                addInstruction(inst,stack,new IfBinaryCompare(IfBinaryCompare.GE,tmpOp2,tmpOp,curpc + (short)readShort()));
                break;
            case 0xa3:  // if_icmpgt
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                addInstruction(inst,stack,new IfBinaryCompare(IfBinaryCompare.GT,tmpOp2,tmpOp,curpc + (short)readShort()));
                break;
            case 0xa4:  // if_icmple
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                addInstruction(inst,stack,new IfBinaryCompare(IfBinaryCompare.LE,tmpOp2,tmpOp,curpc + (short)readShort()));
                break;

            case 0xa5:  // if_acmpeq
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                addInstruction(inst,stack,new IfBinaryCompare(IfBinaryCompare.EQ,tmpOp2,tmpOp,curpc + (short)readShort()));
                break;
            case 0xa6:  // if_acmpne
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                addInstruction(inst,stack,new IfBinaryCompare(IfBinaryCompare.NE,tmpOp2,tmpOp,curpc + (short)readShort()));
                break;

            /*
             * Compare operations
             */
            case 0x94:  // lcmp
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new BinaryOp(BinaryOp.CMP,DataType.T_INT,tmpOp2,tmpOp));
                break;
            case 0x97:  // dcmpl
            case 0x95:  // fcmpl
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new BinaryOp(BinaryOp.CMPL,DataType.T_INT,tmpOp2,tmpOp));
                break;
            case 0x96:  // fcmpg
            case 0x98:  // dcmpg
                tmpOp = stack.pop();
                tmpOp2 = stack.pop();
                stack.push(new BinaryOp(BinaryOp.CMPG,DataType.T_INT,tmpOp2,tmpOp));
                break;
                
            /*
             * Unconditional branching
             */
            case 0xa7:  // goto
                addInstruction(inst,stack,new Goto(curpc + (short)readShort()));
                break;
            case 0xc8:  // goto_w
                addInstruction(inst,stack,new Goto(curpc + (short)readInt()));
                break;
            case 0xa8:  // jsr
            case 0xc9:  // jsr_w
            case 0xa9:  // ret
                throw new ClassParserException("JSR/JSR_W and RET are not supported for converson");
                
            /*
             * Tables
             */
            case 0xaa:  // tableswitch
                pc = (pc + 3) & ~0x03;
                tableInstruction(inst,stack,curpc,stack.pop());
                break;
                
            case 0xab:  // lookupswitch
                pc = (pc + 3) & ~0x03;
                lookupInstruction(inst,stack,curpc,stack.pop());
                break;
                
            /*
             * Methods
             */
            case 0xb9:  // invokeinterface
            case 0xb7:  // invokespecial
            case 0xb8:  // invokestatic
            case 0xb6:  // invokevirtual
                invokeOperator(inst,stack,op);
                break;

            case 0xac:  // ireturn
            case 0xad:  // lreturn
            case 0xae:  // freturn
            case 0xaf:  // dreturn
            case 0xb0:  // areturn
                addInstruction(inst,stack,new ReturnValue(stack.pop()));
                break;
            case 0xb1:  // return
                addInstruction(inst,stack,new Return());
                break;
                
            /*
             * Miscellaneous
             */
            case 0xbf:  // athrow
                addInstruction(inst,stack,new ThrowInstruction(stack.pop()));
                break;
                
            case 0xc2:  // monitorenter
            case 0xc3:  // monitorexit
                addInstruction(inst,stack,new Monitor((op == 0xc2),stack.pop()));
                break;
            default:
                throw new ClassParserException("Unknown instruction $" + String.format("%02X", op));
        }
    }
    
    /**
     * Create a switch instruction
     * @param curpc
     * @param op 
     */

    private void lookupInstruction(ArrayList<Instruction> inst, LinkedList<Op> stack, int curpc, Op op)
    {
        int defaultbyte = readInt() + curpc;
        int npairs = readInt();
        
        SwitchInstruction s = new SwitchInstruction(defaultbyte,op);
        for (int i = 0; i < npairs; ++i) {
            int match = readInt();
            s.addPair(match,readInt() + curpc);
        }
        addInstruction(inst,stack,s);
    }
    
    /**
     * Create a table instruction
     * @param curpc
     * @param op 
     */

    private void tableInstruction(ArrayList<Instruction> inst, LinkedList<Op> stack, int curpc, Op op)
    {
        int defaultbyte = readInt() + curpc;
        int lowbyte = readInt();
        int highbyte = readInt();
        
        TableInstruction t = new TableInstruction(defaultbyte,lowbyte,highbyte,op);
        for (int x = lowbyte; x <= highbyte; ++x) {
            t.add(readInt() + curpc);
        }
        addInstruction(inst,stack,t);
    }

    /**
     * Handle the invoke operator process
     * @param op
     */
    private void invokeOperator(ArrayList<Instruction> inst, LinkedList<Op> stack, int op)
    {
        FMIConstant c = (FMIConstant)(pool[readShort()]);
        
        LinkedList<Op> args = new LinkedList<Op>();
        int i,len = c.getDescriptor().getArgs().size();
        for (i = 0; i < len; ++i) {
            args.addFirst(stack.pop());
        }
        
        if (c.getDescriptor().getRet().isVoid()) {
            /*
             * Use instruction
             */
            
            switch (op) {
                case 0xb9:  // invokeinterface
                    addInstruction(inst,stack,new Invoke(Invoke.INTERFACE,stack.pop(),c,args));
                    readShort();
                    break;
                case 0xb7:  // invokespecial
                    addInstruction(inst,stack,new Invoke(Invoke.SPECIAL,stack.pop(),c,args));
                    break;
                case 0xb8:  // invokestatic
                    addInstruction(inst,stack,new Invoke(Invoke.STATIC,null,c,args));
                    break;
                case 0xb6:  // invokevirtual
                    addInstruction(inst,stack,new Invoke(Invoke.VIRTUAL,stack.pop(),c,args));
                    break;
            }
        } else {
            /*
             * Push operator
             */
            switch (op) {
                case 0xb9:  // invokeinterface
                    stack.push(new InvokeOp(Invoke.INTERFACE,stack.pop(),c,args));
                    readShort();
                    break;
                case 0xb7:  // invokespecial
                    stack.push(new InvokeOp(Invoke.SPECIAL,stack.pop(),c,args));
                    break;
                case 0xb8:  // invokestatic
                    stack.push(new InvokeOp(Invoke.STATIC,null,c,args));
                    break;
                case 0xb6:  // invokevirtual
                    stack.push(new InvokeOp(Invoke.VIRTUAL,stack.pop(),c,args));
                    break;
            }
        }
    }
}


