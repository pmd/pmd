/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.Iterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import net.sourceforge.pmd.lang.ast.SourceCodePositioner;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.lang.ast.xpath.AttributeAxisIterator;

import apex.jorje.data.Location;
import apex.jorje.data.Locations;
import apex.jorje.semantic.ast.AstNode;
import apex.jorje.semantic.exception.UnexpectedCodePathException;

public abstract class AbstractApexNode<T extends AstNode> extends AbstractApexNodeBase implements ApexNode<T> {

    protected final T node;

    protected AbstractApexNode(T node) {
        super(node.getClass());
        this.node = node;
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

    protected boolean hasRealLoc() {
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


    @Override
    public Iterator<Attribute> getXPathAttributesIterator() {
        // Attributes of this node have precedence over same-name attributes of the underlying node
        return new AttributeAxisIterator(this);
    }

    // TODO move to IteratorUtil w/ java 8
    private static <T> Stream<T> iteratorToStream(Iterator<? extends T> it) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(it, 0), false);
    }
}
