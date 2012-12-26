package net.sourceforge.pmd.jedit;

import java.io.Writer;
import java.io.IOException;

/**
 * A bottomless sink.
 */
public class NullWriter extends Writer {
    
    public void close() throws IOException {
    }
    
    public void flush() throws IOException {
    }
    
    public void write(char[] buffer, int offset, int length) throws IOException {
    }
}
