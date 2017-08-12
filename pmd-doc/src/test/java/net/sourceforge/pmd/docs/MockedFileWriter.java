/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.docs;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class MockedFileWriter implements FileWriter {
    
    public static class FileEntry {
        private String filename;
        private String content;

        public String getFilename() {
            return filename;
        }

        public String getContent() {
            return content;
        }
    }

    private List<FileEntry> data = new ArrayList<>();

    @Override
    public void write(Path path, List<String> lines) throws IOException {
        FileEntry entry = new FileEntry();
        entry.filename = path.toString();
        entry.content = StringUtils.join(lines, System.getProperty("line.separator"));
        data.add(entry);
    }

    public List<FileEntry> getData() {
        return data;
    }

    public void reset() {
        data.clear();
    }
}
