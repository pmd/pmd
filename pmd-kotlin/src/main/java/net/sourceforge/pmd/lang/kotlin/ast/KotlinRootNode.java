/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import org.antlr.v4.runtime.ParserRuleContext;

import net.sourceforge.pmd.lang.ast.AstInfo;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtFile;

// package private base class
abstract class KotlinRootNode extends KotlinInnerNode implements RootNode {

    private AstInfo<KtFile> astInfo;

    KotlinRootNode(ParserRuleContext parent, int invokingStateNumber) {
        super(parent, invokingStateNumber);
    }

    @Override
    public AstInfo<KtFile> getAstInfo() {
        return astInfo;
    }

    KtFile makeAstInfo(ParserTask task) {
        KtFile me = (KtFile) this;
        this.astInfo = new AstInfo<>(task, me);
        return me;
    }

}
