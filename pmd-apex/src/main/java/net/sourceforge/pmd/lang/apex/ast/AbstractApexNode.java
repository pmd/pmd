/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.SourceCodePositioner;

import apex.jorje.data.Location;
import apex.jorje.data.Locations;
import apex.jorje.semantic.ast.AstNode;
import apex.jorje.semantic.exception.UnexpectedCodePathException;

/**
 * @deprecated Use {@link ApexNode}
 */
@Deprecated
@InternalApi
public abstract class AbstractApexNode<T extends AstNode> extends AbstractApexNodeBase implements ApexNode<T> {

    protected final T node;

    protected AbstractApexNode(T node) {
        super(node.getClass());
        this.node = node;
    }

    @Override
    public ApexNode<?> getChild(int index) {
        return (ApexNode<?>) super.getChild(index);
    }

    @Override
    public ApexNode<?> getParent() {
        return (ApexNode<?>) super.getParent();
    }

    @Override
    public Iterable<? extends ApexNode<?>> children() {
        return (Iterable<? extends ApexNode<?>>) super.children();
    }

    void calculateLineNumbers(SourceCodePositioner positioner) {
        if (!hasRealLoc()) {
            return;
        }

        Location loc = node.getLoc();
        calculateLineNumbers(positioner, loc.getStartIndex(), loc.getEndIndex());
    }

    protected void handleSourceCode(String source) {
        // default implementation does nothing
    }

    @Override
    public T getNode() {
        return node;
    }

    public boolean hasRealLoc() {
        try {
            Location loc = node.getLoc();
            return loc != null && Locations.isReal(loc);
        } catch (UnexpectedCodePathException e) {
            return false;
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            // bug in apex-jorje? happens on some ReferenceExpression nodes
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
