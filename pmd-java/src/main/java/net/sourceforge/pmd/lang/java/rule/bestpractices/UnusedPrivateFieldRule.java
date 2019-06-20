/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBody;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumBody;
import net.sourceforge.pmd.lang.java.ast.ASTEnumConstant;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaNode;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.java.ast.Annotatable;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractLombokAwareRule;
import net.sourceforge.pmd.lang.java.symboltable.JavaNameOccurrence;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

public class UnusedPrivateFieldRule extends AbstractLombokAwareRule {

    @Override
    protected Collection<String> defaultSuppressionAnnotations() {
        Collection<String> defaultValues = new ArrayList<>();
        defaultValues.addAll(super.defaultSuppressionAnnotations());
        defaultValues.add("java.lang.Deprecated");
        defaultValues.add("javafx.fxml.FXML");
        defaultValues.add("lombok.experimental.Delegate");
        return defaultValues;
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        boolean classHasLombok = hasLombokAnnotation(node);

        Map<VariableNameDeclaration, List<NameOccurrence>> vars = node.getScope()
                .getDeclarations(VariableNameDeclaration.class);
        for (Map.Entry<VariableNameDeclaration, List<NameOccurrence>> entry : vars.entrySet()) {
            VariableNameDeclaration decl = entry.getKey();
            AccessNode accessNodeParent = decl.getAccessNodeParent();
            if (!accessNodeParent.isPrivate() || isOK(decl.getImage()) || classHasLombok
                || hasIgnoredAnnotation((Annotatable) accessNodeParent)) {
                continue;
            }
            if (!actuallyUsed(entry.getValue())) {
                if (!usedInOuterClass(node, decl) && !usedInOuterEnum(node, decl)) {
                    addViolation(data, decl.getNode(), decl.getImage());
                }
            }
        }
        return super.visit(node, data);
    }

    private boolean usedInOuterEnum(ASTClassOrInterfaceDeclaration node, NameDeclaration decl) {
        List<ASTEnumDeclaration> outerEnums = node.getParentsOfType(ASTEnumDeclaration.class);
        for (ASTEnumDeclaration outerEnum : outerEnums) {
            ASTEnumBody enumBody = outerEnum.getFirstChildOfType(ASTEnumBody.class);
            if (usedInOuter(decl, enumBody)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Find out whether the variable is used in an outer class
     */
    private boolean usedInOuterClass(ASTClassOrInterfaceDeclaration node, NameDeclaration decl) {
        List<ASTClassOrInterfaceDeclaration> outerClasses = node.getParentsOfType(ASTClassOrInterfaceDeclaration.class);
        for (ASTClassOrInterfaceDeclaration outerClass : outerClasses) {
            ASTClassOrInterfaceBody classOrInterfaceBody = outerClass
                    .getFirstChildOfType(ASTClassOrInterfaceBody.class);
            if (usedInOuter(decl, classOrInterfaceBody)) {
                return true;
            }
        }
        return false;
    }

    private boolean usedInOuter(NameDeclaration decl, JavaNode body) {
        List<ASTClassOrInterfaceBodyDeclaration> classOrInterfaceBodyDeclarations = body
                .findChildrenOfType(ASTClassOrInterfaceBodyDeclaration.class);
        List<ASTEnumConstant> enumConstants = body.findChildrenOfType(ASTEnumConstant.class);
        List<AbstractJavaNode> nodes = new ArrayList<>();
        nodes.addAll(classOrInterfaceBodyDeclarations);
        nodes.addAll(enumConstants);

        for (AbstractJavaNode node : nodes) {
            for (ASTPrimarySuffix primarySuffix : node.findDescendantsOfType(ASTPrimarySuffix.class, true)) {
                if (decl.getImage().equals(primarySuffix.getImage())) {
                    return true; // No violation
                }
            }

            for (ASTPrimaryPrefix primaryPrefix : node.findDescendantsOfType(ASTPrimaryPrefix.class, true)) {
                ASTName name = primaryPrefix.getFirstDescendantOfType(ASTName.class);

                if (name != null) {
                    for (String id : name.getImage().split("\\.")) {
                        if (id.equals(decl.getImage())) {
                            return true; // No violation
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean actuallyUsed(List<NameOccurrence> usages) {
        for (NameOccurrence nameOccurrence : usages) {
            JavaNameOccurrence jNameOccurrence = (JavaNameOccurrence) nameOccurrence;
            if (!jNameOccurrence.isOnLeftHandSide()) {
                return true;
            }
        }

        return false;
    }

    private boolean isOK(String image) {
        return "serialVersionUID".equals(image) || "serialPersistentFields".equals(image) || "IDENT".equals(image);
    }
}
