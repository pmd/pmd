/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrBaseRule;

public abstract class AbstractSwiftRule extends AntlrBaseRule {

    protected AbstractSwiftRule() {
        // inheritance constructor
    }

    @Override
    public abstract ParseTreeVisitor<Void> buildVisitor(RuleContext ruleCtx);
}
