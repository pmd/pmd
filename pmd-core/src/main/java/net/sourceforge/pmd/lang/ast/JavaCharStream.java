/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.io.IOUtils;

import net.sourceforge.pmd.lang.ast.impl.TokenDocument;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;

/**
 * This stream buffers the whole file in memory before parsing,
 * and track start/end offsets of tokens. This allows building {@link JavaccToken}.
 * The buffer is assumed to be composed of only ASCII characters,
 * and the stream unescapes Unicode escapes. The {@link #getTokenDocument() token document}
 * stores the original file with escapes and all.
 *
 * TODO this is to be moved into the impl.javacc subpackage
 */
public class JavaCharStream extends JavaCharStreamBase {

    // full text with nothing escaped and all
    private final String fullText;
    private final TokenDocument document;

    private int[] startOffsets;

    public JavaCharStream(String fulltext) {
        super(new StringReader(fulltext));
        this.fullText = fulltext;
        this.document = new TokenDocument(fullText);
        this.startOffsets = new int[bufsize];
        maxNextCharInd = fullText.length();

        nextCharBuf = null;
    }

    public JavaCharStream(Reader toDump) {
        this(toString(toDump));
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
    protected void beforeReadChar() {
        if (bufpos + 1 < available) {
            startOffsets[bufpos + 1] = nextCharInd + 1;
        }
    }

    public int getStartOffset() {
        return startOffsets[tokenBegin];
    }

    public int getEndOffset() {
        if (bufpos >= startOffsets.length) {
            return fullText.length();
        } else {
            return startOffsets[bufpos] + 1; // + 1 for exclusive
        }
    }

    public TokenDocument getTokenDocument() {
        return document;
    }

    @Override
    protected char ReadByte() throws IOException {
        ++nextCharInd;

        if (nextCharInd >= fullText.length()) {
            if (bufpos != 0) {
                --bufpos;
                backup(0);
            } else {
                bufline[bufpos] = line;
                bufcolumn[bufpos] = column;
            }
            throw new IOException();
        }

        return fullText.charAt(nextCharInd);
    }


    @Override
    protected void FillBuff() {
        throw new IllegalStateException("Buffer shouldn't be refilled");
    }

    private static String toString(Reader dstream) {
        try (Reader r = dstream) {
            return IOUtils.toString(r);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
