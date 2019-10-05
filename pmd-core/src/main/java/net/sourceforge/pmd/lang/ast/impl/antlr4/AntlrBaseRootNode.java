/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import org.antlr.v4.runtime.ParserRuleContext;

import net.sourceforge.pmd.lang.ast.RootNode;

public class AntlrBaseRootNode extends AntlrBaseNode implements RootNode {

    /**
     * Constructor required by {@link ParserRuleContext}
     */
    @SuppressWarnings("unused")
    public AntlrBaseRootNode() {
        super();
    }

    /**
     * Constructor required by {@link ParserRuleContext}
     *
     * @param parent              The parent
     * @param invokingStateNumber the invokingState defined by {@link org.antlr.v4.runtime.RuleContext} parent
     */
    @SuppressWarnings("unused")
    public AntlrBaseRootNode(final ParserRuleContext parent, final int invokingStateNumber) {
        super(parent, invokingStateNumber);
    }
}
