/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import org.apache.commons.io.IOUtils;

/**
 * 
 * @author Brian Remedios
 */
public final class IOUtil {

    private IOUtil() {
    }

    public static Writer createWriter() {
        return new OutputStreamWriter(System.out);
    }

    public static Writer createWriter(String reportFile) {
        try {
            return StringUtil.isEmpty(reportFile) ? createWriter() : new BufferedWriter(new FileWriter(reportFile));
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Reader skipBOM(Reader source) {
        Reader in = new BufferedReader(source);
        try {
            in.mark(1);
            int firstCharacter = in.read();
            if (firstCharacter != '\ufeff') {
                in.reset();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while trying to skip BOM marker", e);
        }
        return in;
    }

    public static void tryCloseClassLoader(ClassLoader classLoader) {
        if (classLoader instanceof Closeable) {
            IOUtils.closeQuietly((Closeable)classLoader);
        }
    }

}
