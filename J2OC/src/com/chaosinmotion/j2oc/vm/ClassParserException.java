/*  ClassParserException.java
 *
 *  Created on Feb 5, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.vm;

import java.io.IOException;

/**
 * Class parser exception; indicates there was a problem parsing the contents of this
 * file
 */
public class ClassParserException extends IOException
{
    public ClassParserException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ClassParserException(String message)
    {
        super(message);
    }
}


