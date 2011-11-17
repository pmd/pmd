package net.sourceforge.pmd.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

/**
 * 
 * @author Brian Remedios
 */
public class IOUtil {

	private IOUtil() {}
	
    public static void closeQuietly(OutputStream stream) {
    	if (stream == null) return;
    	try {
    		stream.close();
    	} catch (IOException ex) {
    		// ignore
    	}
    }
    
    public static void closeQuietly(InputStream stream) {
    	if (stream == null) return;
    	try {
    		stream.close();
    	} catch (IOException ex) {
    		// ignore
    	}
    }
    
    public static void closeQuietly(Writer writer) {
    	if (writer == null) return;
    	try {
    		writer.close();
    	} catch (IOException ex) {
    		// ignore it
    	}
    }
    
    public static void closeQuietly(Reader reader) {
    	if (reader == null) return;
    	try {
    		reader.close();
    	} catch (IOException ex) {
    		//ignore
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
