/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBody;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumBody;
import net.sourceforge.pmd.lang.java.ast.ASTEnumConstant;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.rule.AbstractLombokAwareRule;
import net.sourceforge.pmd.lang.java.symboltable.JavaNameOccurrence;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.properties.StringMultiProperty;

public class UnusedPrivateFieldRule extends AbstractLombokAwareRule {

    private static final StringMultiProperty IGNORED_ANNOTATIONS_DESCRIPTOR
            = StringMultiProperty.named("ignoredAnnotations")
                                 .desc("Fully qualified names of the annotation types that should be ignored by this rule")
                                 .defaultValues("java.lang.Deprecated", "javafx.fxml.FXML")
                                 .build();


    public UnusedPrivateFieldRule() {
        definePropertyDescriptor(IGNORED_ANNOTATIONS_DESCRIPTOR);
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
                    || hasLombokAnnotation(accessNodeParent) || hasNeglectAnnotation(decl)) {
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
        List<JavaNode> nodes = new ArrayList<>();
        nodes.addAll(classOrInterfaceBodyDeclarations);
        nodes.addAll(enumConstants);

        for (JavaNode node : nodes) {
            List<ASTPrimarySuffix> primarySuffixes = new ArrayList<>();
            node.findDescendantsOfType(ASTPrimarySuffix.class, primarySuffixes, true);
            for (ASTPrimarySuffix primarySuffix : primarySuffixes) {
                if (decl.getImage().equals(primarySuffix.getImage())) {
                    return true; // No violation
                }
            }

            List<ASTPrimaryPrefix> primaryPrefixes = new ArrayList<>();
            node.findDescendantsOfType(ASTPrimaryPrefix.class, primaryPrefixes, true);
            for (ASTPrimaryPrefix primaryPrefix : primaryPrefixes) {
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

    /**
     * Checks whether the given node is annotated with annotation in the set.
     * The node should be variable Name declaration.
     *
     * @param node
     *            the node to check
     * @return <code>true</code> if the annotation has been found
     */
    protected boolean hasNeglectAnnotation(VariableNameDeclaration node) {
        Node parent = node.getAccessNodeParent().jjtGetParent();
        List<ASTAnnotation> annotations = parent.findChildrenOfType(ASTAnnotation.class);
        for (ASTAnnotation annotation : annotations) {
            TypeNode n = (TypeNode) annotation.jjtGetChild(0);
            for (String annotationName : getProperty(IGNORED_ANNOTATIONS_DESCRIPTOR)) {
                if (TypeHelper.isA(n, annotationName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
