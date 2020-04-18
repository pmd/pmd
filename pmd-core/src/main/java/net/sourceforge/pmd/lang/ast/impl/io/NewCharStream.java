/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.io;

import java.io.IOException;

import net.sourceforge.pmd.lang.ast.CharStream;
import net.sourceforge.pmd.lang.ast.impl.io.EscapeTracker.Cursor;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;

public class NewCharStream implements CharStream {

    private final JavaccTokenDocument document;
    private final Cursor cursor;

    public NewCharStream(JavaccTokenDocument document, EscapeTracker.Cursor cursor) {
        this.document = document;
        this.cursor = cursor;
    }

    public static CharStream consume(EscapeAwareReader reader, JavaccTokenDocument doc) throws IOException {
        try (EscapeAwareReader r = reader) {
            reader.translate();
            return new NewCharStream(doc, reader.escapes.new Cursor(reader.input));
        }
    }

    @Override
    public JavaccTokenDocument getTokenDocument() {
        return document;
    }

    @Override
    public char readChar() {
        return cursor.next();
    }

    @Override
    public char BeginToken() {
        char c = cursor.next();
        cursor.mark();
        return c;
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
        return 0; // TODO
    }

    @Override
    public int getEndLine() {
        return 0; // TODO
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
