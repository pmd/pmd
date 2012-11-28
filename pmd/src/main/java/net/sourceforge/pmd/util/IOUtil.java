package net.sourceforge.pmd.util;

import java.io.BufferedWriter;
import java.io.Closeable;
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
	
	/**
	 * Convenience methods to close any stream, reader, or writer. Ignores
	 * null values.
	 *
	 * @param closeable
	 */
    public static void closeQuietly(Closeable closeable) {
    	if (closeable == null) return;
    	try {
    		closeable.close();
    	} catch (IOException ex) {
    		// ignore
    	}
    }
    
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
