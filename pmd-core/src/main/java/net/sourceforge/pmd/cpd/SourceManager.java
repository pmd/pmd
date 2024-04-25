/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.lang.document.TextRegion;
import net.sourceforge.pmd.reporting.FileNameRenderer;

/**
 * Maps {@link FileId} to {@link TextDocument}, reusing documents with
 * {@link SoftReference} if they have not been replaced yet.
 */
class SourceManager implements AutoCloseable {

    private final Map<FileId, SoftReference<TextDocument>> files = new ConcurrentHashMap<>();
    private final Map<FileId, TextFile> fileByPathId = new HashMap<>();
    private final List<TextFile> textFiles;
    private FileNameRenderer fileNameRenderer = FileId::getAbsolutePath;

    SourceManager(List<? extends TextFile> files) {
        textFiles = new ArrayList<>(files);
        files.forEach(f -> fileByPathId.put(f.getFileId(), f));
    }


    List<TextFile> getTextFiles() {
        return textFiles;
    }

    /**
     * Load a textfile without caching it.
     */
    TextDocument load(TextFile file) throws IOException {
        return TextDocument.create(file);
    }

    TextDocument get(TextFile file) throws IOException {
        SoftReference<TextDocument> ref = files.get(file.getFileId());
        TextDocument loaded = ref == null ? null : ref.get();
        if (loaded == null) {
            loaded = load(file);
            files.put(file.getFileId(), new SoftReference<>(loaded));
        }
        return loaded;
    }

    TextDocument getUnchecked(TextFile file) {
        try {
            return get(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
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
        TextFile textFile = fileByPathId.get(mark.getToken().getFileId());
        assert textFile != null : "No such file " + mark.getToken().getFileId();
        TextDocument doc = getUnchecked(textFile);
        assert doc != null;
        FileLocation loc = mark.getLocation();
        TextRegion lineRange = doc.createLineRange(loc.getStartLine(), loc.getEndLine());
        return doc.sliceOriginalText(lineRange);
    }

    public String getFileDisplayName(FileId fileId) {
        return fileNameRenderer.getDisplayName(fileId);
    }
}
