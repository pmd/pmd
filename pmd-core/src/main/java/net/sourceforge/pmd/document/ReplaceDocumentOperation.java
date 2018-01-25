/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.document;

import static java.util.Objects.requireNonNull;

public class ReplaceDocumentOperation extends DocumentOperation {

    private final String textToReplace;

    public ReplaceDocumentOperation(final int beginLine, final int endLine, final int beginColumn, final int endColumn, final String textToReplace) {
        super(beginLine, endLine, beginColumn, endColumn);
        this.textToReplace = requireNonNull(textToReplace);
    }

    @Override
    public void apply(final Document document) {
        document.replace(getRegionByLine(), textToReplace);
    }
}
