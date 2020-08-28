/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.rule.internal.AbstractJavaCounterCheckRule;

/**
 * ExcessiveImports attempts to count all unique imports a class contains. This
 * rule will count a "import com.something.*;" as a single import. This is a
 * unique situation and I'd like to create an audit type rule that captures
 * those.
 *
 * @author aglover
 * @since Feb 21, 2003
 */
public class ExcessiveImportsRule extends AbstractJavaCounterCheckRule<ASTCompilationUnit> {

    public ExcessiveImportsRule() {
        super(ASTCompilationUnit.class);
    }

    @Override
    protected int defaultReportLevel() {
        return 30;
    }

    @Override
    protected boolean isViolation(ASTCompilationUnit node, int reportLevel) {
        return node.findChildrenOfType(ASTImportDeclaration.class).size() >= reportLevel;
    }
}
