/*
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
        boolean selfClosingElement = false;
        boolean sourceBackedElement = false;

        if (n instanceof ASTHtmlDocument) {
            nextIndex = index;
        } else if (n instanceof ASTHtmlComment) {
            nextIndex = indexOfComment(nextIndex);
        } else if (n instanceof ASTHtmlElement) {
            int openingElementStart = indexOfOpeningElement(n.getXPathNodeName(), nextIndex);
            if (openingElementStart >= 0) {
                sourceBackedElement = true;
                nextIndex = openingElementStart;
                int openingElementEnd = endOfOpeningElement(nextIndex);
                nodeLength = openingElementEnd - nextIndex;
                int lastContentIndex = openingElementEnd - 2;
                while (lastContentIndex >= nextIndex && isHtmlWhitespace(htmlString.charAt(lastContentIndex))) {
                    lastContentIndex--;
                }
                selfClosingElement = lastContentIndex >= nextIndex && htmlString.charAt(lastContentIndex) == '/';
            }
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

        // nextIndex is up to the closing tag at this point
        int closeElementEnd = n instanceof ASTHtmlElement && sourceBackedElement && !selfClosingElement
                ? endOfClosingElement(n.getXPathNodeName(), nextIndex)
                : -1;

        if (n instanceof ASTHtmlDocument) {
            nextIndex = htmlString.length();
        } else if (closeElementEnd >= 0) {
            nextIndex = closeElementEnd;
        } else if (n instanceof ASTHtmlComment) {
            nextIndex = endOfComment(nextIndex);
        } else if (n instanceof ASTHtmlTextNode) {
            nextIndex += textLength;
        } else if (n instanceof ASTHtmlCDataNode) {
            nextIndex += "<![CDATA[".length() + ((ASTHtmlCDataNode) n).getText().length() + "]]>".length();
        } else if (n instanceof ASTHtmlXmlDeclaration) {
            nextIndex = htmlString.indexOf("?>", nextIndex) + 2;
        } else if (n instanceof ASTHtmlDocumentType) {
            nextIndex = htmlString.indexOf(">", nextIndex) + 1;
        }

        setEndLocation(n, Math.max(index, nextIndex - 1));
        return nextIndex;
    }

    private int indexOfOpeningElement(String name, int fromIndex) {
        int candidate = htmlString.indexOf("<", fromIndex);
        while (candidate >= 0) {
            int nameStart = candidate + 1;
            int nameEnd = nameStart + name.length();
            if (nameEnd <= htmlString.length() && matchesName(name, nameStart)
                    && (nameEnd == htmlString.length() || isTagNameBoundary(htmlString.charAt(nameEnd)))) {
                return candidate;
            }
            candidate = htmlString.indexOf("<", candidate + 1);
        }
        return -1;
    }

    private int endOfOpeningElement(int fromIndex) {
        char quote = 0;
        for (int i = fromIndex; i < htmlString.length(); i++) {
            char current = htmlString.charAt(i);
            if (quote != 0) {
                if (current == quote) {
                    quote = 0;
                }
            } else if (current == '\'' || current == '"') {
                quote = current;
            } else if (current == '>') {
                return i + 1;
            }
        }
        return htmlString.length();
    }

    private int endOfClosingElement(String name, int fromIndex) {
        if (!htmlString.startsWith("</", fromIndex)) {
            return -1;
        }

        int nameStart = fromIndex + 2;
        if (nameStart + name.length() > htmlString.length()) {
            return -1;
        }

        if (!matchesName(name, nameStart)) {
            return -1;
        }

        int end = nameStart + name.length();
        while (end < htmlString.length() && isHtmlWhitespace(htmlString.charAt(end))) {
            end++;
        }
        return end < htmlString.length() && htmlString.charAt(end) == '>' ? end + 1 : -1;
    }

    private boolean isHtmlWhitespace(char c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\f' || c == '\r';
    }

    private boolean isTagNameBoundary(char c) {
        return isHtmlWhitespace(c) || c == '/' || c == '>';
    }

    private boolean matchesName(String name, int nameStart) {
        for (int i = 0; i < name.length(); i++) {
            if (Character.toLowerCase(htmlString.charAt(nameStart + i)) != Character.toLowerCase(name.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Jsoup adds synthetic comment nodes when encountering malformed input.
     * Due to this, there might be a comment node present that's not backed by any actual text content.
     * This method handles the index lookup for these cases safely by returning {@code fromIndex} instead of {@code -1}.
     */
    private int indexOfComment(int fromIndex) {
        int idx = htmlString.indexOf("<!--", fromIndex);
        return idx < 0 ? fromIndex : idx;
    }

    /**
    /* A synthetic Jsoup comment isn't backed by a real <!--...--> sequence
    /* It runs from '<' to the next bare '>' instead
     */
    private int endOfComment(int nextIndex) {
        boolean isRealComment = htmlString.startsWith("<!--", nextIndex);
        String closeMarker = isRealComment ? "-->" : ">";
        int closeIndex = htmlString.indexOf(closeMarker, nextIndex);
        return closeIndex < 0 ? htmlString.length() : closeIndex + closeMarker.length();
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
