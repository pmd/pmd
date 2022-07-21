/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.Node;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.SourceCodePositioner;

/**
 * @deprecated Use {@link ApexNode}
 */
@Deprecated
@InternalApi
public abstract class AbstractApexNode<T extends Node> extends AbstractApexNodeBase implements ApexNode<T> {

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

        // Location loc = node.getLoc();
        // calculateLineNumbers(positioner, loc.getStartIndex(), loc.getEndIndex());
        // TODO(b/239648780)
    }

    protected void handleSourceCode(String source) {
        // default implementation does nothing
    }

    @Deprecated
    @InternalApi
    @Override
    public T getNode() {
        return node;
    }

    @Override
    public boolean hasRealLoc() {
        try {
            // Location loc = node.getLoc();
            // return loc != null && Locations.isReal(loc);
            // TODO(b/239648780)
            return false;
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            // bug in apex-jorje? happens on some ReferenceExpression nodes
            return false;
        }
    }

    public String getLocation() {
        if (hasRealLoc()) {
            // return String.valueOf(node.getLoc());
            // TODO(b/239648780)
            return null;
        } else {
            return "no location";
        }
    }

    // private TypeInfo getDefiningTypeOrNull() {
    //     try {
    //         return node.getDefiningType();
    //     } catch (UnsupportedOperationException e) {
    //         return null;
    //     }
    // }
    // TODO(b/239648780)

    @Override
    public String getDefiningType() {
        // TypeInfo definingType = getDefiningTypeOrNull();
        // if (definingType != null) {
        //     return definingType.getApexName();
        // }
        // TODO(b/239648780)
        return null;
    }

    @Override
    public String getNamespace() {
        // TypeInfo definingType = getDefiningTypeOrNull();
        // if (definingType != null) {
        //     return definingType.getNamespace().toString();
        // }
        // TODO(b/239648780)
        return null;
    }
}
