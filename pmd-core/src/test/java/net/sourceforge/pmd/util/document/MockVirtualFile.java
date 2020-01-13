/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import java.io.IOException;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.internal.util.BaseCloseable;
import net.sourceforge.pmd.util.document.io.VirtualFile;

/**
 * File modification date is not precise enough to write tests directly on it.
 */
public class MockVirtualFile extends BaseCloseable implements VirtualFile {

    private CharSequence curContents;
    private long modCount = 0;

    public MockVirtualFile(CharSequence initialValue) {
        this.curContents = initialValue;
    }

    @Override
    public @NonNull String getFileName() {
        return "MockFile";
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public void writeContents(CharSequence charSequence) throws IOException {
        curContents = charSequence;
        modCount++;
    }

    @Override
    public CharSequence readContents() throws IOException {
        return curContents;
    }

    @Override
    public long fetchStamp() throws IOException {
        return modCount;
    }

    @Override
    protected void doClose() throws IOException {

    }
}
