package net.sourceforge.pmd.jedit;

import java.io.*;
import net.sourceforge.pmd.util.datasource.DataSource;
import org.gjt.sp.jedit.Buffer;

/**
 * A datasource that uses a Buffer as the source for the bytes.
 */
public class BufferDataSource implements DataSource {

    Buffer buffer = null;
    
    public BufferDataSource(Buffer buffer) {
        if (buffer == null) {
            throw new IllegalArgumentException("buffer is not allowed to be null");   
        }
        this.buffer = buffer;        
    }
    
    public InputStream getInputStream() {
        return new BufferedInputStream(new ByteArrayInputStream(buffer.getText().getBytes()));
    }
    
    public String getNiceFileName(boolean shortNames, String inputFileName) {
       return buffer.getPath();
    }
}