/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.java.javadoc.JavadocTag;
import net.sourceforge.pmd.util.document.FileLocation;

public class JavadocElement extends Comment {

    private final JavadocTag tag;
    private final FileLocation reportLoc;

    public JavadocElement(JavaccToken t, int theBeginLine, int theEndLine, int theBeginColumn, int theEndColumn, JavadocTag theTag) {
        super(t);
        this.tag = theTag;
        this.reportLoc = FileLocation.range("TODO", theBeginLine, theBeginColumn, theEndLine, theEndColumn);
    }

    public JavadocTag tag() {
        return tag;
    }

    @Override
    public FileLocation getReportLocation() {
        return reportLoc;
    }

    @Override
    public String getXPathNodeName() {
        return tag.label + " : " + tag.description;
    }
}
