/*  Arguments.java
 *
 *  Created on Feb 9, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc;

import java.io.File;

/**
 * Arguments parser; determines the runtime commandline arguments
 */
public class Arguments
{
    private File inDirectory;
    private File outDirectory;
    private File outHeaders;
    private File missingDirectory;
    private File rewriteXMLIn;
    private File rewriteXMLOut;
    private boolean verbose;
    private boolean repressExceptions;
    private boolean stopOnWarning;
    private boolean missingCalls;
    private boolean generateRewriteXML;
    private boolean useRewriteXML;
    
    /**
     * Thrown by this class if there is a problem with the arguments
     */
    public class ParserError extends Exception
    {
        public ParserError(String message, Throwable cause)
        {
            super(message, cause);
        }

        public ParserError(String message)
        {
            super(message);
        }
    }

    public Arguments(String[] args) throws ParserError
    {
        int i = 0;

        try {
            while (i < args.length) {
                String arg = args[i++];

                if (arg.startsWith("-")) {
                    for (int j = 1; j < arg.length(); ++j) {
                        switch (arg.charAt(j)) {
                            case '?':
                                printArguments();
                                break;
                            case 'v':
                                verbose = true;
                                break;
                            case 'e':
                                repressExceptions = true;
                                break;
                            case 'w':
                                stopOnWarning = true;
                                break;
                            case 'm':
                                missingCalls = true;
                                missingDirectory = new File(args[i++]);
                                break;
                            case 'r':
                                generateRewriteXML = true;
                                rewriteXMLOut = new File(args[i++]);
                                break;
                            case 'u':
                                useRewriteXML = true;
                                rewriteXMLIn = new File(args[i++]);
                                break;
                            case 'h':
                                outHeaders = new File(args[i++]);
                                break;
                            default:
                                throw new ParserError("Unknown flag: " + arg.charAt(j));
                        }
                    }
                } else {
                    /*
                     * Argument order: outdir, indir
                     */

                    if (outDirectory == null) {
                        outDirectory = new File(arg);
                    } else if (inDirectory == null) {
                        inDirectory = new File(arg);
                        if (!inDirectory.exists()) {
                            throw new ParserError("In directory " + arg + " does not exist");
                        }
                    } else {
                        throw new ParserError("Too many arguments");
                    }
                }
            }
            
            outDirectory.mkdirs();
            if (missingDirectory != null) {
                missingDirectory.mkdirs();
            }
            if (outHeaders != null) {
                outHeaders.mkdirs();
            } else {
                outHeaders = outDirectory;
            }

            if (inDirectory == null) {
                throw new ParserError("Insufficient arguments");
            }
        }
        catch (ParserError err) {
            throw err;
        }
        catch (Throwable err) {
            throw new ParserError("Internal error",err);
        }
    }
    
    public File getRewriteXMLIn()
    {
        return rewriteXMLIn;
    }

    public File getRewriteXMLOut()
    {
        return rewriteXMLOut;
    }

    public boolean isGenerateRewriteXML()
    {
        return generateRewriteXML;
    }

    public boolean isUseRewriteXML()
    {
        return useRewriteXML;
    }

    public static void printArguments()
    {
        System.out.println("j2oc [-flags] outdir indir");
        System.out.println("");
        System.out.println("where");
        System.out.println("  -?   Prints this message");
        System.out.println("  -v   Verbose mode");
        System.out.println("  -w   Halt processing on warning");
        System.out.println("  -e   Repress exception code in generated OC code");
        System.out.println("  -h (dir)    Generated headers are put in this directory");
        System.out.println("  -m (file)   Generate missing interfaces report");
        System.out.println("  -r (file)   Generate rewrite file for native methods");
        System.out.println("  -u (file)   Use rewrite XML for native methods");
        System.out.println("");
        System.out.println("  outdir  Output directory (where OC code is written)");
        System.out.println("  indir   Input directory containing class files");
        System.out.println("");
        System.out.println("For more information visit our web site, http://www.j2oc.com");
    }

    public File getInDirectory()
    {
        return inDirectory;
    }

    public File getOutDirectory()
    {
        return outDirectory;
    }
    
    public File getOutHeaders()
    {
        return outHeaders;
    }
    
    public File getMissingDirectory()
    {
        return missingDirectory;
    }

    public boolean isVerbose()
    {
        return verbose;
    }

    public boolean isRepressExceptions()
    {
        return repressExceptions;
    }
    
    public boolean isStopOnWarning()
    {
        return stopOnWarning;
    }

    public boolean isMissingCalls()
    {
        return missingCalls;
    }
}


