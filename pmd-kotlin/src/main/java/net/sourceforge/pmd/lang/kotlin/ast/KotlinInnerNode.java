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
     * @deprecated Since 7.25.0. Don't use getImage()! See #4787.
     */
    @Override
    @NoAttribute
    @Deprecated
    public String getImage() {
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
     * the corresponding attribute view, if there is one.
     *
     * @see #attributes(Class)
     */
    @Override
    public Iterator<Attribute> getXPathAttributesIterator() {
        Iterator<Attribute> base = super.getXPathAttributesIterator();
        AttributeView<?> attributeView = AttributeView.create(this);
        if (attributeView == null) {
            return base;
        }
        // Note: IteratorUtil.concat cannot be used here because it eagerly calls
        // bs.hasNext() at construction time, before base is consumed. The view
        // filter predicate depends on names collected from base, so base must be
        // fully drained first.
        List<Attribute> result = new ArrayList<>();
        Set<String> names = new HashSet<>();
        while (base.hasNext()) {
            Attribute attr = base.next();
            result.add(attr);
            names.add(attr.getName());
        }
        Iterator<Attribute> viewIt = attributeView.getXPathAttributesIterator();
        while (viewIt.hasNext()) {
            Attribute attr = viewIt.next();
            if (!names.contains(attr.getName())) {
                result.add(attr);
            }
        }
        return result.iterator();
    }
}
