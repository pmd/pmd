/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.rule;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ecmascript.ast.EcmascriptParserVisitor;
import net.sourceforge.pmd.lang.rule.AbstractRule;


public abstract class AbstractEcmascriptRule extends AbstractRule
        implements EcmascriptParserVisitor {

    public AbstractEcmascriptRule() {
    }

    @Override
    public void apply(Node target, RuleContext ctx) {
        target.acceptVisitor(this, ctx);
    }

}
