/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.design;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTResultType;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class UseSingletonRule extends AbstractJavaRule {

    private boolean isOK;
    private int methodCount;

    @Override
    public Object visit(ASTCompilationUnit cu, Object data) {
        methodCount = 0;
        isOK = false;
        Object result = cu.childrenAccept(this, data);
        if (!isOK && methodCount > 0) {
            addViolation(data, cu);
        }

        return result;
    }

    @Override
    public Object visit(ASTFieldDeclaration decl, Object data) {
        if (!decl.isStatic()) {
            isOK = true;
        }
        return data;
    }

    @Override
    public Object visit(ASTConstructorDeclaration decl, Object data) {
        if (decl.isPrivate()) {
            isOK = true;
        }
        return data;
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration decl, Object data) {
        if (decl.isAbstract()) {
            isOK = true;
        }
        return super.visit(decl, data);
    }

    @Override
    public Object visit(ASTMethodDeclaration decl, Object data) {
        if ( !  decl.isPrivate()) {
		methodCount++;
	}
        if (!isOK && !decl.isStatic()) {
            isOK = true;
        }

        // TODO use symbol table
        if (decl.getMethodName().equals("suite")) {
            ASTResultType res = decl.getResultType();
            ASTClassOrInterfaceType c = res.getFirstDescendantOfType(ASTClassOrInterfaceType.class);
            if (c != null && c.hasImageEqualTo("Test")) {
                isOK = true;
            }
        }

        return data;
    }

}
