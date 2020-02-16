/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.lang.ast.AbstractNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.SourceCodePositioner;

/**
 * @deprecated Use {@link ApexNode}
 */
@Deprecated
public abstract class AbstractApexNodeBase extends AbstractNode {

    public AbstractApexNodeBase(Class<?> klass) {
        super(klass.hashCode());
    }

    /* package */ void calculateLineNumbers(SourceCodePositioner positioner, int startOffset, int endOffset) {
        // end column will be interpreted as inclusive, while endOffset/endIndex
        // is exclusive
        endOffset -= 1;

        this.beginLine = positioner.lineNumberFromOffset(startOffset);
        this.beginColumn = positioner.columnFromOffset(this.beginLine, startOffset);
        this.endLine = positioner.lineNumberFromOffset(endOffset);
        this.endColumn = positioner.columnFromOffset(this.endLine, endOffset);

        if (this.endColumn < 0) {
            this.endColumn = 0;
        }
    }

    /**
     * Accept the visitor. *
     */
    public abstract Object jjtAccept(ApexParserVisitor visitor, Object data);

    /**
     * Accept the visitor. *
     */
    public Object childrenAccept(ApexParserVisitor visitor, Object data) {
        if (children != null) {
            for (int i = 0; i < children.length; ++i) {
                // we know that the children here are all ApexNodes
                AbstractApexNodeBase apexNode = (AbstractApexNodeBase) children[i];
                apexNode.jjtAccept(visitor, data);
            }
        }
        return data;
    }

    @Override
    public int getBeginLine() {
        if (this.beginLine > 0) {
            return this.beginLine;
        }
        Node parent = getParent();
        if (parent != null) {
            return parent.getBeginLine();
        }
        throw new RuntimeException("Unable to determine beginning line of Node.");
    }

    @Override
    public int getBeginColumn() {
        if (this.beginColumn > 0) {
            return this.beginColumn;
        }
        Node parent = getParent();
        if (parent != null) {
            return parent.getBeginColumn();
        }
        throw new RuntimeException("Unable to determine beginning column of Node.");
    }

    @Override
    public int getEndLine() {
        if (this.endLine > 0) {
            return this.endLine;
        }
        Node parent = getParent();
        if (parent != null) {
            return parent.getEndLine();
        }
        throw new RuntimeException("Unable to determine ending line of Node.");
    }

    @Override
    public int getEndColumn() {
        if (this.endColumn > 0) {
            return this.endColumn;
        }
        Node parent = getParent();
        if (parent != null) {
            return parent.getEndColumn();
        }
        throw new RuntimeException("Unable to determine ending column of Node.");
    }

    @Override
    public final String getXPathNodeName() {
        return this.getClass().getSimpleName().replaceFirst("^AST", "");
    }
}
