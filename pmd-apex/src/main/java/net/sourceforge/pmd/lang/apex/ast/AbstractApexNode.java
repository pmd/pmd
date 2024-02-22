/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.Arrays;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.AstVisitor;
import net.sourceforge.pmd.lang.ast.FileAnalysisException;
import net.sourceforge.pmd.lang.ast.impl.AbstractNode;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextPos2d;
import net.sourceforge.pmd.lang.document.TextRegion;

import com.google.summit.ast.Node;
import com.google.summit.ast.SourceLocation;
import com.google.summit.ast.expression.LiteralExpression;

abstract class AbstractApexNode extends AbstractNode<AbstractApexNode, ApexNode<?>> implements ApexNode<Void> {

    private TextRegion region;

    /**
     * {@link AbstractApexNode} wrapper around a single {@link Node}.
     */
    abstract static class Single<T extends Node> extends AbstractApexNode {

        protected final T node;

        protected Single(T node) {
            this.node = node;
        }

        @Override
        protected void calculateTextRegion(TextDocument sourceCode) {
            SourceLocation loc = node.getSourceLocation();
            if (loc.isUnknown()) {
                return;
            }
            // Column+1 because Summit columns are 0-based and PMD are 1-based
            setRegion(TextRegion.fromBothOffsets(
                sourceCode.offsetAtLineColumn(TextPos2d.pos2d(loc.getStartLine(), loc.getStartColumn() + 1)),
                sourceCode.offsetAtLineColumn(TextPos2d.pos2d(loc.getEndLine(), loc.getEndColumn() + 1))
            ));
        }

        @Override
        public boolean hasRealLoc() {
            return !node.getSourceLocation().isUnknown();
        }
    }

    /**
     * {@link AbstractApexNode} wrapper around a {@link List} of {@link Node}s.
     */
    abstract static class Many<T extends Node> extends AbstractApexNode {

        protected final List<T> nodes;

        protected Many(List<T> nodes) {
            this.nodes = nodes;
        }

        @Override
        protected void calculateTextRegion(TextDocument sourceCode) {
            // from all nodes, use the earliest location and the latest location.
            // this assumes, that these nodes form a contiguous code snippet.

            SourceLocation union = SourceLocation.Companion.getUNKNOWN();
            for (Node node : nodes) {
                SourceLocation loc = node.getSourceLocation();
                if (!loc.isUnknown()) {
                    if (union.getStartLine() == null
                            || loc.getStartLine() < union.getStartLine()
                            || loc.getStartLine().equals(union.getStartLine()) && loc.getStartColumn() < union.getStartColumn()) {
                        union = new SourceLocation(loc.getStartLine(), loc.getStartColumn(), union.getEndLine(), union.getEndColumn());
                    }
                    if (union.getEndLine() == null
                            || loc.getEndLine() > union.getEndLine()
                            || loc.getEndLine().equals(union.getEndLine()) && loc.getEndColumn() > union.getEndColumn()) {
                        union = new SourceLocation(union.getStartLine(), union.getStartColumn(), loc.getEndLine(), loc.getEndColumn());
                    }
                }
            }

            if (!union.isUnknown()) {
                // Column+1 because Summit columns are 0-based and PMD are 1-based
                setRegion(TextRegion.fromBothOffsets(
                    sourceCode.offsetAtLineColumn(TextPos2d.pos2d(union.getStartLine(), union.getStartColumn() + 1)),
                    sourceCode.offsetAtLineColumn(TextPos2d.pos2d(union.getEndLine(), union.getEndColumn() + 1))
                ));
            }
        }

        @Override
        public boolean hasRealLoc() {
            return !nodes.isEmpty() && nodes.stream().noneMatch(n -> n.getSourceLocation().isUnknown());
        }
    }

    /**
     * {@link AbstractApexNode} that doesn't directly wrap a {@link Node}.
     */
    abstract static class Empty extends AbstractApexNode {

        @Override
        protected void calculateTextRegion(TextDocument sourceCode) {
            // no location
        }

        @Override
        public boolean hasRealLoc() {
            return false;
        }
    }

    // overridden to make them visible
    @Override
    protected void addChild(AbstractApexNode child, int index) {
        super.addChild(child, index);
    }

    @Override
    protected void insertChild(AbstractApexNode child, int index) {
        super.insertChild(child, index);
    }

    @Override
    protected void setChild(AbstractApexNode child, int index) {
        super.setChild(child, index);
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

    abstract void calculateTextRegion(TextDocument sourceCode);

    @Override
    public @NonNull TextRegion getTextRegion() {
        if (region == null) {
            if (!hasRealLoc()) {
                AbstractApexNode parent = (AbstractApexNode) getParent();
                if (parent == null) {
                    throw new FileAnalysisException("Unable to determine location of " + this);
                }
                region = parent.getTextRegion();
            } else {
                throw new FileAnalysisException("Unable to determine location of " + this);
            }
        }
        return region;
    }

    @Override
    public final String getXPathNodeName() {
        return this.getClass().getSimpleName().replaceFirst("^AST", "");
    }

    protected void setRegion(TextRegion region) {
        this.region = region;
    }
    
    @Override
    public abstract boolean hasRealLoc();

    @Override
    public String getDefiningType() {
        BaseApexClass<?> baseNode = this instanceof BaseApexClass ? (BaseApexClass<?>) this : ancestors(BaseApexClass.class).first();
        if (baseNode != null) {
            return baseNode.getQualifiedName().toString();
        }
        return null;
    }

    /** Returns the string value of the {@link LiteralExpression}. */
    static String literalToString(LiteralExpression expr) {
        if (expr instanceof LiteralExpression.StringVal) {
            return ((LiteralExpression.StringVal) expr).getValue();
        } else if (expr instanceof LiteralExpression.NullVal) {
            return "";
        }
        return expr.asCodeString();
    }

    /**
      * Normalizes case of primitive type names.
      *
      * All other strings are returned unchanged.
      *
      * See: https://developer.salesforce.com/docs/atlas.en-us.apexcode.meta/apexcode/langCon_apex_primitives.htm
      */
    static String caseNormalizedTypeIfPrimitive(String name) {
        String floor = caseNormalizedTypeNames.floor(name);
        return name.equalsIgnoreCase(floor) ? floor : name;
    }

    private static NavigableSet<String> caseNormalizedTypeNames =
        new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

    static {
        caseNormalizedTypeNames.addAll(Arrays.asList(
            "Blob",
            "Boolean",
            "Currency",
            "Date",
            "Datetime",
            "Decimal",
            "Double",
            "Id",
            "Integer",
            "Long",
            "Object",
            "String",
            "Time"
        ));
    }
}
