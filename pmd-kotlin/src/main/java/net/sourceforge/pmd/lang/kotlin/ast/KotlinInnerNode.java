/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.ParserRuleContext;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.ast.AstVisitor;
import net.sourceforge.pmd.lang.ast.impl.antlr4.BaseAntlrInnerNode;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;
import net.sourceforge.pmd.lang.rule.xpath.NoAttribute;

abstract class KotlinInnerNode extends BaseAntlrInnerNode<KotlinNode> implements KotlinNode {

    KotlinInnerNode(ParserRuleContext parent, int invokingStateNumber) {
        super(parent, invokingStateNumber);
    }

    @Override
    public <P, R> R acceptVisitor(AstVisitor<? super P, ? extends R> visitor, P data) {
        if (visitor instanceof KotlinVisitor) {
            // some of the generated antlr nodes have no accept method...
            return ((KotlinVisitor<? super P, ? extends R>) visitor).visitKotlinNode(this, data);
        }
        return visitor.visitNode(this, data);
    }

    @Override // override to make visible in package
    protected PmdAsAntlrInnerNode<KotlinNode> asAntlrNode() {
        return super.asAntlrNode();
    }

    @Override
    public String getXPathNodeName() {
        return KotlinParser.DICO.getXPathNameOfRule(getRuleIndex());
    }

    /**
     * @deprecated Since 7.25.0. Don't use getImage() or hasImageEqualTo()! See #4787.
     */
    @Override
    @NoAttribute
    @Deprecated
    public @Nullable String getImage() {
        return null;
    }

    /**
     * @deprecated Since 7.25.0. Don't use getImage() or hasImageEqualTo()! See #4787.
     */
    @Override
    @Deprecated
    public boolean hasImageEqualTo(String image) {
        return super.hasImageEqualTo(image);
    }

    /**
     * Returns the corresponding attributes class for this node.
     * The returned type is already cast to have the correct type.
     *
     * <p>Usage example:
     * <pre>{@code
     * String id = classDecl.attributes(KtClassDeclarationAttributes.class).getIdentifier();
     * }</pre>
     *
     * @throws IllegalArgumentException if the given attribute view type doesn't match this node's type.
     *
     * @since 7.25.0
     * @experimental See {@link AttributeView}.
     */
    @Experimental
    public <A extends AttributeView<?>> @Nullable A attributes(Class<A> type) {
        AttributeView<?> view = AttributeView.create(this);
        if (view == null) {
            return null;
        }

        if (!type.isInstance(view)) {
            throw new IllegalArgumentException("Expected type " + view.getClass().getName() + " but got " + type.getName());
        }
        return type.cast(view);
    }

    /**
     * Returns the attributes on the node and additionally the attributes of
     * the corresponding attribute view, if there is one. Duplicate names are
     * suppressed and null-valued attributes (e.g. type attributes with no
     * resolved type) are omitted.
     *
     * @see #attributes(Class)
     */
    @Override
    public Iterator<Attribute> getXPathAttributesIterator() {
        List<Attribute> result = new ArrayList<>();
        Set<String> names = new HashSet<>();
        addAttributes(super.getXPathAttributesIterator(), result, names);

        AttributeView<?> attributeView = AttributeView.create(this);
        if (attributeView != null) {
            addAttributes(attributeView.getXPathAttributesIterator(), result, names);
        }
        return result.iterator();
    }

    private static void addAttributes(Iterator<Attribute> source, List<Attribute> result, Set<String> names) {
        while (source.hasNext()) {
            Attribute attr = source.next();
            // Dedup by name; skip null-valued attributes. This implements deliberate
            // optional-attribute absence: the type-aware views (@TypeName, @ReturnTypeName,
            // @AnnotationFqNames, @TypeInfoAvailable, ...) return null when the value does not
            // apply, so the attribute is absent from XPath rather than present-with-null.
            // Rules distinguish "unknown" (root has no @TypeInfoAvailable), "unresolved"
            // (pmd-kotlin:hasUnresolvedReference()), and "genuinely none" — see the Kotlin docs.
            if (attr.getValue() != null && names.add(attr.getName())) {
                result.add(attr);
            }
        }
    }
}
