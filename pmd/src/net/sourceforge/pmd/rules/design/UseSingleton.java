/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTAnnotation;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceBody;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTResultType;
import net.sourceforge.pmd.ast.Node;

public class UseSingleton extends AbstractRule {

    @Override
    public Object visit(ASTClassOrInterfaceBody decl, Object data) {
        if (decl.jjtGetParent() instanceof ASTClassOrInterfaceDeclaration) {
            ASTClassOrInterfaceDeclaration parent = (ASTClassOrInterfaceDeclaration) decl.jjtGetParent();
            if (parent.isAbstract() || parent.isInterface()) {
                return super.visit(decl, data);
            }
            int i = decl.jjtGetNumChildren();
            int methodCount = 0;
            boolean isOK = false;
            while (i > 0) {
                Node p = decl.jjtGetChild(--i);
                if (p.jjtGetNumChildren() == 0) {
                    continue;
                }
                Node n = p.jjtGetChild(0);
                if (n instanceof ASTAnnotation) {
                    n = p.jjtGetChild(1);
                }
                if (n instanceof ASTFieldDeclaration) {
                    if (!((ASTFieldDeclaration) n).isStatic()) {
                        isOK = true;
                        break;
                    }
                } else if (n instanceof ASTConstructorDeclaration) {
                    if (((ASTConstructorDeclaration) n).isPrivate()) {
                        isOK = true;
                        break;
                    }
                } else if (n instanceof ASTMethodDeclaration) {
                    ASTMethodDeclaration m = (ASTMethodDeclaration) n;
                    if (!m.isPrivate()) {
                        methodCount++;
                    }
                    if (!m.isStatic()) {
                        isOK = true;
                        break;
                    }

                    // TODO use symbol table
                    if (m.getMethodName().equals("suite")) {
                        ASTResultType res = m.getResultType();
                        ASTClassOrInterfaceType c = res.getFirstChildOfType(ASTClassOrInterfaceType.class);
                        if (c != null && c.hasImageEqualTo("Test")) {
                            isOK = true;
                            break;
                        }
                    }

                }
            }
            if (!isOK && methodCount > 0) {
                addViolation(data, decl);
            }
        }
        return super.visit(decl, data);
    }

}
