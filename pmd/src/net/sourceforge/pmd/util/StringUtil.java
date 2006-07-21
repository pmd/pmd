/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StringUtil {

	public static final String[] EMPTY_STRINGS = new String[0];
    private static final boolean supportsUTF8 = System.getProperty("net.sourceforge.pmd.supportUTF8", "no").equals("yes");
    private static final String[] ENTITIES;

    static {
        ENTITIES = new String[256 - 126];
        for (int i = 126; i <= 255; i++) {
            ENTITIES[i - 126] = "&#" + i + ';';
        }
    }

    public static String replaceString(String d, char oldChar, String newString) {
        String fixedNew = newString;
        if (fixedNew == null) {
            fixedNew = "";
        }
        StringBuffer desc = new StringBuffer();
        int index = d.indexOf(oldChar);
        int last = 0;
        while (index != -1) {
            desc.append(d.substring(last, index));
            desc.append(fixedNew);
            last = index + 1;
            index = d.indexOf(oldChar, last);
        }
        desc.append(d.substring(last));
        return desc.toString();
    }

    public static String replaceString(String inputString, String oldString, String newString) {
        String fixedNew = newString;
        if (fixedNew == null) {
            fixedNew = "";
        }
        StringBuffer desc = new StringBuffer();
        int index = inputString.indexOf(oldString);
        int last = 0;
        while (index != -1) {
            desc.append(inputString.substring(last, index));
            desc.append(fixedNew);
            last = index + oldString.length();
            index = inputString.indexOf(oldString, last);
        }
        desc.append(inputString.substring(last));
        return desc.toString();
    }

    /**
     * Appends to a StringBuffer the String src where non-ASCII and
     * XML special chars are escaped.
     *
     * @param buf The destination XML stream
     * @param src The String to append to the stream
     */
    public static void appendXmlEscaped(StringBuffer buf, String src) {
        appendXmlEscaped(buf, src, supportsUTF8);
    }

    private static void appendXmlEscaped(StringBuffer buf, String src, boolean supportUTF8) {
        char c;
        for (int i = 0; i < src.length(); i++) {
            c = src.charAt(i);
            if (c > '~') {// 126
                if (!supportUTF8) {
                    if (c <= 255) {
                        buf.append(ENTITIES[c - 126]);
                    } else {
                        buf.append("&u").append(Integer.toHexString(c)).append(';');
                    }
                } else {
                    buf.append(c);
                }
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

	/**
	 * Parses the input source using the delimiter specified. This method is much
	 * faster than using the StringTokenizer or String.split(char) approach and
	 * serves as a replacement for String.split() for JDK1.3 that doesn't have it.
	 *
	 * @param source String
	 * @param delimiter char
	 * @return String[]
	 */
	public static String[] substringsOf(String source, char delimiter) {

		if (source == null || source.length() == 0) {
            return EMPTY_STRINGS;
        }
		
		int delimiterCount = 0;
		int length = source.length();
		char[] chars = source.toCharArray();

		for (int i=0; i<length; i++) {
			if (chars[i] == delimiter) delimiterCount++;
			}

		if (delimiterCount == 0) return new String[] { source };

		String results[] = new String[delimiterCount+1];

		int i = 0;
		int offset = 0;

		while (offset <= length) {
			int pos = source.indexOf(delimiter, offset);
			if (pos < 0) pos = length;
			results[i++] = pos == offset ? "" : source.substring(offset, pos);
			offset = pos + 1;
			}

		return results;
	}
	
	/**
	 * @param str String
	 * @param separator char
	 * @return String[]
	 */
	  public static String[] substringsOf(String str, String separator) {
		  
	        if (str == null || str.length() == 0) {
	            return EMPTY_STRINGS;
	        }

	        int index = str.indexOf(separator);
	        if (index == -1) {
	            return new String[]{str};
	        }

	        List list = new ArrayList();
	        int currPos = 0;
	        int len = separator.length();
	        while (index != -1) {
	            list.add(str.substring(currPos, index));
	            currPos = index + len;
	            index = str.indexOf(separator, currPos);
	        }
	        list.add(str.substring(currPos));
	        return (String[]) list.toArray(new String[list.size()]);
	    }
	
	
	/**
	 * Copies the elements returned by the iterator onto the string buffer
	 * each delimited by the separator.
	 *
	 * @param sb StringBuffer
	 * @param iter Iterator
	 * @param separator String
	 */
	public static void asStringOn(StringBuffer sb, Iterator iter, String separator) {
		
	    if (!iter.hasNext()) return;
	    
	    sb.append(iter.next());
	    
	    while (iter.hasNext()) {
	    	sb.append(separator);
	        sb.append(iter.next());
	    }
	}
}
