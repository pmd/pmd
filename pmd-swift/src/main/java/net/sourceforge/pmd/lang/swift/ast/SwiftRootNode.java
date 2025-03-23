/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import org.antlr.v4.runtime.ParserRuleContext;

import net.sourceforge.pmd.lang.ast.AstInfo;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.swift.ast.SwiftParser.SwTopLevel;

// package private base class
abstract class SwiftRootNode extends SwiftInnerNode implements RootNode {

    private AstInfo<SwTopLevel> astInfo;

    SwiftRootNode(ParserRuleContext parent, int invokingStateNumber) {
        super(parent, invokingStateNumber);
    }

    @Override
    public AstInfo<SwTopLevel> getAstInfo() {
        return astInfo;
    }

    SwTopLevel makeAstInfo(ParserTask task) {
        SwTopLevel me = (SwTopLevel) this;
        this.astInfo = new AstInfo<>(task, me);
        return me;
    }

}
