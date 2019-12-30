/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.util.document;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;

/**
 * Represents a mutable text document. Instances of this interface maintain
 * a coordinate system that is consistent with the original state of the file,
 * even after performing mutation operations.
 *
 * <p>For example, take a document containing the text "a".
 * You insert "k " at index 0. The document is now "k a". If you
 * now insert "g " at index 0, the document is now "k g a", instead
 * of "g k a", meaning that the index 0 is still relative to the old "a"
 * document.
 *
 * <p>Consider that all mutation operations shift the coordinate system
 * transparently.
 */
public interface MutableTextDocument extends TextDocument, Closeable {


    /** Replace a region with some new text. */
    void replace(TextRegion region, String textToReplace);


    /** Insert some text in the document. */
    default void insert(int offset, String textToInsert) {
        replace(createRegion(offset, 0), textToInsert);
    }


    /** Delete a region in the document. */
    default void delete(TextRegion region) {
        replace(region, "");
    }


    /**
     * Commit the document. The {@link #getUncommittedText() uncommitted text}
     * becomes the {@link #getText() text}, and subsequent operations use that
     * coordinate system.
     */
    @Override
    void close() throws IOException;


    /**
     * Returns the original text, source of the coordinate system used by mutation
     * operations.
     */
    @Override
    CharSequence getText();


    /** Returns the uncommitted text, that will be committed by {@link #close()}. */
    CharSequence getUncommittedText();


    static MutableTextDocument forFile(final Path file, final Charset charset) throws IOException {
        TextDocument doc = TextDocument.forFile(file, charset);
        return doc.newMutableDoc(ReplaceHandler.bufferedFile(doc.getText(), file, charset));
    }

}
