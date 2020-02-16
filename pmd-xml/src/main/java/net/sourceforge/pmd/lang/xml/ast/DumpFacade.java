/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml.ast;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.util.StringUtil;

/**
 *
 * @deprecated This class will be removed with PMD 7. The rule designer is a better way to inspect nodes.
 */
@Deprecated
public class DumpFacade {

    private PrintWriter writer;
    private boolean recurse;

    public void initializeWith(Writer writer, String prefix, boolean recurse, XmlNode node) {
        this.writer = writer instanceof PrintWriter ? (PrintWriter) writer : new PrintWriter(writer);
        this.recurse = recurse;
        this.dump(node, prefix);
        try {
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException("Problem flushing PrintWriter.", e);
        }
    }

    public Object visit(XmlNode node, Object data) {
        dump(node, (String) data);
        if (recurse) {
            for (int i = 0; i < node.getNumChildren(); i++) {
                visit((XmlNode) node.getChild(i), data + " ");
            }
            return data;
        } else {
            return data;
        }
    }

    private void dump(XmlNode node, String prefix) {
        //
        // Dump format is generally composed of the following items...
        //

        // 1) Dump prefix
        writer.print(prefix);

        // 2) JJT Name of the Node
        writer.print(node.getXPathNodeName());

        //
        // If there are any additional details, then:
        // 1) A colon
        // 2) The Node.getImage() if it is non-empty
        // 3) Extras in parentheses
        //

        // Standard image handling
        String image = node.getImage();

        // Special image handling (e.g. Nodes with normally null images)

        image = StringUtil.escapeWhitespace(image);

        // Extras
        List<String> extras = new ArrayList<>();
        Iterator<Attribute> iterator = node.getAttributeIterator();
        while (iterator.hasNext()) {
            Attribute attribute = iterator.next();
            extras.add(attribute.getName() + "=" + StringUtil.escapeWhitespace(attribute.getValue()));
        }

        // Output image and extras
        if (image != null || !extras.isEmpty()) {
            writer.print(':');
            if (image != null) {
                writer.print(image);
            }
            for (String extra : extras) {
                writer.print('(');
                writer.print(extra);
                writer.print(')');
            }
        }

        writer.println();
    }
}
