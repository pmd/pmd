/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.lang.ast.AbstractNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.SourceCodePositioner;

import apex.jorje.data.Location;
import apex.jorje.data.Locations;
import apex.jorje.semantic.ast.AstNode;
import apex.jorje.semantic.exception.UnexpectedCodePathException;

public abstract class AbstractApexNode<T extends AstNode> extends AbstractNode implements ApexNode<T> {

    protected final T node;

    public AbstractApexNode(T node) {
        super(node.getClass().hashCode());
        this.node = node;
    }

    void calculateLineNumbers(SourceCodePositioner positioner) {
        if (!hasRealLoc()) {
            return;
        }

        Location loc = node.getLoc();
        int startOffset = loc.getStartIndex();
        int endOffset = loc.getEndIndex();
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

    protected void handleSourceCode(String source) {
        // default implementation does nothing
    }

    @Override
    public int getBeginLine() {
        if (this.beginLine > 0) {
            return this.beginLine;
        }
        Node parent = jjtGetParent();
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
        Node parent = jjtGetParent();
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
        Node parent = jjtGetParent();
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
        Node parent = jjtGetParent();
        if (parent != null) {
            return parent.getEndColumn();
        }
        throw new RuntimeException("Unable to determine ending column of Node.");
    }

    /**
     * Accept the visitor. *
     */
    public Object childrenAccept(ApexParserVisitor visitor, Object data) {
        if (children != null) {
            for (int i = 0; i < children.length; ++i) {
                @SuppressWarnings("unchecked")
                // we know that the children here are all ApexNodes
                ApexNode<T> apexNode = (ApexNode<T>) children[i];
                apexNode.jjtAccept(visitor, data);
            }
        }
        return data;
    }

    public T getNode() {
        return node;
    }

    protected boolean hasRealLoc() {
        try {
            Location loc = node.getLoc();
            return loc != null && Locations.isReal(loc);
        } catch (UnexpectedCodePathException e) {
            return false;
        } catch (IndexOutOfBoundsException e) {
            // bug in apex-jorje? happens on some ReferenceExpression nodes
            return false;
        } catch (NullPointerException e) {
            // bug in apex-jorje?
            return false;
        }
    }




    @Override
    public final String getXPathNodeName() {
        new ASTMapEntryNode(null).toString();
        return this.getClass().getSimpleName().replaceFirst("^AST", "");
    }


    public String getLocation() {
        if (hasRealLoc()) {
            return String.valueOf(node.getLoc());
        } else {
            return "no location";
        }
    }
}
