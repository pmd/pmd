/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.List;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.SourceCodePositioner;

import com.google.summit.ast.Node;

/**
 * @deprecated Use {@link ApexNode}
 */
@Deprecated
@InternalApi
public abstract class AbstractApexNode extends AbstractApexNodeBase implements ApexNode<Void> {

    /**
     * {@link AbstractApexNode} wrapper around a single {@link Node}.
     *
     * @deprecated Use {@link ApexNode}
     */
    @Deprecated
    @InternalApi
    public static abstract class Single<T extends Node> extends AbstractApexNode {

        protected final T node;

        protected Single(T node) {
            super(node.getClass());
            this.node = node;
        }

        @Override
        void calculateLineNumbers(SourceCodePositioner positioner) {
            setLineNumbers(node.getSourceLocation());
        }

        @Override
        public boolean hasRealLoc() {
            return !node.getSourceLocation().isUnknown();
        }

        @Override
        public String getLocation() {
            if (hasRealLoc()) {
                return String.valueOf(node.getSourceLocation());
            } else {
                return "no location";
            }
        }
    }

    /**
     * {@link AbstractApexNode} wrapper around a {@link List} of {@link Node}s.
     *
     * @deprecated Use {@link ApexNode}
     */
    @Deprecated
    @InternalApi
    public static abstract class Many<T extends Node> extends AbstractApexNode {

        protected final List<T> nodes;

        protected Many(List<T> nodes) {
            super(nodes.getClass());
            this.nodes = nodes;
        }

        @Override
        void calculateLineNumbers(SourceCodePositioner positioner) {
            for (Node node : nodes) {
                setLineNumbers(node.getSourceLocation());
            }
        }

        @Override
        public boolean hasRealLoc() {
            return false;
        }

        @Override
        public String getLocation() {
            return "no location";
        }
    }

    protected AbstractApexNode(Class<?> klass) {
        super(klass);
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

    abstract void calculateLineNumbers(SourceCodePositioner positioner);

    protected void handleSourceCode(String source) {
        // default implementation does nothing
    }

    public abstract boolean hasRealLoc();

    public abstract String getLocation();

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
