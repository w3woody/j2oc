/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package java.io;

/**
 * Wraps an existing {@link OutputStream} and provides convenience methods for
 * writing common data types in a human readable format. This is not to be
 * confused with DataOutputStream which is used for encoding common data types
 * so that they can be read back in. No {@code IOException} is thrown by this
 * class. Instead, callers should use {@link #checkError()} to see if a problem
 * has occurred in this stream.
 */
public class PrintStream extends FilterOutputStream 
{
    public PrintStream(OutputStream out)
    {
        super(out);
    }

    public void println(String str)
    {
    }
}
