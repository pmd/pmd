/**
 * <copyright>
 *  Copyright 1997-2002 InfoEther, LLC
 *  under sponsorship of the Defense Advanced Research Projects Agency
(DARPA).
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the Cougaar Open Source License as published
by
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).
 *
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 *  PROVIDED 'AS IS' WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.
 * </copyright>
 *
 * CHANGE RECORD
 * - 17 Nov 2003: modified by Olivier Mengu\u00E9 to add method
 *                appendXmlEscaped used in XMLRenderer
 */
package net.sourceforge.pmd.util;

public class StringUtil {

    public static String replaceString(String d, char oldChar, String newString) {
        StringBuffer desc = new StringBuffer();
        int index = d.indexOf(oldChar);
        int last = 0;
        while (index != -1) {
            desc.append(d.substring(last, index));
            desc.append(newString);
            last = index + 1;
            index = d.indexOf(oldChar, last);
        }
        desc.append(d.substring(last));
        return desc.toString();
    }

    public static String replaceString(String inputString, String oldString, String newString) {
        StringBuffer desc = new StringBuffer();
        int index = inputString.indexOf(oldString);
        int last = 0;
        while (index != -1) {
            desc.append(inputString.substring(last, index));
            desc.append(newString);
            last = index + oldString.length();
            index = inputString.indexOf(oldString, last);
        }
        desc.append(inputString.substring(last));
        return desc.toString();
    }

    /**
     * Appends to a StringBuffer the String src where non-ASCII and
     * XML special chars are escaped.
     * @param buf The destination XML stream
     * @param str The String to append to the stream
     */
    public static void appendXmlEscaped(StringBuffer buf, String src) {
        int l = src.length();
        char c;
        for(int i=0; i<l; i++) {
            c = src.charAt(i);
            if (c > '~') {// 126
                if (c <= 255)
                    buf.append(ENTITIES[c-126]);
                else 
                    buf.append("&u").append(Integer.toHexString(c)).append(';');
            } else if (c == '&')
                buf.append("&amp;");
            else if (c == '"')
                buf.append("&quot;");
            else if (c == '<')
                buf.append("&lt;");
            else if (c == '>')
                buf.append("&gt;");
            else
                buf.append(c);
        }
    }

    private static final String[] ENTITIES;
    static {
        ENTITIES = new String[256-126];
        for(int i=126; i<= 255; i++)
            ENTITIES[i-126] = "&#" + i + ';';
    }


}
