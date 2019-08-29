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

/** Represents a text document. */
public interface Document {

    /**
     * Convert the representation of the given region.
     *
     * @throws IndexOutOfBoundsException If 'check', and the first arg does
     *                                   not identify a valid region in this document
     */
    RegionByLine mapToLine(RegionByOffset region, boolean check);


    /**
     * Convert the representation of the given region.
     *
     * @throws IndexOutOfBoundsException If 'check', and the first arg does
     *                                   not identify a valid region in this document
     */
    RegionByOffset mapToOffset(RegionByLine region, boolean check);


    /** Returns the text of this document. */
    CharSequence getText();


    static Document forFile(final Path file, final Charset charset) throws IOException {
        byte[] bytes = Files.readAllBytes(requireNonNull(file));
        String text = new String(bytes, requireNonNull(charset));
        return forCode(text);
    }


    static Document forCode(final String source) {
        return new DocumentImpl(source, ReplaceFunction.NOOP);
    }

}
