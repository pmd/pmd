/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.html.ast;

import net.sourceforge.pmd.lang.ast.SourceCodePositioner;

class LineNumbers {
    private final HtmlDocument document;
    private String htmlString;
    private SourceCodePositioner sourceCodePositioner;

    LineNumbers(HtmlDocument document, String htmlString) {
        this.document = document;
        this.htmlString = htmlString;
        this.sourceCodePositioner = new SourceCodePositioner(htmlString);
    }

    public void determine() {
        determineLocation(document, 0);
    }

    private int determineLocation(AbstractHtmlNode<?> n, int index) {
        int nextIndex = index;
        int nodeLength = 0;
        int textLength = 0;

        if (n instanceof HtmlDocument) {
            nextIndex = index;
        } else if (n instanceof HtmlComment) {
            nextIndex = htmlString.indexOf("<!--", nextIndex);
        } else if (n instanceof HtmlElement) {
            nextIndex = htmlString.indexOf("<" + n.getXPathNodeName(), nextIndex);
            nodeLength = htmlString.indexOf(">", nextIndex) - nextIndex + 1;
        } else if (n instanceof HtmlCDataNode) {
            nextIndex = htmlString.indexOf("<![CDATA[", nextIndex);
        } else if (n instanceof HtmlXmlDeclaration) {
            nextIndex = htmlString.indexOf("<?", nextIndex);
        } else if (n instanceof HtmlTextNode) {
            textLength = ((HtmlTextNode) n).getText().length();
        } else if (n instanceof HtmlDocumentType) {
            nextIndex = index;
        }

        setBeginLocation(n, nextIndex);

        nextIndex += nodeLength;

        for (net.sourceforge.pmd.lang.ast.Node child : n.children()) {
            nextIndex = determineLocation((AbstractHtmlNode<?>) child, nextIndex);
        }

        // autoclosing element, eg <a />
        boolean isAutoClose = n.getNumChildren() == 0
                && n instanceof HtmlElement
                // nextIndex is up to the closing > at this point
                && htmlString.startsWith("/>", nextIndex - 2);

        if (n instanceof HtmlDocument) {
            nextIndex = htmlString.length();
        } else if (n instanceof HtmlElement && !isAutoClose) {
            nextIndex += 2 + n.getXPathNodeName().length() + 1; // </nodename>
        } else if (n instanceof HtmlComment) {
            nextIndex += 4 + 3; // <!-- and -->
            nextIndex += ((HtmlComment) n).getData().length();
        } else if (n instanceof HtmlTextNode) {
            nextIndex += textLength;
        } else if (n instanceof HtmlCDataNode) {
            nextIndex += "<![CDATA[".length() + ((HtmlCDataNode) n).getText().length() + "]]>".length();
        } else if (n instanceof HtmlXmlDeclaration) {
            nextIndex = htmlString.indexOf("?>", nextIndex) + 2;
        } else if (n instanceof HtmlDocumentType) {
            nextIndex = htmlString.indexOf(">", nextIndex) + 1;
        }

        setEndLocation(n, nextIndex - 1);
        return nextIndex;
    }

    private void setBeginLocation(AbstractHtmlNode<?> n, int index) {
        if (n != null) {
            int line = sourceCodePositioner.lineNumberFromOffset(index);
            int column = sourceCodePositioner.columnFromOffset(line, index);
            n.setBeginLine(line);
            n.setBeginColumn(column);
        }
    }

    private void setEndLocation(AbstractHtmlNode<?> n, int index) {
        if (n != null) {
            int line = sourceCodePositioner.lineNumberFromOffset(index);
            int column = sourceCodePositioner.columnFromOffset(line, index);
            n.setEndLine(line);
            n.setEndColumn(column);
        }
    }
}
