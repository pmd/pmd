/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.autofixes;

import java.util.Objects;

public class DocumentImp implements Document {

    private StringBuffer stream;

    public DocumentImp() {
        stream = new StringBuffer();
    }

    public DocumentImp(final String documentAsString) {
        Objects.requireNonNull(documentAsString);

        stream = new StringBuffer(documentAsString);
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
        return new String(stream);
    }

    @Override
    public int getLength() {
        return stream.length();
    }
}
