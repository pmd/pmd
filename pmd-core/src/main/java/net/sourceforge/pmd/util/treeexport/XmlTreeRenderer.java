/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.treeexport;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;

/**
 * Renders a tree to XML. The resulting document is as close as possible
 * to the representation PMD uses to run XPath queries on nodes. This
 * allows the same XPath queries to match, in theory (it would depend
 * on the XPath engine used I believe).
 */
@Experimental
public final class XmlTreeRenderer implements TreeRenderer {

    // See https://www.w3.org/TR/2008/REC-xml-20081126/#NT-Name
    private static final String XML_START_CHAR = "[:A-Z_a-z\\xC0-\\xD6\\xD8-\\xF6\\xF8-\\x{2FF}\\x{370}-\\x{37D}\\x{37F}-\\x{1FFF}\\x{200C}-\\x{200D}\\x{2070}-\\x{218F}\\x{2C00}-\\x{2FEF}\\x{3001}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFFD}\\x{10000}-\\x{EFFFF}]";
    private static final String XML_CHAR = "[" + XML_START_CHAR + ".\\-0-9\\xB7\\x{0300}-\\x{036F}\\x{203F}-\\x{2040}]";
    private static final Pattern XML_NAME = Pattern.compile(XML_START_CHAR + XML_CHAR + "*");


    private final XmlRenderingConfig strategy;
    private final char attrDelim;

    /*
        TODO it's unclear to me how the strong typing of XPath 2.0 would
         impact XPath queries run on the output XML. PMD maps attributes
         to typed values, the XML only has untyped strings.

         OTOH users should expect differences, and it's even documented
         on this class.

     */


    /**
     * Creates a new XML renderer.
     *
     * @param strategy Strategy to parameterize the output of this instance
     */
    public XmlTreeRenderer(XmlRenderingConfig strategy) {
        this.strategy = strategy;
        this.attrDelim = strategy.singleQuoteAttributes ? '\'' : '"';
    }

    /**
     * Creates a new XML renderer with a default configuration.
     */
    public XmlTreeRenderer() {
        this(new XmlRenderingConfig());
    }

    /**
     * {@inheritDoc}
     *
     * <p>Each node of the AST has a corresponding XML element, whose
     * name and attributes are the one the node presents in XPath queries.
     *
     * @param node {@inheritDoc}
     * @param out  {@inheritDoc}
     *
     * @throws IllegalArgumentException If some node has attributes or
     *                                  a name that is not a valid XML name
     */
    @Override
    public void renderSubtree(Node node, Appendable out) throws IOException {
        if (strategy.renderProlog) {
            renderProlog(out);
        }
        renderSubtree(0, node, out);
        out.append(strategy.lineSeparator);
    }

    private void renderProlog(Appendable out) throws IOException {
        out.append("<?xml version=").append(attrDelim).append("1.0").append(attrDelim)
           .append(" encoding=").append(attrDelim).append("UTF-8").append(attrDelim)
           .append(" ?>").append(strategy.lineSeparator);
    }

    private void renderSubtree(int depth, Node node, Appendable out) throws IOException {

        String eltName = node.getXPathNodeName();

        checkValidName(eltName);


        indent(depth, out).append('<').append(eltName);

        Map<String, String> attributes = strategy.getXmlAttributes(node);

        for (String attrName : attributes.keySet()) {
            appendAttribute(out, attrName, attributes.get(attrName));
        }

        if (node.getNumChildren() == 0) {
            out.append(" />");
            return;
        }


        out.append(">");

        for (int i = 0; i < node.getNumChildren(); i++) {
            out.append(strategy.lineSeparator);
            renderSubtree(depth + 1, node.getChild(i), out);
        }

        out.append(strategy.lineSeparator);

        indent(depth, out).append("</").append(eltName).append('>');
    }

