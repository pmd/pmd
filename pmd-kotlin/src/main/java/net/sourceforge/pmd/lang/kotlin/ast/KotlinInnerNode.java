/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.antlr.v4.runtime.ParserRuleContext;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.AstVisitor;
import net.sourceforge.pmd.lang.ast.impl.antlr4.BaseAntlrInnerNode;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;

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
     */
    @Override
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
     */
    @Override
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
                return buildFqnFromIdentifier(child);
            }
        }
        return null;
    }

    private static @Nullable String buildFqnFromIdentifier(KotlinNode identifierNode) {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < identifierNode.getNumChildren(); j++) {
            KotlinNode part = identifierNode.getChild(j);
            if (part instanceof KotlinParser.KtSimpleIdentifier && part.getNumChildren() > 0) {
                KotlinNode token = part.getChild(0);
                if (token instanceof KotlinTerminalNode) {
                    if (sb.length() > 0) {
                        sb.append('.');
                    }
                    sb.append(((KotlinTerminalNode) token).getText());
                }
            }
        }
        return sb.length() > 0 ? sb.toString() : null;
    }

    /**
     * Returns the text of the first {@code SimpleIdentifier} direct child,
     * or {@code null} if none is present.
     */
    @Override
    public @Nullable String getIdentifier() {
        for (int i = 0; i < getNumChildren(); i++) {
            KotlinNode child = getChild(i);
            if (child instanceof KotlinParser.KtSimpleIdentifier) {
                // KtSimpleIdentifier wraps a single terminal token
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
     * This prevents optional attributes like {@code @TypeName} and
     * {@code @ReturnTypeName} from appearing on every node in the PMD Designer
     * when they have no meaningful value -- consistent with how {@code @Text}
     * is only present on terminal (T-prefixed) nodes.
     */
    @Override
    public Iterator<Attribute> getXPathAttributesIterator() {
        return new NonNullAttributeIterator(super.getXPathAttributesIterator());
    }

    /** Filters out XPath attributes whose value is {@code null}. */
    private static final class NonNullAttributeIterator implements Iterator<Attribute> {
        private final Iterator<Attribute> base;
        private Attribute pending;

        NonNullAttributeIterator(Iterator<Attribute> base) {
            this.base = base;
            advance();
        }

        private void advance() {
            pending = null;
            while (base.hasNext()) {
                Attribute attr = base.next();
                if (attr.getValue() != null) {
                    pending = attr;
                    break;
                }
            }
        }

        @Override
        public boolean hasNext() {
            return pending != null;
        }

        @Override
        public Attribute next() {
            if (pending == null) {
                throw new NoSuchElementException();
            }
            Attribute result = pending;
            advance();
            return result;
        }
    }
}
