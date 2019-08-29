/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.document;

import static java.util.Objects.requireNonNull;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
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
public interface MutableDocument extends Document, Closeable {

    /** Insert some text in the document. */
    void insert(int beginLine, int beginColumn, String textToInsert);


    /** Insert some text in the document. */
    void insert(int offset, String textToInsert);


    /** Replace a region with some new text. */
    void replace(TextRegion region, String textToReplace);


    /** Delete a region in the document. */
    void delete(TextRegion region);


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


    /** Returns the uncommitted text. */
    CharSequence getUncommittedText();


    static MutableDocument forFile(final Path file, final Charset charset) throws IOException {
        byte[] bytes = Files.readAllBytes(requireNonNull(file));
        String text = new String(bytes, requireNonNull(charset));
        return forCode(text, ReplaceFunction.bufferedFile(text, file, charset));
    }


    static MutableDocument forFile(String code, final Path file, final Charset charset) {
        return forCode(code, ReplaceFunction.bufferedFile(code, file, charset));
    }


    static MutableDocument forCode(final String source, final ReplaceFunction writer) {
        return new DocumentImpl(source, writer);
    }

}
