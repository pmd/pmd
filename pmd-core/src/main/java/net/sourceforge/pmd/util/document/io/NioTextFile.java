/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.IOUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.internal.util.BaseCloseable;

/**
 * A {@link TextFile} backed by a file in some {@link FileSystem}.
 */
class NioTextFile extends BaseCloseable implements TextFile {

    private final Path path;
    private final Charset charset;

    NioTextFile(Path path, Charset charset) throws IOException {
        AssertionUtil.requireParamNotNull("path", path);
        AssertionUtil.requireParamNotNull("charset", charset);

        if (!Files.isRegularFile(path)) {
            throw new IOException("Not a regular file: " + path);
        }

        this.path = path;
        this.charset = charset;
    }

    @Override
    public @NonNull String getDisplayName() {
        return path.toAbsolutePath().toString();
    }

    @Override
    public boolean isReadOnly() {
        return !Files.isWritable(path);
    }

    @Override
    public void writeContents(TextFileContent content) throws IOException {
        ensureOpen();
        try (BufferedWriter bw = Files.newBufferedWriter(path, charset)) {
            if (content.getLineTerminator().equals(TextFileContent.NORMALIZED_LINE_TERM)) {
                content.getNormalizedText().writeFully(bw);
            } else {
                try (BufferedReader br = new BufferedReader(content.getNormalizedText().newReader())) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        bw.write(line);
                        bw.write(content.getLineTerminator());
                    }
                }
            }
        }
    }

    @Override
    public TextFileContent readContents() throws IOException {
        ensureOpen();

        String text;
        try (BufferedReader br = Files.newBufferedReader(path, charset)) {
            text = IOUtils.toString(br);
        }
        return TextFileContent.normalizeToFileContent(text);
    }


    @Override
    protected void doClose() {
        // do nothing
    }

    @Override
    public String toString() {
        return "NioTextFile[charset=" + charset + ", path=" + path + ']';
    }
}
