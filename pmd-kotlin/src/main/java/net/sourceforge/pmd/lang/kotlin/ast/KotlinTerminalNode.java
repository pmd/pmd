/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import org.antlr.v4.runtime.Token;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.AstVisitor;
import net.sourceforge.pmd.lang.ast.impl.antlr4.BaseAntlrTerminalNode;

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

}
