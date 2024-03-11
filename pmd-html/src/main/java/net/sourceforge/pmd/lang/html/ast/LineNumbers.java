/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.html.ast;


import net.sourceforge.pmd.lang.document.Chars;

class LineNumbers {
    private final ASTHtmlDocument document;
    private final Chars htmlString;

    LineNumbers(ASTHtmlDocument document) {
        this.document = document;
        this.htmlString = document.getTextDocument().getText();
    }

    public void determine() {
        determineLocation(document, 0);
    }

    private int determineLocation(AbstractHtmlNode<?> n, int index) {
        int nextIndex = index;
        int nodeLength = 0;
        int textLength = 0;

        if (n instanceof ASTHtmlDocument) {
            nextIndex = index;
        } else if (n instanceof ASTHtmlComment) {
            nextIndex = htmlString.indexOf("<!--", nextIndex);
        } else if (n instanceof ASTHtmlElement) {
            nextIndex = htmlString.indexOf("<" + n.getXPathNodeName(), nextIndex);
            nodeLength = htmlString.indexOf(">", nextIndex) - nextIndex + 1;
        } else if (n instanceof ASTHtmlCDataNode) {
            nextIndex = htmlString.indexOf("<![CDATA[", nextIndex);
        } else if (n instanceof ASTHtmlXmlDeclaration) {
            nextIndex = htmlString.indexOf("<?", nextIndex);
        } else if (n instanceof ASTHtmlTextNode) {
            textLength = ((ASTHtmlTextNode) n).getWholeText().length();
        } else if (n instanceof ASTHtmlDocumentType) {
            nextIndex = index;
        }

        setBeginLocation(n, nextIndex);

        nextIndex += nodeLength;

        for (net.sourceforge.pmd.lang.ast.Node child : n.children()) {
            nextIndex = determineLocation((AbstractHtmlNode<?>) child, nextIndex);
        }

        // autoclosing element, eg <a />
        boolean isAutoClose = n.getNumChildren() == 0
                && n instanceof ASTHtmlElement
                // nextIndex is up to the closing > at this point
                && htmlString.startsWith("/>", nextIndex - 2);

        if (n instanceof ASTHtmlDocument) {
            nextIndex = htmlString.length();
        } else if (n instanceof ASTHtmlElement && !isAutoClose) {
            nextIndex += 2 + n.getXPathNodeName().length() + 1; // </nodename>
        } else if (n instanceof ASTHtmlComment) {
            nextIndex += 4 + 3; // <!-- and -->
            nextIndex += ((ASTHtmlComment) n).getData().length();
        } else if (n instanceof ASTHtmlTextNode) {
            nextIndex += textLength;
        } else if (n instanceof ASTHtmlCDataNode) {
            nextIndex += "<![CDATA[".length() + ((ASTHtmlCDataNode) n).getText().length() + "]]>".length();
        } else if (n instanceof ASTHtmlXmlDeclaration) {
            nextIndex = htmlString.indexOf("?>", nextIndex) + 2;
        } else if (n instanceof ASTHtmlDocumentType) {
            nextIndex = htmlString.indexOf(">", nextIndex) + 1;
        }

        setEndLocation(n, nextIndex - 1);
        return nextIndex;
    }

    private void setBeginLocation(AbstractHtmlNode<?> n, int index) {
        if (n != null) {
            n.startOffset = index;
        }
    }

    private void setEndLocation(AbstractHtmlNode<?> n, int index) {
        if (n != null) {
            n.endOffset = index;
        }
    }
}
