/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.io;

import java.io.EOFException;
import java.io.IOException;

import net.sourceforge.pmd.lang.ast.CharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.util.document.FileLocation;
import net.sourceforge.pmd.util.document.TextDocument;

public class NewCharStream implements CharStream {

    private final JavaccTokenDocument document;
    private final EscapeTracker.Cursor cursor;

    private NewCharStream(JavaccTokenDocument document, EscapeTracker.Cursor cursor) {
        this.document = document;
        this.cursor = cursor;
    }

    public static CharStream open(JavaccTokenDocument doc) throws IOException {
        try (EscapeAwareReader reader = doc.newReader(doc.getTextDocument().getText())) {
            reader.translate();
            return new NewCharStream(doc, reader.escapes.new Cursor(reader.input));
        }
    }

    @Override
    public JavaccTokenDocument getTokenDocument() {
        return document;
    }

    @Override
    public char readChar() throws EOFException {
        return cursor.next();
    }

    @Override
    public char BeginToken() throws EOFException {
        cursor.mark();
        return cursor.next();
    }

    @Override
    public String GetImage() {
        StringBuilder sb = new StringBuilder();
        cursor.markToString(sb);
        return sb.toString();
    }

    @Override
    public void backup(int amount) {
        cursor.backup(amount);
    }

    @Override
    public int getEndColumn() {
        return endLocation().getEndColumn();
    }

    @Override
    public int getEndLine() {
        return endLocation().getEndLine();
    }

    private FileLocation endLocation() {
        TextDocument textDoc = document.getTextDocument();
        return textDoc.toLocation(textDoc.createRegion(getEndOffset(), 0));
    }

    @Override
    public int getStartOffset() {
        return cursor.markOutOffset();
    }

    @Override
    public int getEndOffset() {
        return cursor.curOutOffset();
    }
}
