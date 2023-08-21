/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import java.io.IOException;
import java.io.Reader;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.util.AssertionUtil;

/**
 * Read-only view on a string.
 */
class ReaderTextFile implements TextFile {

    private final FileId fileId;
    private final LanguageVersion languageVersion;
    private final Reader reader;

    ReaderTextFile(Reader reader, @NonNull FileId fileId, LanguageVersion languageVersion) {
        AssertionUtil.requireParamNotNull("reader", reader);
        AssertionUtil.requireParamNotNull("path id", fileId);
        AssertionUtil.requireParamNotNull("language version", languageVersion);

        this.reader = reader;
        this.languageVersion = languageVersion;
        this.fileId = fileId;
    }

    @Override
    public FileId getFileId() {
        return fileId;
    }

    @Override
    public @NonNull LanguageVersion getLanguageVersion() {
        return languageVersion;
    }

    @Override
    public TextFileContent readContents() throws IOException {
        return TextFileContent.fromReader(reader); // this closes the reader
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    @Override
    public String toString() {
        return "ReaderTextFile[" + fileId.getAbsolutePath() + "]";
    }

}
