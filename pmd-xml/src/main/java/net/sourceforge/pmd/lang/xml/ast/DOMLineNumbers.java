/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.xml.ast;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;

/**
 *
 */
class DOMLineNumbers {
    private final Document document;
    private final String xmlString;
    private Map<Integer, Integer> lines;

    public DOMLineNumbers(Document document, String xmlString) {
        this.document = document;
        this.xmlString = xmlString;
    }
    
    public void determine() {
        calculateLinesMap();
        determineLocation(document, 0);
    }
    private int determineLocation(Node n, int index) {
        int nextIndex = index;
        if (n.getNodeType() == Node.DOCUMENT_TYPE_NODE) {
            nextIndex = xmlString.indexOf("<!DOCTYPE", nextIndex);
        } else if (n.getNodeType() == Node.COMMENT_NODE) {
            nextIndex = xmlString.indexOf("<!--", nextIndex);
        } else if (n.getNodeType() == Node.ELEMENT_NODE) {
            nextIndex = xmlString.indexOf("<" + n.getNodeName(), nextIndex);
        } else if (n.getNodeType() == Node.CDATA_SECTION_NODE) {
            nextIndex = xmlString.indexOf("<![CDATA[", nextIndex);
        } else if (n.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) {
            ProcessingInstruction pi = (ProcessingInstruction)n;
            nextIndex = xmlString.indexOf("<?" + pi.getTarget(), nextIndex);
        } else if (n.getNodeType() == Node.TEXT_NODE) {
            String te = unexpandEntities(n, n.getNodeValue());
            int newIndex = xmlString.indexOf(te, nextIndex);
            if (newIndex > 0) {
                nextIndex = newIndex;
            } else {
                System.out.println("Still not found: " + n.getNodeValue());
            }
        } else if (n.getNodeType() == Node.ENTITY_REFERENCE_NODE) {
            nextIndex = xmlString.indexOf("&" + n.getNodeName() + ";", nextIndex);
        }
        setBeginLocation(n, nextIndex);
        if (n.hasChildNodes()) {
            NodeList childs = n.getChildNodes();
            for (int i = 0; i < childs.getLength(); i++) {
                nextIndex = determineLocation(childs.item(i), nextIndex);
            }
        }
        if (n.getNodeType() == Node.ELEMENT_NODE) {
            nextIndex += 2 + n.getNodeName().length() + 1; // </nodename>
        } else if (n.getNodeType() == Node.DOCUMENT_TYPE_NODE) {
            Node nextSibling = n.getNextSibling();
            if (nextSibling.getNodeType() == Node.ELEMENT_NODE) {
                nextIndex = xmlString.indexOf("<" + nextSibling.getNodeName(), nextIndex) - 1;
            } else if (nextSibling.getNodeType() == Node.COMMENT_NODE) {
                nextIndex = xmlString.indexOf("<!--", nextIndex);
            } else {
                nextIndex = xmlString.indexOf(">", nextIndex);
            }
        } else if (n.getNodeType() == Node.COMMENT_NODE) {
            nextIndex += 4 + 3; // <!-- and -->
            nextIndex += n.getNodeValue().length();
        } else if (n.getNodeType() == Node.TEXT_NODE) {
            String te = unexpandEntities(n, n.getNodeValue());
            nextIndex += te.length();
        } else if (n.getNodeType() == Node.CDATA_SECTION_NODE) {
            nextIndex += "<![CDATA[".length() + n.getNodeValue().length() + "]]>".length();
        } else if (n.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) {
            ProcessingInstruction pi = (ProcessingInstruction)n;
            nextIndex += "<?".length() + pi.getTarget().length() + "?>".length() + pi.getData().length();
        }
        setEndLocation(n, nextIndex - 1);
        return nextIndex;
    }

    private String unexpandEntities(Node n, String te) {
        String result = te;
        DocumentType doctype = n.getOwnerDocument().getDoctype();
        // implicit entities
        result = result.replaceAll(Matcher.quoteReplacement("&"), "&amp;");
        result = result.replaceAll(Matcher.quoteReplacement("<"), "&lt;");
        result = result.replaceAll(Matcher.quoteReplacement(">"), "&gt;");
        result = result.replaceAll(Matcher.quoteReplacement("\""), "&quot;");
        result = result.replaceAll(Matcher.quoteReplacement("'"), "&apos;");

        if (doctype != null) {
            NamedNodeMap entities = doctype.getEntities();
            for (int i = 0; i < entities.getLength(); i++) {
                Node item = entities.item(i);
                result = result.replaceAll(Matcher.quoteReplacement(item.getFirstChild().getNodeValue()), "&" + item.getNodeName() + ";");
            }

        }
        return result;
    }
    private void setBeginLocation(Node n, int index) {
        if (n != null) {
            n.setUserData(XmlNode.BEGIN_LINE, toLine(index), null);
            n.setUserData(XmlNode.BEGIN_COLUMN, toColumn(index), null);
        }
    }
    private void setEndLocation(Node n, int index) {
        if (n != null) {
            n.setUserData(XmlNode.END_LINE, toLine(index), null);
            n.setUserData(XmlNode.END_COLUMN, toColumn(index), null);
        }
    }
    
    private void calculateLinesMap() {
        lines = new TreeMap<Integer, Integer>();
        int index = -1;
        int count = StringUtils.countMatches(xmlString, "\n");
        for (int line = 1; line <= count; line++) {
            lines.put(line, index + 1);
            index = xmlString.indexOf("\n", index + 1);
        }
        lines.put(count + 1, index + 1);
    }
    
    private int toLine(int index) {
        int line = 1;
        for (Map.Entry<Integer, Integer> e : lines.entrySet()) {
            line = e.getKey();
            if (e.getValue() > index) {
                line--;
                break;
            }
        }
        return line;
    }
    private int toColumn(int index) {
        int line = toLine(index);
        int column = index - lines.get(line);
        return column + 1;
    }

}
