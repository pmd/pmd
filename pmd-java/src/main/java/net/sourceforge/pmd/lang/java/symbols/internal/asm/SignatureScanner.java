/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.util.AssertionUtil;

/**
 * Base class to scan a type signature.
 */
class SignatureScanner {

    protected final String chars;
    protected final int start;
    protected final int end;


    SignatureScanner(String descriptor) {
        if (descriptor == null || descriptor.isEmpty()) {
            throw new IllegalArgumentException("Type descriptor \"" + descriptor + "\" is empty or null");
        }

        this.chars = descriptor;
        this.start = 0;
        this.end = descriptor.length();
    }

    SignatureScanner(String chars, int start, int end) {
        assert chars != null;
        AssertionUtil.assertValidStringRange(chars, start, end);
        this.chars = chars;
        this.start = start;
        this.end = end;
    }


    public char charAt(int off) {
        return off == end ? 0 : chars.charAt(off);
    }

    public void dumpChars(int start, int end, StringBuilder builder) {
        builder.append(chars, start, end);
    }

    public int consumeChar(int start, char l, String s) {
        if (charAt(start) != l) {
            throw expected(s, start);
        }
        return start + 1;
    }

    public int consumeChar(int start, char l) {
        return consumeChar(start, l, "" + l);
    }

    public int nextIndexOf(final int start, char stop) {
        int cur = start;
        while (cur < end && charAt(cur) != stop) {
            cur++;
        }
        return cur;
    }

    public int nextIndexOfAny(final int start, char stop, char stop2) {
        int cur = start;
        while (cur < end) {
            char c = charAt(cur);
            if (c == stop || c == stop2) {
                break;
            }
            cur++;
        }
        return cur;
    }


    public RuntimeException expected(String expectedWhat, int pos) {
        final String indent = "    ";
        String sb = "Expected " + expectedWhat + ":\n"
            + indent + bufferToString() + "\n"
            + indent + StringUtils.repeat(' ', pos - start) + '^' + "\n";
        return new IllegalArgumentException(sb);
    }

    public void expectEoI(int e) {
        consumeChar(e, (char) 0, "end of input");
    }

    public String bufferToString() {
        return bufferToString(start, end);
    }

    public String bufferToString(int start, int end) {
        if (start == end) {
            return "";
        }
        return chars.substring(start, end);
    }

    @Override
    public String toString() {
        return "TypeBuilder{sig=" + bufferToString() + '}';
    }
}
