/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.io.IOUtils;

import net.sourceforge.pmd.lang.ast.internal.SharingCharSeq;

/**
 * This stream buffers the whole file in memory before parsing,
 * and shares the char array between all tokens.
 *
 * @author Clément Fournier
 */
public class JavaCharStream extends JavaCharStreamBase {

    // full text with nothing escaped and all
    private final SharingCharSeq seq;

    private int[] startOffsets;

    private JavaCharStream(String fulltext) {
        super(new StringReader(fulltext));
        this.seq = new SharingCharSeq(fulltext);
        this.startOffsets = new int[bufsize];
        maxNextCharInd = seq.length();

        nextCharBuf = null; // the char buf is emulated by the TokenisedCharseq and isn't needed
    }

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
    protected void beforeReadChar() {
        startOffsets[bufpos + 1] = nextCharInd + 1;
    }


    public int getStartOffset() {
        return startOffsets[tokenBegin];
    }

    public int getEndOffset() {
        if (bufpos >= startOffsets.length) {
            return seq.length();
        } else {
            return startOffsets[bufpos] + 1;
        }
    }

    public SharingCharSeq getFullText() {
        return seq;
    }

    @Override
    protected char ReadByte() throws IOException {
        ++nextCharInd;

        if (nextCharInd >= seq.length()) {
            if (bufpos != 0) {
                --bufpos;
                backup(0);
            } else {
                bufline[bufpos] = line;
                bufcolumn[bufpos] = column;
            }
            throw new IOException();
        }

        return seq.charAt(nextCharInd);
    }


    @Override
    public String GetImage() {
        // TODO GetImage should return a CharSequence based on the current shared sequence
        return super.GetImage();
    }

    @Override
    protected void FillBuff() {
        throw new IllegalStateException("Buffer shouldn't be refilled");
    }

    public static JavaCharStream createStream(Reader dstream) {
        String fullText;
        try {
            fullText = IOUtils.toString(dstream);
            dstream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new JavaCharStream(fullText);
    }
}
