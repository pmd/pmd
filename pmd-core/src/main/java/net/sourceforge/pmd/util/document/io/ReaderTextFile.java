/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document.io;

import java.io.IOException;
import java.io.Reader;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;

/**
 * Read-only view on a string.
 */
class ReaderTextFile implements TextFile {

    private final String name;
    private final LanguageVersion lv;
    private final Reader reader;

    ReaderTextFile(Reader reader, @NonNull String name, LanguageVersion lv) {
        this.reader = reader;
        AssertionUtil.requireParamNotNull("reader", reader);
        AssertionUtil.requireParamNotNull("file name", name);

        this.lv = lv;
        this.name = name;
    }

    @Override
    public @NonNull String getDisplayName() {
        return name;
    }

    @Override
    public String getPathId() {
        return name;
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public void writeContents(TextFileContent charSequence) {
        throw new ReadOnlyFileException();
    }

    @Override
    public @NonNull LanguageVersion getLanguageVersion(LanguageVersionDiscoverer discoverer) {
        return lv == null ? TextFile.super.getLanguageVersion(discoverer)
                          : lv;
    }

    @Override
    public TextFileContent readContents() throws IOException {
        try {
            return TextFileContent.fromReader(reader);
        } finally {
            reader.close();
        }
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    @Override
    public String toString() {
        return "ReaderTextFile[" + name + "]";
    }

}
