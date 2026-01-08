/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import java.util.List;

import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextRegion;
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

    /**
     * @deprecated since 7.18.0. This method is not used anymore and shouldn't be implemented.
     */
    @Deprecated
    protected boolean isViolation(ASTCompilationUnit node, int reportLevel) {
        throw new UnsupportedOperationException("method is deprecated and not supported anymore.");
    }

    @Override
    protected int getMetric(ASTCompilationUnit node) {
        return node.children(ASTImportDeclaration.class).count();
    }

    @Override
    protected FileLocation getReportLocation(ASTCompilationUnit node) {
        List<ASTImportDeclaration> imports = node.children(ASTImportDeclaration.class).toList();

        // note: reportLevel must be positive (>0), so we have at least one import declaration in the list
        ASTImportDeclaration firstImport = imports.get(0);
        ASTImportDeclaration lastImport = imports.get(imports.size() - 1);

        TextRegion importsRegion = TextRegion.union(firstImport.getTextRegion(), lastImport.getTextRegion());
        return node.getTextDocument().toLocation(importsRegion);
    }
}
