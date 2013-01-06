package net.sourceforge.pmd.jedit;

import java.io.*;
import net.sourceforge.pmd.util.datasource.DataSource;
import org.gjt.sp.jedit.Buffer;

/**
 * A datasource that uses a Buffer as the source for the bytes.
 */
public class BufferDataSource implements DataSource {

    Buffer buffer = null;
    
    /**
     * @param buffer The buffer to use as the data source.    
     */
    public BufferDataSource(Buffer buffer) {
        if (buffer == null) {
            throw new IllegalArgumentException("buffer is not allowed to be null");   
        }
        this.buffer = buffer;        
    }
    
    /**
     * @return A buffered input stream containing the contents of the given buffer.    
     */
    public InputStream getInputStream() {
        return new BufferedInputStream(new ByteArrayInputStream(buffer.getText().getBytes()));
    }

    /**
     * @param shortNames Not used.
     * @param inputFileName Not used.
     * @return The path and file name of the buffer, as returned by <code>Buffer.getPath()</code>.
     */
    public String getNiceFileName(boolean shortNames, String inputFileName) {
       return buffer.getPath();
    }
}