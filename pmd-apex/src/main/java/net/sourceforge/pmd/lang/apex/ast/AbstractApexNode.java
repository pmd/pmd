/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.lang.ast.SourceCodePositioner;

import apex.jorje.data.Location;
import apex.jorje.data.Locations;
import apex.jorje.semantic.ast.AstNode;
import apex.jorje.semantic.exception.UnexpectedCodePathException;

public abstract class AbstractApexNode<T extends AstNode> extends AbstractApexNodeBase implements ApexNode<T> {

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

    public String getLocation() {
        if (hasRealLoc()) {
            return String.valueOf(node.getLoc());
        } else {
            return "no location";
        }
    }
}
