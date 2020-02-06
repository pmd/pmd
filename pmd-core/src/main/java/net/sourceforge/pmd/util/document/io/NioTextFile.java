/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document.io;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

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
    public @NonNull String getFileName() {
        return path.toAbsolutePath().toString();
    }

    @Override
    public boolean isReadOnly() {
        return !Files.isWritable(path);
    }

    @Override
    public void writeContents(CharSequence charSequence) throws IOException {
        ensureOpen();
        byte[] bytes = charSequence.toString().getBytes(charset);
        Files.write(path, bytes);
    }

    @Override
    public CharSequence readContents() throws IOException {
        ensureOpen();
        byte[] bytes = Files.readAllBytes(path);
        return new String(bytes, charset);
    }

    @Override
    public long fetchStamp() throws IOException {
        ensureOpen();
        return Files.getLastModifiedTime(path).to(TimeUnit.MICROSECONDS);
    }


    @Override
    protected void doClose() {
        // do nothing
    }

    @Override
    public String toString() {
        return "NioVFile[charset=" + charset + ", path=" + path + ']';
    }
}
