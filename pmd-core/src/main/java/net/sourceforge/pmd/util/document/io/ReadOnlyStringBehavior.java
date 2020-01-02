/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document.io;

import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.util.StringUtil;

/**
 * Read-only view on a string.
 */
public class ReadOnlyStringBehavior implements TextFileBehavior {

    private final String buffer;

    public ReadOnlyStringBehavior(String source) {
        AssertionUtil.requireParamNotNull("source text", source);

        this.buffer = source;
    }

    /** Returns true, always. */
    @Override
    public boolean isReadOnly() {
        return true;
    }

    /** @throws ReadOnlyFileException Always */
    @Override
    public void writeContents(CharSequence charSequence) {
        throw new ReadOnlyFileException("Readonly source");
    }

    /** Returns the original string. */
    @Override
    public String readContents() {
        return buffer;
    }

    @Override
    public long fetchStamp() {
        return hashCode();
    }

    /** Closing an instance of this class has no effect. */
    @Override
    public void close() {

    }

    @Override
    public String toString() {
        return "ReadOnlyString[" + StringUtil.truncate(buffer, 15, "...") + "]";
    }

}
