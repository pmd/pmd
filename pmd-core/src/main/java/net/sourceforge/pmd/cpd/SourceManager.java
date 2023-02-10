/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextFile;

public class SourceManager implements AutoCloseable {

    private final Map<TextFile, SoftReference<TextDocument>> files = new ConcurrentHashMap<>();
    private final List<TextFile> textFiles;

    public SourceManager(List<? extends TextFile> files) {
        textFiles = new ArrayList<>(files);
    }


    TextDocument get(String pathId) {

    }

    public int size() {
        return files.size();
    }


    @Override
    public void close() throws Exception {
        Exception exception = IOUtil.closeAll(textFiles);
        if (exception != null) {
            throw exception;
        }
    }
}
