/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.document;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import net.sourceforge.pmd.document.TextRegion.RegionByLine;
import net.sourceforge.pmd.document.TextRegion.RegionByOffset;

/**
 * Represents a text document. A document provides methods to identify
 * regions of text.
 */
public interface Document {

    /**
     * Create a new line-based region.
     *
     * @throws IndexOutOfBoundsException If the argument does not identify a valid region in this document
     */
    RegionByLine createRegion(final int beginLine, final int beginColumn, final int endLine, final int endColumn);


    /**
     * Create a new offset-based region.
     *
     * @throws IndexOutOfBoundsException If the argument does not identify a valid region in this document
     */
    RegionByOffset createRegion(final int offset, final int length);


    /**
     * Convert the representation of the given region.
     *
     * @throws IndexOutOfBoundsException If the argument does not identify a valid region in this document
     */
    RegionByLine mapToLine(RegionByOffset region);


    /**
     * Convert the representation of the given region.
     *
     * @throws IndexOutOfBoundsException If the argument does not identify a valid region in this document
     */
    RegionByOffset mapToOffset(RegionByLine region);


    /** Returns the text of this document. */
    CharSequence getText();


    /** Returns a region of the {@link #getText() text} as a character sequence. */
    CharSequence subSequence(TextRegion region);


    /** Returns a mutable document that uses the given replace handler. */
    MutableDocument newMutableDoc(ReplaceHandler out);


    static Document forFile(final Path file, final Charset charset) throws IOException {
        byte[] bytes = Files.readAllBytes(requireNonNull(file));
        String text = new String(bytes, requireNonNull(charset));
        return forCode(text);
    }


    static Document forCode(final String source) {
        return new DocumentImpl(source);
    }

}
