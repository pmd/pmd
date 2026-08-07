/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.design;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTParameter;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.internal.AbstractCounterCheckRule;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextRange2d;

/**
 * This rule detects an abnormally long parameter list.
 */
public class ExcessiveParameterListRule extends AbstractCounterCheckRule<ASTMethod> {


    public ExcessiveParameterListRule() {
        super(ASTMethod.class);
    }

    @Override
    protected int defaultReportLevel() {
        return 4;
    }

    @Override
    protected FileLocation getReportLocation(ASTMethod node) {
        ApexNode<?> lastParameter = node.children(ASTParameter.class).last();
        if (lastParameter == null) {
            return node.getReportLocation();
        }
        return FileLocation.range(node.getTextDocument().getFileId(),
            TextRange2d.range2d(
                node.getReportLocation().getStartPos(),
                lastParameter.getReportLocation().getEndPos()));
    }

    @Override
    protected int getMetric(ASTMethod node) {
        return node.getArity();
    }
}
