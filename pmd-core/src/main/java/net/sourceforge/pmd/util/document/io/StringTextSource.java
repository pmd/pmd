/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document.io;

import static java.util.Objects.requireNonNull;

import net.sourceforge.pmd.util.StringUtil;

public class StringTextSource implements PhysicalTextSource {

    private final String buffer;

    public StringTextSource(CharSequence source) {
        requireNonNull(source, "Null charset");

        this.buffer = source.toString();
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
        return 0;
    }

    @Override
    public String toString() {
        return "StringTextSource[" + StringUtil.truncate(buffer, 15, "...") + "]";
    }

}
