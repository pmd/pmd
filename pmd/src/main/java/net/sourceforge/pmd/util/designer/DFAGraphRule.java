package net.sourceforge.pmd.util.designer;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class DFAGraphRule extends AbstractJavaRule {

    private List<ASTMethodDeclaration> methods;
    private List<ASTConstructorDeclaration> constructors;

    public DFAGraphRule() {
	super.setUsesDFA();
    }

    public List<ASTMethodDeclaration> getMethods() {
	return this.methods;
    }

    public List<ASTConstructorDeclaration> getConstructors() {
	return this.constructors;
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
	methods.add(node);
	return super.visit(node, data);
    }

    @Override
    public Object visit(ASTConstructorDeclaration node, Object data) {
	constructors.add(node);
	return super.visit(node, data);
    }

    @Override
    public Object visit(ASTCompilationUnit acu, Object data) {
	methods = new ArrayList<ASTMethodDeclaration>();
	constructors = new ArrayList<ASTConstructorDeclaration>();
	return super.visit(acu, data);
    }
}
