package net.sourceforge.pmd.util.dfagraph;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;

import java.util.List;

public class DFAGraphRule extends AbstractRule {

    private SourceFile src;
    private List methods;
    private List constructors;

    public DFAGraphRule() {
        super.setUsesDFA();
        super.setUsesSymbolTable();
    }

    public List getMethods() {
        return this.methods;
    }
    public List getConstructors() {
        return this.constructors;
    }
    public SourceFile getSrc() {
        return this.src;
    }

    public Object visit(ASTCompilationUnit acu, Object data) {
        this.src = new SourceFile(((RuleContext)data).getSourceCodeFilename());
        methods = acu.findChildrenOfType(ASTMethodDeclaration.class);
        constructors = acu.findChildrenOfType(ASTMethodDeclaration.class);
        return data;
    }
}

