/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import net.sourceforge.pmd.lang.ast.AstInfo;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtTopLevel;
import org.antlr.v4.runtime.ParserRuleContext;

// package private base class
abstract class KotlinRootNode extends KotlinInnerNode implements RootNode {

    private AstInfo<KtTopLevel> astInfo;

    KotlinRootNode(ParserRuleContext parent, int invokingStateNumber) {
        super(parent, invokingStateNumber);
    }

    @Override
    public AstInfo<KtTopLevel> getAstInfo() {
        return astInfo;
    }

    KtTopLevel makeAstInfo(ParserTask task) {
        KtTopLevel me = (KtTopLevel) this;
        this.astInfo = new AstInfo<>(task, me);
        return me;
    }

}
