/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.unusedcode;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBody;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.JavaNameOccurrence;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

public class UnusedPrivateFieldRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        Map<VariableNameDeclaration, List<NameOccurrence>> vars = node.getScope().getDeclarations(VariableNameDeclaration.class);
        for (Map.Entry<VariableNameDeclaration, List<NameOccurrence>> entry: vars.entrySet()) {
            VariableNameDeclaration decl = entry.getKey();
            AccessNode accessNodeParent = decl.getAccessNodeParent();
            if (!accessNodeParent .isPrivate() || isOK(decl.getImage())) {
                continue;
            }
            if (!actuallyUsed(entry.getValue())) {
            	if (!usedInOuterClass(node, decl)) {
            		addViolation(data, decl.getNode(), decl.getImage());
            	}
            }
        }
        return super.visit(node, data);
    }

    /**
     * Find out whether the variable is used in an outer class
     */
	private boolean usedInOuterClass(ASTClassOrInterfaceDeclaration node,
			NameDeclaration decl) {
		List<ASTClassOrInterfaceDeclaration> outerClasses = node.getParentsOfType(ASTClassOrInterfaceDeclaration.class);
		for (ASTClassOrInterfaceDeclaration outerClass : outerClasses) {
			ASTClassOrInterfaceBody classOrInterfaceBody = outerClass.getFirstChildOfType(ASTClassOrInterfaceBody.class);

			List<ASTClassOrInterfaceBodyDeclaration> classOrInterfaceBodyDeclarations = classOrInterfaceBody.findChildrenOfType(ASTClassOrInterfaceBodyDeclaration.class);

			for (ASTClassOrInterfaceBodyDeclaration classOrInterfaceBodyDeclaration : classOrInterfaceBodyDeclarations) {
				for (int i = 0; i < classOrInterfaceBodyDeclaration.jjtGetNumChildren(); i++) {
					if (classOrInterfaceBodyDeclaration.jjtGetChild(i) instanceof ASTClassOrInterfaceDeclaration) {
						continue;	//Skip other inner classes
					}

					List<ASTPrimarySuffix> primarySuffixes = classOrInterfaceBodyDeclaration.findDescendantsOfType(ASTPrimarySuffix.class);
					for (ASTPrimarySuffix primarySuffix : primarySuffixes) {
						if (decl.getImage().equals(primarySuffix.getImage())) {
							return true; //No violation
						}
					}

					List<ASTPrimaryPrefix> primaryPrefixes = classOrInterfaceBodyDeclaration.findDescendantsOfType(ASTPrimaryPrefix.class);
					for (ASTPrimaryPrefix primaryPrefix : primaryPrefixes) {
						ASTName name = primaryPrefix.getFirstDescendantOfType(ASTName.class);

						if (name != null && name.getImage().endsWith(decl.getImage())) {
							return true; //No violation
						}
					}
				}
			}

		}

		return false;
	}

    private boolean actuallyUsed(List<NameOccurrence> usages) {
        for (NameOccurrence nameOccurrence: usages) {
            JavaNameOccurrence jNameOccurrence = (JavaNameOccurrence)nameOccurrence;
            if (!jNameOccurrence.isOnLeftHandSide()) {
                return true;
            }
        }

        return false;
    }

    private boolean isOK(String image) {
        return image.equals("serialVersionUID") || image.equals("serialPersistentFields") || image.equals("IDENT");
    }
}
