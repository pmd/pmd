/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.Arrays;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.AstVisitor;
import net.sourceforge.pmd.lang.ast.FileAnalysisException;
import net.sourceforge.pmd.lang.ast.impl.AbstractNode;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextRegion;

import com.google.summit.ast.Node;
import com.google.summit.ast.expression.LiteralExpression;
import org.checkerframework.checker.nullness.qual.NonNull;

abstract class AbstractApexNode extends AbstractApexNodeBase implements ApexNode<Void> {

    /**
     * {@link AbstractApexNode} wrapper around a single {@link Node}.
     */
    abstract static class Single<T extends Node> extends AbstractApexNode {

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
     */
    abstract static class Many<T extends Node> extends AbstractApexNode {

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

    /**
     * {@link AbstractApexNode} that doesn't directly wrap a {@link Node}.
     */
    abstract static class Empty extends AbstractApexNode {

        protected Empty() {
            super(Void.class);
        }

        @Override
        void calculateLineNumbers(SourceCodePositioner positioner) {
            // no operation
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

    protected AbstractApexNode() {
        this(Void.class);
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

    abstract void calculateLineNumbers(SourceCodePositioner positioner);

    @Override
    public @NonNull TextRegion getTextRegion() {
        if (region == null) {
            if (!hasRealLoc()) {
                AbstractApexNode<?> parent = (AbstractApexNode<?>) getParent();
                if (parent == null) {
                    throw new FileAnalysisException("Unable to determine location of " + this);
                }
                region = parent.getTextRegion();
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

    @Override
    public abstract boolean hasRealLoc();

    public abstract String getLocation();

    @Override
    public String getDefiningType() {
        BaseApexClass<?> baseNode = this instanceof BaseApexClass ? (BaseApexClass<?>) this : getFirstParentOfType(BaseApexClass.class);
        if (baseNode != null) {
            return baseNode.getQualifiedName();
        }
        return null;
    }

    @Override
    public String getNamespace() {
        // TypeInfo definingType = getDefiningTypeOrNull();
        // if (definingType != null) {
        //     return definingType.getNamespace().toString();
        // }
        // TODO(b/243905954)
        return null;
    }

    /** Returns the string value of the {@link LiteralExpression}. */
    protected static String literalToString(LiteralExpression expr) {
        if (expr instanceof LiteralExpression.StringVal) {
            return ((LiteralExpression.StringVal) expr).getValue();
        } else if (expr instanceof LiteralExpression.NullVal) {
            return "";
        }
        return expr.asCodeString();
    }
    
    private TypeInfo getDefiningTypeOrNull() {
        try {
            return node.getDefiningType();
        } catch (UnsupportedOperationException e) {
            return null;
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
    public static String caseNormalizedTypeIfPrimitive(String name) {
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
