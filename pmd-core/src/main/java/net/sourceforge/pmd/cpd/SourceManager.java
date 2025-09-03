/*
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

    private final Map<FileId, SoftReference<TextDocument>> files = new HashMap<>();
    private final Map<FileId, TextDocument> strongReferences = new HashMap<>();
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

    boolean isEmpty() {
        return textFiles.isEmpty();
    }

    /**
     * Load a textfile without caching it.
     */
    TextDocument load(TextFile file) throws IOException {
        if (!file.canReopen()) {
            // Note we synchronize manually around the map instead of using
            // a concurrent hashmap because using computeIfAbsent we would
            // have to throw the IOException as UncheckedIOException and unwrap
            // it afterward.
            synchronized (strongReferences) {
                TextDocument loaded = strongReferences.get(file.getFileId());
                if (loaded == null) {
                    loaded = TextDocument.create(file);
                    strongReferences.put(file.getFileId(), loaded);
                }
                return loaded;
            }
        }
        return TextDocument.create(file);
    }

    /**
     * Get a textfile from cache or load it.
     *
     * @param fileId A file ID
     *
     * @return A text document
     *
     * @throws IOException If reading the TextFile throws
     */
    TextDocument get(FileId fileId) throws IOException {
        TextFile textFile = fileByPathId.get(fileId);
        if (textFile == null) {
            throw new IllegalArgumentException(fileId.toString());
        } else if (!textFile.canReopen()) {
            // if the file cannot be reopened later, then we should keep it loaded
            return load(textFile);
        }

        // Here we synchronize manually around the map instead of using
        // a concurrent map, because we could not guarantee that the SoftReference
        // has not been cleared between the moment it is loaded within an eg Map::compute
        // call and the moment it is returned.
        synchronized (files) {
            // use soft reference
            SoftReference<TextDocument> ref = files.get(fileId);
            TextDocument loaded = ref == null ? null : ref.get();
            if (loaded == null) {
                loaded = load(textFile);
                files.put(fileId, new SoftReference<>(loaded));
            }
            return loaded;
        }
    }

    private TextDocument getUnchecked(FileId file) {
        try {
            return get(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public int size() {
        return textFiles.size();
    }


    @Override
    public void close() throws IOException {
        strongReferences.clear();
        files.clear();
        Exception exception = IOUtil.closeAll(textFiles);
        if (exception != null) {
            throw new IOException(exception);
        }
    }

    @SuppressWarnings("PMD.CloseResource")
    public Chars getSlice(Mark mark) {
        TextDocument doc = getUnchecked(mark.getFileId());
        assert doc != null;
        FileLocation loc = mark.getLocation();
        TextRegion lineRange = doc.createLineRange(loc.getStartLine(), loc.getEndLine());
        return doc.sliceOriginalText(lineRange);
    }

    public String getFileDisplayName(FileId fileId) {
        return fileNameRenderer.getDisplayName(fileId);
    }

    FileLocation toLocation(FileId fileId, TextRegion region) {
        TextDocument doc = getUnchecked(fileId); // NOPMD
        return doc.toLocation(region);
    }
}
