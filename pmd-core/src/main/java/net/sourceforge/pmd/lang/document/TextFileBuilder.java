/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Path;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.util.AssertionUtil;

/**
 * A builder for a new text file.
 * See static methods on {@link TextFile}.
 */
@SuppressWarnings("PMD.MissingStaticMethodInNonInstantiatableClass")
public abstract class TextFileBuilder {

    protected final LanguageVersion languageVersion;
    protected @Nullable String displayName;

    TextFileBuilder(LanguageVersion languageVersion) {
        this.languageVersion = AssertionUtil.requireParamNotNull("language version", languageVersion);
    }

    /**
     * Specify that the built file is read only. Some text files are
     * always read-only.
     *
     * @return This builder
     */
    public TextFileBuilder asReadOnly() {
        // default is appropriate if the file type is always read-only
        return this;
    }


    /**
     * Sets a custom display name for the new file. If null, or this is
     * never called, the display name defaults to the path ID.
     *
     * @param displayName A display name
     *
     * @return This builder
     */
    public TextFileBuilder withDisplayName(@Nullable String displayName) {
        this.displayName = displayName;
        return this;
    }

    /**
     * Creates and returns the new text file.
     */
    public abstract TextFile build();

    static class ForNio extends TextFileBuilder {

        private final Path path;
        private final Charset charset;
        private boolean readOnly = false;

        ForNio(LanguageVersion languageVersion, Path path, Charset charset) {
            super(languageVersion);
            this.path = AssertionUtil.requireParamNotNull("path", path);
            this.charset = AssertionUtil.requireParamNotNull("charset", charset);
        }

        @Override
        public TextFileBuilder asReadOnly() {
            readOnly = true;
            return this;
        }

        @Override
        public TextFile build() {
            return new NioTextFile(path, charset, languageVersion, displayName, readOnly);
        }
    }

    static class ForCharSeq extends TextFileBuilder {

        private final CharSequence charSequence;
        private final String pathId;

        ForCharSeq(CharSequence charSequence, String pathId, LanguageVersion languageVersion) {
            super(languageVersion);
            this.charSequence = AssertionUtil.requireParamNotNull("charseq", charSequence);
            this.pathId = AssertionUtil.requireParamNotNull("path ID", pathId);
        }

        @Override
        public TextFile build() {
            return new StringTextFile(charSequence, pathId, languageVersion);
        }
    }

    static class ForReader extends TextFileBuilder {

        private final Reader reader;
        private final String pathId;

        ForReader(LanguageVersion languageVersion, Reader reader, String pathId) {
            super(languageVersion);
            this.reader = AssertionUtil.requireParamNotNull("reader", reader);
            this.pathId = AssertionUtil.requireParamNotNull("path ID", pathId);
        }

        @Override
        public TextFile build() {
            return new ReaderTextFile(reader, pathId, languageVersion);
        }
    }

}
