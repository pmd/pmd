/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.document;

import static java.util.Objects.requireNonNull;

class ReplaceDocumentOperation extends DocumentOperation {

    private final String textToReplace;

    ReplaceDocumentOperation(TextRegion region, final String textToReplace) {
        super(region);
        this.textToReplace = requireNonNull(textToReplace);
    }

    @Override
    public void apply(final MutableDocument document) {
        document.replace(getRegion(), textToReplace);
    }

}
