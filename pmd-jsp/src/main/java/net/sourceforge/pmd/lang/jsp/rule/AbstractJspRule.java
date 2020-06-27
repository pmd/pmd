/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.rule;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.jsp.ast.JspNode;
import net.sourceforge.pmd.lang.jsp.ast.JspParserVisitor;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.rule.ImmutableLanguage;

public abstract class AbstractJspRule extends AbstractRule implements JspParserVisitor, ImmutableLanguage {

    @Override
    public void apply(Node target, RuleContext ctx) {
        ((JspNode) target).jjtAccept(this, ctx);
    }
}
