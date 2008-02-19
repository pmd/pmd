/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.junit;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;

public class TestClassWithoutTestCases extends AbstractJUnitRule {

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isAbstract() || node.isInterface() || node.isNested()) {
            return data;
        }

        List<ASTMethodDeclaration> m = node.findChildrenOfType(ASTMethodDeclaration.class);
        boolean testsFound = false;

        if (m != null) {
        	for (Iterator<ASTMethodDeclaration> it = m.iterator(); it.hasNext() && !testsFound;) {
        		ASTMethodDeclaration md = it.next();
        		if (!isInInnerClassOrInterface(md)
        				&& isJUnitMethod(md, data))
                        	testsFound = true;
            }
        }

        if (!testsFound) {
        	addViolation(data, node);
        }

        return data;
    }

    private boolean isInInnerClassOrInterface(ASTMethodDeclaration md) {
        ASTClassOrInterfaceDeclaration p = md.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class);
        return p != null && p.isNested();
    }
}
