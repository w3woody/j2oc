/*  RewriteSupport.java
 *
 *  Created on Feb 13, 2010 by William Edward Woody
 */

package com.chaosinmotion.j2oc.oc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Rewrite support class for reading in and writing out method rewrite for native
 * classes
 */
public class RewriteSupport
{
    private static final RewriteSupport gRewrite = new RewriteSupport();
    
    private HashSet<String> fIgnore = new HashSet<String>();
    private HashMap<StringPair,String> fRewrite = new HashMap<StringPair,String>();
    
    private static class StringPair
    {
        private String a;
        private String b;
        
        StringPair(String aa, String bb)
        {
            a = aa;
            b = bb;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((a == null) ? 0 : a.hashCode());
            result = prime * result + ((b == null) ? 0 : b.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            StringPair other = (StringPair)obj;
            if (a == null) {
                if (other.a != null) return false;
            } else if (!a.equals(other.a)) return false;
            if (b == null) {
                if (other.b != null) return false;
            } else if (!b.equals(other.b)) return false;
            return true;
        }
    }

    public static RewriteSupport getRewrite()
    {
        return gRewrite;
    }
    
    /**
     * Load the rewrite rules file
     * @param f
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public void loadRewriteRules(File f) throws ParserConfigurationException, SAXException, IOException
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder parser = dbf.newDocumentBuilder();
        Document doc = parser.parse(f);
        
        Element e = doc.getDocumentElement();
        if (!e.getTagName().equals("rewrite")) {
            throw new IOException("Error: expected tag <rewrite> as root tag");
        }
        
        for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element eparser = (Element)n;
                
                if (eparser.getTagName().equals("method")) {
                    addMethodRewrite(eparser);
                } else if (eparser.getTagName().equals("ignore")) {
                    addClassIgnore(eparser);
                }
            }
        }
    }

    private void addClassIgnore(Element eparser)
    {
        String str = eparser.getAttribute("class");
        fIgnore.add(str);
    }

    private void addMethodRewrite(Element eparser)
    {
        String method = eparser.getAttribute("name");
        String cname = eparser.getAttribute("class");
        
        StringPair pair = new StringPair(method,cname);
        
        for (Node n = eparser.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == Node.CDATA_SECTION_NODE) {
                CDATASection sec = (CDATASection)n;
                fRewrite.put(pair, sec.getNodeValue());
                break;
            }
        }
    }

    public String getMethod(String methodName, String thisClassName)
    {
        return fRewrite.get(new StringPair(methodName,thisClassName));
    }

    public boolean skipRewrite(String fname)
    {
        return fIgnore.contains(fname);
    }
}


