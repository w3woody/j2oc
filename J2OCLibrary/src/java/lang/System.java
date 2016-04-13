/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package java.lang;

import java.io.PrintStream;

/**
 * Provides access to system-related information and resources including
 * standard input and output. Enables clients to dynamically load native
 * libraries. All methods of this class are accessed in a static way and the
 * class itself can not be instantiated.
 *
 * @see Runtime
 */
public final class System 
{
    private static class InternalPrintStream extends PrintStream
    {
        public InternalPrintStream()
        {
            super(null);
        }
        public native void println(String str);
    }
    /**
     * Default output stream.
     */
    public static final PrintStream out = new InternalPrintStream();

    /**
     * Default error output stream.
     */
    public static final PrintStream err = new InternalPrintStream();

    /**
     * Copies the number of {@code length} elements of the Array {@code src}
     * starting at the offset {@code srcPos} into the Array {@code dest} at
     * the position {@code destPos}.
     *
     * @param src
     *            the source array to copy the content.
     * @param srcPos
     *            the starting index of the content in {@code src}.
     * @param dest
     *            the destination array to copy the data into.
     * @param destPos
     *            the starting index for the copied content in {@code dest}.
     * @param length
     *            the number of elements of the {@code array1} content they have
     *            to be copied.
     */
    public static native void arraycopy(Object src, int srcPos, Object dest, 
            int destPos, int length);

    

}
