/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document.io;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.util.StringUtil;

/**
 * Read-only view on a string.
 */
class StringFile implements TextFile {

    private final String buffer;
    private final String name;

    StringFile(String source, @Nullable String name) {
        AssertionUtil.requireParamNotNull("source text", source);

        this.buffer = source;
        this.name = String.valueOf(name);
    }

    @Override
    public @NonNull String getFileName() {
        return name;
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public void writeContents(CharSequence charSequence) {
        throw new ReadOnlyFileException("Readonly source");
    }

    @Override
    public String readContents() {
        return buffer;
    }

    @Override
    public long fetchStamp() {
        return hashCode();
    }

    @Override
    public void close() {
        // nothing to do
    }

    @Override
    public String toString() {
        return "ReadOnlyString[" + StringUtil.truncate(buffer, 15, "...") + "]";
    }

}
