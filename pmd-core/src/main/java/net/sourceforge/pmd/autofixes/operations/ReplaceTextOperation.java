/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.autofixes.operations;

import java.util.Objects;

import net.sourceforge.pmd.autofixes.Document;

public class ReplaceTextOperation extends TextOperation {

    private final String replacementText;


    public ReplaceTextOperation(final int offset, final int length, final String replacementText) {
        super(offset, length);

        Objects.requireNonNull(replacementText, "replacementText must not be null");
        this.replacementText = replacementText;
    }

    @Override
    int applyTextOperationToDocument(final Document document) {
        document.replace(getRegion(), replacementText);

        return replacementText.length() - getLength();
    }
}
