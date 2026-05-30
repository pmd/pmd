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

    public @Nullable String getTypeName() {
        return KotlinTypeMapper.getTypeName(this);
    }

    public @Nullable String getReturnTypeName() {
        return KotlinTypeMapper.getReturnTypeName(this);
    }

    /**
     * Returns the explicit modifier keywords of this declaration node as a
     * space-separated string (e.g. {@code "override suspend"}), or {@code null}
     * if this node has no modifier keywords. Annotations inside the modifier list
     * are excluded. Exposed as XPath attribute {@code @Modifiers}.
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
            // KtAnnotation children are skipped
        }
        return sb.length() > 0 ? sb.toString() : null;
    }

    /**
     * Returns the imported type's fully-qualified name for {@code ImportHeader} nodes
     * (e.g. {@code "com.example.Foo"} for {@code import com.example.Foo}).
     * Returns {@code null} for all other node types.
     *
     * <p>This is used by PMD's XPath rule engine as {@code {0}} in violation messages,
     * so that the unresolved type name appears in the message text.
     *
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
     * Returns the text of the first {@code SimpleIdentifier} direct child,
     * or {@code null} if none is present.
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

    private static @Nullable String firstModifierKeyword(KotlinNode node) {
        if (node instanceof KotlinTerminalNode) {
            return ((KotlinTerminalNode) node).getText();
        }
        for (int i = 0; i < node.getNumChildren(); i++) {
            String found = firstModifierKeyword(node.getChild(i));
            if (found != null) {
                return found;
            }
        }
        return null;
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
     * the corresponding attribute view, if there is one. Attributes with null
     * values are filtered out and duplicate names are suppressed.
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
            if (attr.getValue() != null && names.add(attr.getName())) {
                result.add(attr);
            }
        }
    }
}
