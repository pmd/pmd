package net.sourceforge.pmd.util.dfagraph;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;

/**
 * Created by IntelliJ IDEA.
 * User: tom
 * Date: Sep 20, 2004
 * Time: 5:44:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class DFAGraphRule extends AbstractRule {
    public DFAGraphRule() {
        super.setUsesDFA();
        super.setUsesSymbolTable();
    }
    private SourceFile src;
    public Object visit(ASTCompilationUnit acu, Object data) {
        this.src = new SourceFile(((RuleContext)data).getSourceCodeFilename());
        return super.visit(acu, data);
    }
    public Object visit(ASTMethodDeclaration node, Object data) {
        super.visit(node, data);
        new DFAGrapher(node, src);
        return data;
    }
}

