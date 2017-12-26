/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.autofixes.operations;

import java.util.Objects;

import net.sourceforge.pmd.autofixes.Document;
import net.sourceforge.pmd.autofixes.Region;

/**
 * Note: this implementation should only be used as a proof of concept and it is nowhere near a final implementation to
 * be used in production code.
 */
public class DocumentImp implements Document {

    private StringBuilder stream;

    public DocumentImp() {
        stream = new StringBuilder();
    }

    public DocumentImp(final String documentAsString) {
        Objects.requireNonNull(documentAsString);

        stream = new StringBuilder(documentAsString);
    }

    @Override
    public void insert(int offset, String textToInsert) {
        Objects.requireNonNull(textToInsert, "textToInsert must not be null");
        if (offset < 0) {
            throw new IllegalArgumentException("offset must be non-negative");
        } else if (offset > stream.length()) {
            throw new IllegalArgumentException("offset should be at most the size of the document");
        }

        stream.insert(offset, textToInsert);
    }

    @Override
    public void replace(final Region region, final String textToReplace) {
        Objects.requireNonNull(region, "region must not be null");
        Objects.requireNonNull(textToReplace, "textToReplace must not be null");

        stream.replace(region.getOffset(), region.getOffsetAfterEnding(), textToReplace);
    }

    @Override
    public void delete(final Region region) {
        Objects.requireNonNull(region, "region must not be null");
        Objects.requireNonNull(region, "textToReplace must not be null");

        stream.delete(region.getOffset(), region.getOffsetAfterEnding());
    }


    @Override
    public String getAsString() {
        return stream.toString();
    }

    @Override
    public int getLength() {
        return stream.length();
    }
}
