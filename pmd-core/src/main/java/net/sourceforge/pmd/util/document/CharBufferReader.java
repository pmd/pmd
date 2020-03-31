/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import java.io.Reader;
import java.nio.CharBuffer;

final class CharBufferReader extends Reader {

    private final CharBuffer b;

    public CharBufferReader(CharBuffer b) {
        this.b = b;
    }

    @Override
    public int read(char[] cbuf, int off, int len) {
        if ((off < 0) || (off > b.length()) || (len < 0) ||
            ((off + len) > b.length()) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        }

        int toRead = Math.min(len, b.remaining());
        if (toRead == 0) {
            return -1;
        }
        b.get(cbuf, off, toRead);
        return toRead;
    }

    @Override
    public int read() {
        return b.get();
    }

    @Override
    public void close() {

    }
}
