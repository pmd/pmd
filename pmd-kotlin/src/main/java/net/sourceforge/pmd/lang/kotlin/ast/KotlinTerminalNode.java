/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import net.sourceforge.pmd.lang.ast.impl.antlr4.BaseAntlrTerminalNode;
import org.antlr.v4.runtime.Token;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class KotlinTerminalNode extends BaseAntlrTerminalNode<KotlinNode> implements KotlinNode {

    KotlinTerminalNode(Token token) {
        super(token);
    }

    @Override
    public @NonNull String getText() {
        String constImage = KotlinParser.DICO.getConstantImageOfToken(getFirstAntlrToken());
        return constImage == null ? getFirstAntlrToken().getText()
                                  : constImage;
    }

    @Override
    public String getXPathNodeName() {
        return KotlinParser.DICO.getXPathNameOfToken(getFirstAntlrToken().getType());
    }

}
