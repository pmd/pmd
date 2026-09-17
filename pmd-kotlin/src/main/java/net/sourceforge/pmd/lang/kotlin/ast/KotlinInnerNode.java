/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.antlr.v4.runtime.ParserRuleContext;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.ast.AstVisitor;
import net.sourceforge.pmd.lang.ast.impl.antlr4.BaseAntlrInnerNode;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;
import net.sourceforge.pmd.lang.rule.xpath.NoAttribute;

abstract class KotlinInnerNode extends BaseAntlrInnerNode<KotlinNode> implements KotlinNode {

    /**
     * Standard {@link net.sourceforge.pmd.lang.ast.Node} position attributes that every
     * {@link AttributeView} redeclares by delegating to the wrapped node. These are expected
     * to collide with the base node's own copies and are exempted from the duplicate-name
     * check in {@link #addAttributes(Iterator, List, Map)}, as long as the values agree.
     * Self-enforcing: if a new source starts redeclaring some other attribute name, the
     * check throws on the unexpected collision, so this list can't silently go stale.
     */
    private static final Set<String> STANDARD_ATTRIBUTE_NAMES = new HashSet<>(Arrays.asList(
            "BeginLine", "BeginColumn", "EndLine", "EndColumn"
    ));

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
     * the corresponding attribute view, if there is one. Null-valued attributes
     * (e.g. type attributes with no resolved type) are omitted; this implements
     * deliberate optional-attribute absence. Attribute views always redeclare
     * the standard position attributes ({@link #STANDARD_ATTRIBUTE_NAMES}), so
     * those names are allowed to collide, provided the value is the same on
     * both sides. Any other collision, or a standard-attribute collision with
     * a different value, indicates an attribute-view bug (two sources
     * disagreeing on an attribute), so it throws instead of being silently
     * dropped.
     *
     * @see #attributes(Class)
     */
    @Override
    public Iterator<Attribute> getXPathAttributesIterator() {
        List<Attribute> result = new ArrayList<>();
        Map<String, Attribute> byName = new HashMap<>();
        addAttributes(super.getXPathAttributesIterator(), result, byName);

        AttributeView<?> attributeView = AttributeView.create(this);
        if (attributeView != null) {
            addAttributes(attributeView.getXPathAttributesIterator(), result, byName);
        }
        return result.iterator();
    }

    static void addAttributes(Iterator<Attribute> source, List<Attribute> result, Map<String, Attribute> byName) {
        while (source.hasNext()) {
            Attribute attr = source.next();
            // Skip null-valued attributes. This implements deliberate optional-attribute
            // absence: the type-aware views (@TypeName, @ReturnTypeName, @AnnotationFqNames,
            // ...) return null when the value does not apply, so the attribute is absent
            // from XPath rather than present-with-null. Rules distinguish "unresolved"
            // (pmd-kotlin:hasUnresolvedReference()) from "genuinely none" — see the Kotlin docs.
            if (attr.getValue() == null) {
                continue;
            }
            Attribute existing = byName.putIfAbsent(attr.getName(), attr);
            if (existing != null) {
                if (!STANDARD_ATTRIBUTE_NAMES.contains(attr.getName())
                        || !Objects.equals(existing.getValue(), attr.getValue())) {
                    throw new IllegalStateException(
                            "Duplicate XPath attribute name @" + attr.getName()
                                    + " defined by more than one source on node " + attr.getParent());
                }
                // Expected collision on a standard position attribute with the same value; keep the first copy.
                continue;
            }
            result.add(attr);
        }
    }
}
