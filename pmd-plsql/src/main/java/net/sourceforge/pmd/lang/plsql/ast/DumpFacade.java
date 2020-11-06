/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @deprecated This class will be removed with PMD 7. The rule designer is a better way to inspect nodes.
 */
@Deprecated
public class DumpFacade extends PLSQLParserVisitorAdapter {

    private PrintWriter writer;
    private boolean recurse;

    public void initializeWith(Writer writer, String prefix, boolean recurse, PLSQLNode node) {
        this.writer = writer instanceof PrintWriter ? (PrintWriter) writer : new PrintWriter(writer);
        this.recurse = recurse;
        this.visit(node, prefix);
        try {
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException("Problem flushing PrintWriter.", e);
        }
    }

    @Override
    public Object visit(PLSQLNode node, Object data) {
        dump(node, (String) data);
        if (recurse) {
            return super.visit(node, data + " ");
        } else {
            return data;
        }
    }

    private void dump(PLSQLNode node, String prefix) {
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
        if (node instanceof ASTBooleanLiteral) {
            image = node.getImage();
        } else if (node instanceof ASTPrimaryPrefix) {
            String result = null;
            /*
             * if (primaryPrefix.usesSuperModifier()) { result = "super"; } else
             * if (primaryPrefix.usesThisModifier()) { result = "this"; }
             */
            if (image != null) {
                result += "." + image;
            }
            image = result;
        } else if (node instanceof ASTPrimarySuffix) {
            ASTPrimarySuffix primarySuffix = (ASTPrimarySuffix) node;
            if (primarySuffix.isArrayDereference()) {
                if (image == null) {
                    image = "[";
                } else {
                    image = "[" + image;
                }
            }
        }

        // Extras
        List<String> extras = new ArrayList<>();

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
