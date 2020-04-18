/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc;

import java.io.EOFException;
import java.io.IOException;

/**
 * This stream buffers the whole file in memory before parsing,
 * and track start/end offsets of tokens. This allows building {@link JavaccToken}.
 * The buffer is assumed to be composed of only ASCII characters,
 * and the stream unescapes Unicode escapes. The {@link #getTokenDocument() token document}
 * stores the original file with escapes and all.
 */
public class JavaCharStream extends JavaCharStreamBase {

    // full text with nothing escaped and all
    private final String fullText;
    private final JavaccTokenDocument document;

    private int[] startOffsets;

    public JavaCharStream(JavaccTokenDocument document) {
        super(document.getTextDocument().newReader());
        this.fullText = document.getFullText();
        this.document = document;
        this.startOffsets = new int[bufsize];
        maxNextCharInd = fullText.length();

        nextCharBuf = null;
    }

    @Override
    protected void ExpandBuff(boolean wrapAround) {
        int[] newStartOffsets = new int[bufsize + 2048];

        if (wrapAround) {
            System.arraycopy(startOffsets, tokenBegin, newStartOffsets, 0, bufsize - tokenBegin);
            System.arraycopy(startOffsets, 0, newStartOffsets, bufsize - tokenBegin, bufpos);
            startOffsets = newStartOffsets;
        } else {
            System.arraycopy(startOffsets, tokenBegin, newStartOffsets, 0, bufsize - tokenBegin);
            startOffsets = newStartOffsets;
        }

        super.ExpandBuff(wrapAround);
    }

    @Override
    protected void UpdateLineColumn(char c) {
        startOffsets[bufpos] = nextCharInd;
        super.UpdateLineColumn(c);
    }

    @Override
    public int getStartOffset() {
        return startOffsets[tokenBegin];
    }

    @Override
    public int getEndOffset() {
        if (isAtEof()) {
            return fullText.length();
        } else {
            return startOffsets[bufpos] + 1; // + 1 for exclusive
        }
    }

    @Override
    public JavaccTokenDocument getTokenDocument() {
        return document;
    }

    @Override
    public String GetImage() {
        if (bufpos >= tokenBegin) {
            return new String(buffer, tokenBegin, bufpos - tokenBegin + 1);
        } else {
            return new String(buffer, tokenBegin, bufsize - tokenBegin)
                + new String(buffer, 0, bufpos + 1);
        }
    }

    @Override
    protected char ReadByte() throws IOException {
        ++nextCharInd;

        if (isAtEof()) {
            if (bufpos != 0) {
                --bufpos;
                if (bufpos < 0) {
                    bufpos += bufsize;
                }
            } else {
                bufline[bufpos] = line;
                bufcolumn[bufpos] = column;
                startOffsets[bufpos] = fullText.length();
            }
            throw new EOFException();
        }

        return fullText.charAt(nextCharInd);
    }

    private boolean isAtEof() {
        return nextCharInd >= fullText.length();
    }


    @Override
    protected void FillBuff() {
        throw new IllegalStateException("Buffer shouldn't be refilled");
    }

}
