/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * 
 * @author Brian Remedios
 */
public class IOUtil {

    private IOUtil() {}

    public static Writer createWriter() {
    	return new OutputStreamWriter(System.out);
    }
    
    public static Writer createWriter(String reportFile) {
    	try {
    		return StringUtil.isEmpty(reportFile) ? createWriter()  : new BufferedWriter(new FileWriter(reportFile));
    	} catch (IOException e) {
    		throw new IllegalArgumentException(e);
    	}
    }
}
