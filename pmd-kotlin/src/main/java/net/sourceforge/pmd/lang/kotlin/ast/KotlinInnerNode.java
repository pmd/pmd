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
import net.sourceforge.pmd.lang.kotlin.ast.internal.KotlinAstUtil;
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
     * Returns the explicit modifier keywords of this declaration node as a
     * space-separated string (e.g. {@code "override suspend"}), or {@code null}
     * if this node has no modifier keywords. Annotations inside the modifier list
     * are excluded. Exposed as XPath attribute {@code @Modifiers}.
     *
     * <p>Available on all inner nodes as a convenience; nodes with a dedicated
     * {@link AttributeView} implementing {@link HasModifiers} may override.
     */
    public @Nullable String getModifiers() {
        KotlinParser.KtModifiers mods = findModifiersNode();
        if (mods == null) {
            return null;
        }
        return buildModifiersString(mods);
    }

    private KotlinParser.@Nullable KtModifiers findModifiersNode() {
        for (int i = 0; i < getNumChildren(); i++) {
            KotlinNode child = getChild(i);
            if (child instanceof KotlinParser.KtModifiers) {
                return (KotlinParser.KtModifiers) child;
            }
        }
        return null;
    }

    private static @Nullable String buildModifiersString(KotlinParser.KtModifiers mods) {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < mods.getNumChildren(); j++) {
            KotlinNode mod = mods.getChild(j);
            if (mod instanceof KotlinParser.KtModifier) {
                String kw = firstModifierKeyword(mod);
                if (kw != null) {
                    if (sb.length() > 0) {
                        sb.append(' ');
                    }
                    sb.append(kw);
                }
            }
        }
        return sb.length() > 0 ? sb.toString() : null;
    }

    private static @Nullable String firstModifierKeyword(KotlinNode node) {
        if (node instanceof KotlinTerminalNode) {
            return ((KotlinTerminalNode) node).getText();
        }
        for (int i = 0; i < node.getNumChildren(); i++) {
            KotlinNode child = node.getChild(i);
            if (child instanceof KotlinTerminalNode) {
                return ((KotlinTerminalNode) child).getText();
            }
        }
        return null;
    }

    /**
     * Returns the text of the first {@code SimpleIdentifier} direct child,
     * or {@code null} if none is present. Exposed as XPath attribute {@code @Identifier}.
     *
     * <p>Available on all inner nodes so that XPath rules can reference {@code @Identifier}
     * on any node (e.g. {@code CatchBlock/@Identifier} for the caught variable name).
     */
    public @Nullable String getIdentifier() {
        for (int i = 0; i < getNumChildren(); i++) {
            KotlinNode child = getChild(i);
            if (child instanceof KotlinParser.KtSimpleIdentifier) {
                KotlinParser.KtSimpleIdentifier si = (KotlinParser.KtSimpleIdentifier) child;
                if (si.getNumChildren() > 0) {
                    KotlinNode token = si.getChild(0);
                    if (token instanceof KotlinTerminalNode) {
                        return ((KotlinTerminalNode) token).getText();
                    }
                }
            }
        }
        return null;
    }

    /**
     * @deprecated Since 7.25.0. Don't use getImage() or hasImageEqualTo()! See #4787.
     */
    @Override
    @NoAttribute
    @Deprecated
    public @Nullable String getImage() {
        if (getRuleIndex() == KotlinParser.RULE_importHeader) {
            return buildImportFqn();
        }
        return null;
    }

    private @Nullable String buildImportFqn() {
        for (int i = 0; i < getNumChildren(); i++) {
            KotlinNode child = getChild(i);
            if (child instanceof KotlinParser.KtIdentifier) {
                return KotlinAstUtil.dottedTextOf(child);
            }
        }
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
