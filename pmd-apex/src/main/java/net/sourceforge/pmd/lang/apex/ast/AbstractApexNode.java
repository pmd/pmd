/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.AstVisitor;
import net.sourceforge.pmd.lang.ast.impl.AbstractNode;
import net.sourceforge.pmd.util.document.FileLocation;
import net.sourceforge.pmd.util.document.TextDocument;
import net.sourceforge.pmd.util.document.TextRegion;

import apex.jorje.data.Location;
import apex.jorje.data.Locations;
import apex.jorje.semantic.ast.AstNode;
import apex.jorje.semantic.exception.UnexpectedCodePathException;

abstract class AbstractApexNode<T extends AstNode> extends AbstractNode<AbstractApexNode<?>, ApexNode<?>> implements ApexNode<T> {

    protected final T node;
    private TextRegion region;

    protected AbstractApexNode(T node) {
        this.node = node;
    }

    // overridden to make them visible
    @Override
    protected void addChild(AbstractApexNode<?> child, int index) {
        super.addChild(child, index);
    }

    @Override
    protected void insertChild(AbstractApexNode<?> child, int index) {
        super.insertChild(child, index);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <P, R> R acceptVisitor(AstVisitor<? super P, ? extends R> visitor, P data) {
        if (visitor instanceof ApexVisitor) {
            return this.acceptApexVisitor((ApexVisitor<? super P, ? extends R>) visitor, data);
        }
        return visitor.cannotVisit(this, data);
    }

    protected abstract <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data);

    @Override
    public @NonNull ASTApexFile getRoot() {
        return getParent().getRoot();
    }

    @Override
    public FileLocation getReportLocation() {
        return getTextDocument().toLocation(getRegion());
    }

    protected @NonNull TextRegion getRegion() {
        if (region == null) {
            if (!hasRealLoc()) {
                AbstractApexNode<?> parent = (AbstractApexNode<?>) getParent();
                if (parent == null) {
                    throw new RuntimeException("Unable to determine location of " + this);
                }
                region = parent.getRegion();
            } else {
                Location loc = node.getLoc();
                region = TextRegion.fromBothOffsets(loc.getStartIndex(), loc.getEndIndex());
            }
        }
        return region;
    }

    @Override
    public final String getXPathNodeName() {
        return this.getClass().getSimpleName().replaceFirst("^AST", "");
    }

    /**
     * Note: in this routine, the node has not been added to its parents,
     * but its children have been populated (except comments).
     */
    void closeNode(TextDocument positioner) {
        // do nothing
    }

    protected void setRegion(TextRegion region) {
        this.region = region;
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
            Location loc = node.getLoc();
            return loc != null && Locations.isReal(loc);
        } catch (UnexpectedCodePathException e) {
            return false;
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            // bug in apex-jorje? happens on some ReferenceExpression nodes
            return false;
        }
    }

    @Override
    public String getDefiningType() {
        if (node.getDefiningType() != null) {
            return node.getDefiningType().getApexName();
        }
        return null;
    }

    @Override
    public String getNamespace() {
        if (node.getDefiningType() != null) {
            return node.getDefiningType().getNamespace().toString();
        }
        return null;
    }
}
