/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.ConcurrentModificationException;

import net.sourceforge.pmd.internal.util.BaseCloseable;
import net.sourceforge.pmd.util.document.io.TextFile;


final class TextDocumentImpl extends BaseCloseable implements TextDocument {

    private final TextFile backend;

    private long curStamp;

    private SourceCodePositioner positioner;
    private CharSequence text;

    private final String fileName;

    private TextEditorImpl curEditor;

    TextDocumentImpl(TextFile backend) throws IOException {
        this.backend = backend;
        this.curStamp = backend.fetchStamp();

        // charbuffer doesn't copy the char array for subsequence operations
        this.text = CharBuffer.wrap(backend.readContents());
        this.positioner = null;
        this.fileName = backend.getFileName();
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public boolean isReadOnly() {
        return backend.isReadOnly();
    }

    @Override
    public TextEditor newEditor(EditorCommitHandler handler) throws IOException {
        ensureOpen();
        if (curEditor != null) {
            throw new ConcurrentModificationException("An editor is already open on this document");
        }
        curEditor = new TextEditorImpl(this, backend, handler);
        return curEditor;
    }

    void closeEditor(CharSequence text, long stamp) {

        curEditor = null;
        this.text = text.toString();
        this.positioner = null;
        this.curStamp = stamp;

    }

    @Override
    protected void doClose() throws IOException {
        if (curEditor != null) {
            curEditor.sever();
            curEditor = null;
            throw new IllegalStateException("Unclosed editor!");
        }

        backend.close();
    }

    @Override
    public FileLocation toLocation(TextRegion region) {
        checkInRange(region.getStartOffset(), region.getLength());

        if (positioner == null) {
            // if nobody cares about lines, this is not computed
            positioner = new SourceCodePositioner(text);
        }

        int bline = positioner.lineNumberFromOffset(region.getStartOffset());
        int bcol = positioner.columnFromOffset(bline, region.getStartOffset());
        int eline = positioner.lineNumberFromOffset(region.getEndOffset());
        int ecol = positioner.columnFromOffset(eline, region.getEndOffset());

        return new FileLocation(
            fileName,
            bline, bcol,
            eline, ecol
        );
    }

    @Override
    public TextRegion createRegion(int startOffset, int length) {
        checkInRange(startOffset, length);
        return TextRegionImpl.fromOffsetLength(startOffset, length);
    }


    void checkInRange(int startOffset, int length) {
        if (startOffset < 0) {
            throw InvalidRegionException.negativeQuantity("Start offset", startOffset);
        } else if (length < 0) {
            throw InvalidRegionException.negativeQuantity("Region length", length);
        } else if (startOffset + length > getLength()) {
            throw InvalidRegionException.regionOutOfBounds(startOffset, startOffset + length, getLength());
        }
    }

    @Override
    public int getLength() {
        return getText().length();
    }

    @Override
    public CharSequence getText() {
        return text;
    }

    long getCurStamp() {
        return curStamp;
    }


    @Override
    public CharSequence subSequence(TextRegion region) {
        if (region.isEmpty()) {
            return "";
        }
        return getText().subSequence(region.getStartOffset(), region.getEndOffset());
    }

}
