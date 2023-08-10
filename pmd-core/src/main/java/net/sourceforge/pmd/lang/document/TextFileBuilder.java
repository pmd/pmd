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
    protected FileId parentFsId;

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

    public TextFileBuilder setParentFsPath(@Nullable FileId fileId) {
        parentFsId = fileId;
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
            return new NioTextFile(path, parentFsId, charset, languageVersion, readOnly);
        }
    }

    static class ForCharSeq extends TextFileBuilder {

        private final CharSequence charSequence;
        private FileId fileId;

        ForCharSeq(CharSequence charSequence, FileId fileId, LanguageVersion languageVersion) {
            super(languageVersion);
            this.charSequence = AssertionUtil.requireParamNotNull("charseq", charSequence);
            this.fileId = AssertionUtil.requireParamNotNull("path ID", fileId);
        }

        @Override
        public TextFileBuilder setParentFsPath(@Nullable FileId fileId) {
            this.fileId = FileId.asChildOf(this.fileId, fileId);
            return super.setParentFsPath(fileId);
        }

        @Override
        public TextFile build() {
            return new StringTextFile(charSequence, fileId, languageVersion);
        }
    }

    static class ForReader extends TextFileBuilder {

        private final Reader reader;
        private FileId fileId;

        ForReader(LanguageVersion languageVersion, Reader reader, FileId fileId) {
            super(languageVersion);
            this.reader = AssertionUtil.requireParamNotNull("reader", reader);
            this.fileId = AssertionUtil.requireParamNotNull("path ID", fileId);
        }

        @Override
        public TextFileBuilder setParentFsPath(@Nullable FileId fileId) {
            this.fileId = FileId.asChildOf(this.fileId, fileId);
            return super.setParentFsPath(fileId);
        }


        @Override
        public TextFile build() {
            return new ReaderTextFile(reader, fileId, languageVersion);
        }
    }

}
