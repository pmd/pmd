package net.sourceforge.pmd.util.designer;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;

import java.util.List;

public class DFAGraphRule extends AbstractRule {

    private List<ASTMethodDeclaration> methods;
    private List<ASTMethodDeclaration> constructors;

    public DFAGraphRule() {
        super.setUsesDFA();
    }

    public List<ASTMethodDeclaration> getMethods() {
        return this.methods;
    }

    public List<ASTMethodDeclaration> getConstructors() {
        return this.constructors;
    }

    public Object visit(ASTCompilationUnit acu, Object data) {
        methods = acu.findChildrenOfType(ASTMethodDeclaration.class);
        constructors = acu.findChildrenOfType(ASTMethodDeclaration.class);
        return data;
    }
}


