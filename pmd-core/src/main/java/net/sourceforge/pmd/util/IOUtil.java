/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 *
 * @author Brian Remedios
 * @deprecated Is internal API
 */
@InternalApi
@Deprecated
public final class IOUtil {

    private IOUtil() {
    }

    public static Writer createWriter() {
        return new OutputStreamWriter(System.out);
    }

    public static Writer createWriter(String reportFile) {
        try {
            return StringUtils.isBlank(reportFile) ? createWriter()
                    : Files.newBufferedWriter(new File(reportFile).toPath(), Charset.defaultCharset());
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
            IOUtils.closeQuietly((Closeable) classLoader);
        }
    }

}
