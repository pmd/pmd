/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrNameDictionary;
import net.sourceforge.pmd.lang.swift.ast.SwiftParser.TopLevelContext;

public final class SwiftFileNode extends SwiftInnerNode implements RootNode {

    SwiftFileNode(TopLevelContext root) {
        asAntlrNode().addChild(root.asAntlrNode());
    }

    @Override
    protected int getRuleIndex() {
        return AntlrNameDictionary.ROOT_RULE_IDX;
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
        return visitor.visitChildren(asAntlrNode());
    }
}
