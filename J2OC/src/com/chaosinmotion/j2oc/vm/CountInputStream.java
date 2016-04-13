/*  CountInputStream.java
 *
 *  Created on Feb 10, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm;

import java.io.IOException;
import java.io.InputStream;

public class CountInputStream extends InputStream
{
    private int fIndex;
    private InputStream fInputStream;
    
    public CountInputStream(InputStream is)
    {
        fInputStream = is;
        fIndex = 0;
    }

    @Override
    public int read() throws IOException
    {
        int c = fInputStream.read();
        if (c != -1) fIndex++;
        return c;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException
    {
        int c;
        int i = 0;
        
        c = read();
        if (c == -1) return -1;
        b[off + i++] = (byte)c;
        while (i < (len - off)) {
            c = read();
            if (c == -1) return i;
            b[off + i++] = (byte)c;
        }
        return i;
    }

    @Override
    public int read(byte[] b) throws IOException
    {
        return read(b,0,b.length);
    }

    public int getReadPos()
    {
        return fIndex;
    }
}


