/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import net.sourceforge.pmd.lang.ast.AstInfo;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtKotlinFile;
import org.antlr.v4.runtime.ParserRuleContext;

// package private base class
abstract class KotlinRootNode extends KotlinInnerNode implements RootNode {

    private AstInfo<KtKotlinFile> astInfo;

    KotlinRootNode(ParserRuleContext parent, int invokingStateNumber) {
        super(parent, invokingStateNumber);
    }

    @Override
    public AstInfo<KtKotlinFile> getAstInfo() {
        return astInfo;
    }

    KtKotlinFile makeAstInfo(ParserTask task) {
        KtKotlinFile me = (KtKotlinFile) this;
        this.astInfo = new AstInfo<>(task, me);
        return me;
    }
}