    private void appendAttribute(Appendable out, String name, String value) throws IOException {
        checkValidName(name);

        out.append(' ')
           .append(name)
           .append('=')
           .append(attrDelim)
           .append(escapeXmlAttribute(value, strategy.singleQuoteAttributes))
            .append(attrDelim);
    }

    private void checkValidName(String name) {
        if (!isValidXmlName(name) || isReservedXmlName(name)) {
            throw new IllegalArgumentException(name + " is not a valid XML name");
        }
    }

    private Appendable indent(int depth, Appendable out) throws IOException {
        while (depth-- > 0) {
            out.append(strategy.indentString);
        }
        return out;
    }

    private static String escapeXmlText(String xml) {
        return xml.replaceAll("<", "&lt;")
                  .replaceAll("&", "&amp;");

    }

    private static String escapeXmlAttribute(String xml, boolean isSingleQuoted) {

        return isSingleQuoted ? escapeXmlText(xml).replaceAll("'", "&apos;")
                              : escapeXmlText(xml).replaceAll("\"", "&quot;");
    }

    private static boolean isValidXmlName(String xml) {
        return XML_NAME.matcher(xml).matches();
    }

    private static boolean isReservedXmlName(String xml) {
        return StringUtils.startsWithIgnoreCase(xml, "xml");
    }

    /**
     * A strategy to parameterize an {@link XmlTreeRenderer}.
     */
    @Experimental
    public static class XmlRenderingConfig {

        private String indentString = "    ";
        private String lineSeparator = System.lineSeparator();
        private boolean singleQuoteAttributes = true;
        private boolean renderProlog = true;

        private Map<String, String> getXmlAttributes(Node node) {
            Map<String, String> attrs = new TreeMap<>();
            Iterator<Attribute> iter = node.getXPathAttributesIterator();
            while (iter.hasNext()) {
                Attribute next = iter.next();
                if (takeAttribute(node, next)) {
                    try {

                        attrs.put(next.getName(), next.getStringValue());
                    } catch (Exception e) {
                        handleAttributeFetchException(next, e);
                    }
                }
            }
            return attrs;
        }

        /**
         * Handle an exception that occurred while fetching the value
         * of an attribute. The default does nothing, it's meant to be
         * overridden if you want to handle it.
         *
         * @param attr Attribute for which the fetch failed
         * @param e    Exception that occurred
         */
        protected void handleAttributeFetchException(Attribute attr, Exception e) {
            // to be overridden
        }

        /**
         * Returns true if the attribute should be included in the element
         * corresponding to the given node. Subclasses can override this
         * method to filter out some attributes.
         *
         * @param node      Node owning the attribute
         * @param attribute Attribute to test
         */
        protected boolean takeAttribute(Node node, Attribute attribute) {
            return true;
        }

        /**
         * Sets the string that should be used to separate lines. The
         * default is the platform-specific line separator.
         *
         * @throws NullPointerException If the argument is null
         */
        public XmlRenderingConfig lineSeparator(String lineSeparator) {
            this.lineSeparator = Objects.requireNonNull(lineSeparator);
            return this;
        }

        /**
         * Sets the delimiters use for attribute values. The default is
         * to use single quotes.
         *
         * @param useSingleQuote True for single quotes, false for double quotes
         */
        public XmlRenderingConfig singleQuoteAttributes(boolean useSingleQuote) {
            this.singleQuoteAttributes = useSingleQuote;
            return this;
        }

        /**
         * Sets whether to render an XML prolog or not. The default is
         * true.
         */
        public XmlRenderingConfig renderProlog(boolean renderProlog) {
            this.renderProlog = renderProlog;
            return this;
        }

        /**
         * Sets the string that should be used to indent children elements.
         * The default is four spaces.
         *
         * @throws NullPointerException If the argument is null
         */
        public XmlRenderingConfig indentWith(String indentString) {
            this.indentString = Objects.requireNonNull(indentString);
            return this;
        }

    }

}
