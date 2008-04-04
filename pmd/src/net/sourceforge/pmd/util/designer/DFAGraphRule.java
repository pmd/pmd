package net.sourceforge.pmd.util.designer;

import java.util.List;

import net.sourceforge.pmd.lang.java.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;

public class DFAGraphRule extends AbstractJavaRule {

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


