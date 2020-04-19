/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc.io;

import java.io.EOFException;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.util.document.Chars;

final class FragmentedDocCursor {

    private static final EOFException EOF = new EOFException();

    private Fragment cur;
    private int curOutPos;

    private Fragment mark;
    private int markOutPos;

    FragmentedDocCursor(Fragment firstFragment) {
        cur = firstFragment;
        mark = firstFragment;
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


    public void appendMarkSuffix(StringBuilder sb, final int suffixLen) {
        assert suffixLen <= markLength();

        if (cur == mark) {
            // entire mark is in a single fragment, fast path 1
            appendSuffix(sb, suffixLen);
        } else {

            // look backwards until we find the fragment that starts the suffix
            Fragment f = cur;
            int suffixStart = curOutPos - suffixLen;
            while (f != null && f.outStart() > suffixStart) {
                f = f.prev;
            }
            assert f != null;

            if (f == cur) {
                // entire suffix is in a single fragment, fast path 2
                appendSuffix(sb, suffixLen);
            }

            sb.ensureCapacity(sb.length() + suffixLen);
            appendUntilCurPos(f, sb, suffixStart);
        }
    }

    public void appendSuffix(StringBuilder sb, int suffixLen) {
        cur.chars.appendChars(sb, curOutPos - cur.outStart - suffixLen, suffixLen);
    }

    public Chars getMarkImage() {
        Fragment f = mark;
        if (f == cur) { // same fragment, this is the fast path
            return f.chars.slice(markOutPos - f.outStart(), markLength());
        }

        StringBuilder sb = new StringBuilder(markLength());
        appendUntilCurPos(f, sb, markOutPos);
        assert sb.length() == markLength() : sb + " should have length " + markLength();
        return Chars.wrap(sb);
    }

    public void appendUntilCurPos(Fragment f, StringBuilder sb, int startOutPos) {
        assert f.outStart() <= startOutPos && startOutPos < f.outEnd();

        // append the suffix of the first fragment after the start pos
        f.appendAbs(sb, startOutPos, f.outEnd());
        f = f.next;
        while (f != cur) {
            // append whole fragments
            f.appendAbs(sb, f.outStart(), f.outEnd());
            f = f.next;
        }
        // append the prefix of the last fragment until the current pos
        f.appendAbs(sb, f.outStart(), curOutPos);
    }


    static final class Fragment {

        private final Chars chars;

        final @Nullable Fragment prev;
        @Nullable Fragment next;

        private final int outStart;
        private final int inStart;
        private final int inLength;

        Fragment(@Nullable Fragment prev, int inLength, Chars chars) {
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
            return "Fragment[" + inStart + ".." + outStart + "]\n" + chars;
        }
    }
}
