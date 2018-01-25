/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.document;

public class DeleteDocumentOperation extends DocumentOperation {

    public DeleteDocumentOperation(final int beginLine, final int endLine, final int beginColumn, final int endColumn) {
        super(beginLine, endLine, beginColumn, endColumn);
    }

    @Override
    public void apply(final Document document) {
        document.delete(getRegionByLine());
    }
}
