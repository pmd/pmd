/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import java.util.Iterator;

import org.antlr.v4.runtime.ParserRuleContext;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.ast.AstVisitor;
import net.sourceforge.pmd.lang.ast.impl.antlr4.BaseAntlrInnerNode;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;
import net.sourceforge.pmd.util.IteratorUtil;

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

    private AttributeView<?> attributes() {
        final AttributeView<?> result;
        if (this instanceof KotlinParser.KtImportHeader) {
            result = new KtImportHeaderAttributes((KotlinParser.KtImportHeader) this);
        } else if (this instanceof KotlinParser.KtImportAlias) {
            result = new KtImportAliasAttributes((KotlinParser.KtImportAlias) this);
        } else if (this instanceof KotlinParser.KtClassDeclaration) {
            result = new KtClassDeclarationAttributes((KotlinParser.KtClassDeclaration) this);
        } else if (this instanceof KotlinParser.KtClassParameter) {
            result = new KtClassParameterAttributes((KotlinParser.KtClassParameter) this);
        } else if (this instanceof KotlinParser.KtFunctionDeclaration) {
            result = new KtFunctionDeclarationAttributes((KotlinParser.KtFunctionDeclaration) this);
        } else if (this instanceof KotlinParser.KtVariableDeclaration) {
            result = new KtVariableDeclarationAttributes((KotlinParser.KtVariableDeclaration) this);
        } else {
            result = null;
        }
        return result;
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
    public <A extends AttributeView<?>> A attributes(Class<A> type) {
        AttributeView<?> view = attributes();
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
        AttributeView<?> attributeView = attributes();
        if (attributeView != null) {
            return IteratorUtil.concat(base, attributeView.getXPathAttributesIterator());
        }
        return base;
    }
}
