/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.antlr.v4.runtime.Token;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.AstVisitor;
import net.sourceforge.pmd.lang.ast.impl.antlr4.BaseAntlrTerminalNode;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;

public final class KotlinTerminalNode extends BaseAntlrTerminalNode<KotlinNode> implements KotlinNode {


    KotlinTerminalNode(Token token) {
        super(token);
    }


    @Override
    public @NonNull String getText() {
        String constImage = KotlinParser.DICO.getConstantImageOfToken(getFirstAntlrToken());
        return constImage == null ? getFirstAntlrToken().getText() : constImage;
    }


    @Override
    public String getXPathNodeName() {
        return KotlinParser.DICO.getXPathNameOfToken(getFirstAntlrToken().getType());
    }


    @Override
    public <P, R> R acceptVisitor(AstVisitor<? super P, ? extends R> visitor, P data) {
        if (visitor instanceof KotlinVisitor) {
            return ((KotlinVisitor<? super P, ? extends R>) visitor).visitKotlinNode(this, data);
        }
        return super.acceptVisitor(visitor, data);
    }

    /**
     * Filters out XPath attributes with a {@code null} value, keeping the PMD
     * Designer clean by only showing attributes that have meaningful values.
     */
    @Override
    public Iterator<Attribute> getXPathAttributesIterator() {
        List<Attribute> attrs = new ArrayList<>();
        Iterator<Attribute> base = super.getXPathAttributesIterator();
        while (base.hasNext()) {
            Attribute attr = base.next();
            if (attr.getValue() != null) {
                attrs.add(attr);
            }
        }
        return attrs.iterator();
    }

}
