/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import org.antlr.v4.runtime.ParserRuleContext;

/**
 * Base class for the parser rule contexts, use {@code contextSuperClass} option
 * in the antlr grammar.
 */
public abstract class AntlrParseTreeBase extends ParserRuleContext {

    protected AntlrParseTreeBase() {
    }

    protected AntlrParseTreeBase(ParserRuleContext parent, int invokingStateNumber) {
        super(parent, invokingStateNumber);
    }
}
