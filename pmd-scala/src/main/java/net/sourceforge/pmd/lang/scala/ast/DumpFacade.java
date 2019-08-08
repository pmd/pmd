/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Iterator;

import net.sourceforge.pmd.lang.ast.xpath.Attribute;

/**
 * A Dump Facade for Scala for testing purposes.
 */
public class DumpFacade extends ScalaParserVisitorAdapter {
    private PrintWriter writer;
    private boolean recurse;

    /**
     * Write the nodes of the tree to the given writer recursively.
     * 
     * @param outWriter
     *            the writer to write the tree data to
     * @param prefix
     *            a string prefix to use before each line in the writer
     * @param shouldRecurse
     *            should this recurse below the root node?
     * @param node
     *            the node to start with. Not necessarily a tree root.
     */
    public void dump(Writer outWriter, String prefix, boolean shouldRecurse, ScalaNode node) {
        this.writer = outWriter instanceof PrintWriter ? (PrintWriter) outWriter : new PrintWriter(outWriter);
        this.recurse = shouldRecurse;
        this.visit(node, prefix);
        this.writer.flush();
    }

    @Override
    public Object visit(ScalaNode node, Object data) {
        dump(node, (String) data);
        if (recurse) {
            return super.visit(node, data + " ");
        } else {
            return data;
        }
    }

    private void dump(ScalaNode node, String prefix) {
        writer.print(prefix);
        writer.print(node.getXPathNodeName());

        String image = node.getImage();

        String attrs = null;
        Iterator<Attribute> attributeIter = node.getXPathAttributesIterator();
        if (attributeIter.hasNext()) {
            StringBuilder sb = new StringBuilder();
            while (attributeIter.hasNext()) {
                Attribute attr = attributeIter.next();
                sb.append(attr.getName()).append("=").append(attr.getStringValue()).append(",");
            }
            attrs = sb.deleteCharAt(sb.length()).toString();
        }

        if (image != null) {
            writer.print(":" + image);
        }

        if (attrs != null) {
            writer.print("[");
            writer.print(attrs);
            writer.print("]");
        }

        writer.println();
    }
}
