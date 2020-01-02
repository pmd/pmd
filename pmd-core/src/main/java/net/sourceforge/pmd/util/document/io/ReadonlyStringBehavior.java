/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document.io;

import static java.util.Objects.requireNonNull;

import net.sourceforge.pmd.util.StringUtil;

/**
 * Read-only view on a string.
 */
public class ReadonlyStringBehavior implements TextFileBehavior {

    private final String buffer;

    public ReadonlyStringBehavior(String source) {
        requireNonNull(source, "Null charset");

        this.buffer = source;
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public void writeContents(CharSequence charSequence) {
        throw new UnsupportedOperationException("Readonly source");
    }

    @Override
    public CharSequence readContents() {
        return buffer;
    }

    @Override
    public long fetchStamp() {
        return hashCode();
    }

    @Override
    public String toString() {
        return "ReadOnlyString[" + StringUtil.truncate(buffer, 15, "...") + "]";
    }

}
