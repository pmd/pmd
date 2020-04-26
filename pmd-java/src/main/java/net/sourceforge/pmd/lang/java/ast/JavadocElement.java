/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.java.javadoc.JavadocTag;

public class JavadocElement extends Comment {

    private final int beginLine;
    private final int endLine;
    private final int beginColumn;
    private final int endColumn;

    private final JavadocTag tag;

    public JavadocElement(JavaccToken t, int theBeginLine, int theEndLine, int theBeginColumn, int theEndColumn, JavadocTag theTag) {
        super(t);
        this.tag = theTag;
        this.beginLine = theBeginLine;
        this.endLine = theEndLine;
        this.beginColumn = theBeginColumn;
        this.endColumn = theEndColumn;
    }

    public JavadocTag tag() {
        return tag;
    }

    @Override
    public int getBeginLine() {
        return beginLine;
    }

    @Override
    public int getEndColumn() {
        return endColumn;
    }

    @Override
    public int getEndLine() {
        return endLine;
    }

    @Override
    public int getBeginColumn() {
        return beginColumn;
    }

    @Override
    public String getXPathNodeName() {
        return tag.label + " : " + tag.description;
    }
}
