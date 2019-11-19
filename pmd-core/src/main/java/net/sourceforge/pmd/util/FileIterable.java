/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

/**
 *
 * <p>
 * Handy class to easily iterate over a file, line by line, using a Java 5 for
 * loop.
 * </p>
 *
 * @author Romain Pelisse &lt;belaran@gmail.com&gt;
 * @deprecated Just use {@link Files#readAllLines(Path, Charset)} or {@code lines} on Java 8
 */
@Deprecated
public class FileIterable implements Iterable<String> {

    private LineNumberReader lineReader = null;

    public FileIterable(File file) {
        try {
            lineReader = new LineNumberReader(Files.newBufferedReader(file.toPath(), Charset.defaultCharset()));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (lineReader != null) {
                lineReader.close();
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        super.finalize();
    }

    @Override
    public Iterator<String> iterator() {
        return new FileIterator();
    }

    class FileIterator implements Iterator<String> {

        private boolean hasNext = true;

        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public String next() {
            String line = null;
            try {
                if (hasNext) {
                    line = lineReader.readLine();
                    if (line == null) {
                        hasNext = false;
                        line = "";
                    }
                }
                return line;
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove is not supported by " + this.getClass().getName());
        }

    }

}
