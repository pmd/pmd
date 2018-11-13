/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.ast.AbstractNode;
import net.sourceforge.pmd.lang.java.javadoc.JavadocTag;

public class JavadocElement extends AbstractNode {

    private final JavadocTag tag;

    public JavadocElement(int theBeginLine, int theEndLine, int theBeginColumn, int theEndColumn, JavadocTag theTag) {
        super(-1, theBeginLine, theEndLine, theBeginColumn, theEndColumn);

        tag = theTag;
    }

    public JavadocTag tag() {
        return tag;
    }




    @Override
    public String getXPathNodeName() {
        return tag.label + " : " + tag.description;
    }
}
