/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import java.util.Iterator;

import org.antlr.v4.runtime.Token;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.AstVisitor;
import net.sourceforge.pmd.lang.ast.impl.antlr4.BaseAntlrTerminalNode;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;
import net.sourceforge.pmd.lang.rule.xpath.NoAttribute;
import net.sourceforge.pmd.util.IteratorUtil;

public final class KotlinTerminalNode extends BaseAntlrTerminalNode<KotlinNode> implements KotlinNode {


    KotlinTerminalNode(Token token) {
        super(token);
    }


    @Override
    public @NonNull String getText() {
        String constImage = KotlinParser.DICO.getConstantImageOfToken(getFirstAntlrToken());
        return constImage == null ? getFirstAntlrToken().getText() : constImage;
    }

    /**
     * @deprecated Since 7.25.0. Don't use getImage() or hasImageEqualTo()! See #4787.
     */
    @Override
    @NoAttribute
    @Deprecated
    public String getImage() {
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

    @Override
    public String getXPathNodeName() {
        return KotlinParser.DICO.getXPathNameOfToken(getFirstAntlrToken().getType());
    }


    @Override
    public Iterator<Attribute> getXPathAttributesIterator() {
        return IteratorUtil.filter(super.getXPathAttributesIterator(), attr -> attr.getValue() != null);
    }

    @Override
    public <P, R> R acceptVisitor(AstVisitor<? super P, ? extends R> visitor, P data) {
        if (visitor instanceof KotlinVisitor) {
            return ((KotlinVisitor<? super P, ? extends R>) visitor).visitKotlinNode(this, data);
        }
        return super.acceptVisitor(visitor, data);
    }
}
