/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.lang.document.TextRegion;

class SourceManager implements AutoCloseable {

    private final Map<TextFile, SoftReference<TextDocument>> files = new ConcurrentHashMap<>();
    private final Map<String, TextFile> fileByPathId = new HashMap<>();
    private final List<TextFile> textFiles;

    SourceManager(List<? extends TextFile> files) {
        textFiles = new ArrayList<>(files);
        files.forEach(f -> fileByPathId.put(f.getPathId(), f));
    }


    List<TextFile> getTextFiles() {
        return textFiles;
    }

    TextDocument get(TextFile file) {
        return files.computeIfAbsent(file, f -> {
            TextDocument doc;
            try {
                doc = TextDocument.create(f);
                return new SoftReference<>(doc);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).get();
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

    @SuppressWarnings("PMD.CloseResource")
    public Chars getSlice(Mark mark) {
        TextFile textFile = fileByPathId.get(mark.getToken().getFilePathId());
        assert textFile != null: "No such file " + mark.getToken().getFilePathId();
        TextDocument doc = get(textFile);
        assert doc != null;
        FileLocation loc = mark.getLocation();
        TextRegion lineRange = doc.createLineRange(loc.getStartLine(), loc.getEndLine());
        return doc.sliceOriginalText(lineRange);
    }

    public String getFileDisplayName(String filePathId) {
        return fileByPathId.get(filePathId).getDisplayName();
    }
}
