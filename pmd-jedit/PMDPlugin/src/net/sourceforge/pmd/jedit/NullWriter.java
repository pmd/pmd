package net.sourceforge.pmd.jedit;

import java.io.Writer;
import java.io.IOException;

/**
 * A bottomless sink. This writer doesn't write anything, it simply ignores any
 * input to any method. No exceptions will actually be thrown from this class.
 * This is useful for situations where a writer is required, but there isn't any
 * need to actually do any writing.
 */
public class NullWriter extends Writer {
    
    public void close() throws IOException {
    }
    
    public void flush() throws IOException {
    }
    
    public void write(char[] buffer, int offset, int length) throws IOException {
    }
}
