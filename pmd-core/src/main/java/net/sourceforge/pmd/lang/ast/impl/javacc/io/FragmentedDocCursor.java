/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc.io;

import java.io.EOFException;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.NonNull;
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
        checkAssertions();
    }


    char next() throws EOFException {
        Fragment f = cur;

        while (f != null && curOutPos >= f.outEnd()) {
            f = f.next; // this is a loop to handle chained zero-length fragments
        }

        if (f == null) {
            throw EOF;
        }

        cur = f;
        return f.charAt(curOutPos++);
    }

    void backup(final int amount) {
        final int posToRetreatTo = curOutPos - amount;
        this.curOutPos = posToRetreatTo;
        this.cur = findBackwards(posToRetreatTo);
        checkAssertions();
    }


    void appendMarkSuffix(StringBuilder sb, final int suffixLen) {
        assert suffixLen <= markLength() : "Suffix is greater than the mark length? " + suffixLen + " > " + markLength();

        if (cur == mark || cur.outStart() <= curOutPos - suffixLen) {
            // entire suffix is in the last fragment, fast path
            cur.chars.appendChars(sb, curOutPos - cur.outStart - suffixLen, suffixLen);
        } else {
            int suffixStart = curOutPos - suffixLen;
            Fragment f = findBackwards(suffixStart);
            sb.ensureCapacity(sb.length() + suffixLen);
            appendUntilCurPos(f, sb, suffixStart);
        }
    }

    Chars getMarkImage() {
        Fragment f = mark;
        if (f == cur) { // same fragment, this is the fast path
            return f.chars.slice(markOutPos - f.outStart(), markLength());
        }

        StringBuilder sb = new StringBuilder(markLength());
        appendUntilCurPos(f, sb, markOutPos);
        assert sb.length() == markLength() : sb + " should have length " + markLength();
        return Chars.wrap(sb);
    }

    private void appendUntilCurPos(Fragment f, StringBuilder sb, int startOutPos) {
        assert f != null && f != cur; // this won't work otherwise
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


    private void checkAssertions() {
        assert mark != null && cur != null : "Null mark or current fragment";
        assert cur.outStart() >= mark.outStart() : "Mark is after the current fragment";
    }

    // find the fragment that contains the given out offset
    private @NonNull Fragment findBackwards(int posToRetreatTo) {
        Fragment f = cur;
        while (f != null && f.outStart() > posToRetreatTo) {
            f = f.prev;
        }
        return Objects.requireNonNull(f, "Cannot retreat to " + posToRetreatTo);
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
