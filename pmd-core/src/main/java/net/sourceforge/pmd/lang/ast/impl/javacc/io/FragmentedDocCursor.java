/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc.io;

import java.io.EOFException;

import net.sourceforge.pmd.util.document.Chars;

final class FragmentedDocCursor {

    private static final EOFException EOF = new EOFException();

    private Fragment cur;
    private int curOutPos;

    private Fragment mark;
    private int markOutPos;

    FragmentedDocCursor(Fragment first) {
        cur = first;
        mark = first;
    }


    char next() throws EOFException {
        Fragment f = cur;

        while (f != null && curOutPos >= f.outEnd()) {
            f = f.next;
        }

        if (f == null) {
            throw EOF;
        }

        cur = f;
        return f.charAt(curOutPos++);
    }

    void backup(final int amount) {
        Fragment f = cur;

        int a = amount;
        while (f != null && a > 0) {
            if (a <= f.outLen()) {
                break;
            } else {
                a -= f.outLen();
                f = f.prev;
            }
        }

        if (f == null) {
            throw new IllegalArgumentException("Cannot backup by " + amount + " chars");
        }

        this.curOutPos = curOutPos - amount;
        this.cur = f;
    }

    void mark() {
        mark = cur;
        markOutPos = curOutPos;
    }

    int markInOffset() {
        return mark.outToIn(markOutPos);
    }

    int curInOffset() {
        return cur.outToIn(curOutPos);
    }

    int markLength() {
        return curOutPos - markOutPos;
    }


    public void appendMarkSuffix(StringBuilder sb, int suffixLen) {
        assert suffixLen <= markLength();

        if (cur == mark) {
            // no escape in the marked range
            cur.chars.appendChars(sb, curOutPos - cur.outStart - suffixLen, suffixLen);
        } else {
            // fallback inefficient implementation
            getMarkImage().appendChars(sb, markLength() - suffixLen, suffixLen);
        }
    }

    public Chars getMarkImage() {
        Fragment f = mark;
        if (f == cur) { // same fragment, this is the fast path
            return f.chars.slice(markOutPos - f.outStart(), markLength());
        }

        StringBuilder sb = new StringBuilder(markLength());

        f.appendAbs(sb, markOutPos, f.outEnd());
        f = f.next;
        while (f != cur) {
            f.appendAbs(sb, f.outStart(), f.outEnd());
            f = f.next;
        }
        f.appendAbs(sb, f.outStart(), curOutPos);
        assert sb.length() == markLength() : sb + " should have length " + markLength();
        return Chars.wrap(sb);
    }


    static final class Fragment {

        private final Chars chars;

        private final Fragment prev;
        Fragment next;

        private final int outStart;
        private final int inStart;
        private final int inLength;

        Fragment(Fragment prev, int inLength, Chars chars) {
            this.chars = chars;
            this.prev = prev;
            this.inLength = inLength;
            if (prev != null) {
                prev.next = this;
                this.outStart = prev.outEnd();
                this.inStart = prev.inEnd();
            } else {
                this.outStart = this.inStart = 0;
            }
        }


        void appendAbs(StringBuilder sb, int absOffset, int absEndOffset) {
            chars.appendChars(sb, absOffset - outStart, absEndOffset - absOffset);
        }


        char charAt(int absPos) {
            return chars.charAt(absPos - outStart);
        }

        int outStart() {
            return outStart;
        }

        int outLen() {
            return chars.length();
        }

        int outEnd() {
            return outStart + outLen();
        }

        int inStart() {
            return inStart;
        }

        int inLen() {
            return inLength;
        }

        int inEnd() {
            return inStart + inLength;
        }

        int outToIn(int outOffset) {
            return inStart() + (outOffset - outStart());
        }

        @Override
        public String toString() {
            return "Fragment{"
                + "chars=" + chars
                + ", outStart=" + outStart
                + ", inStart=" + inStart
                + ", inLength=" + inLength
                + '}';
        }
    }
}
