/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.unusedcode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBody;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTEnumBody;
import net.sourceforge.pmd.lang.java.ast.ASTEnumConstant;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.JavaNameOccurrence;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

public class UnusedPrivateFieldRule extends AbstractJavaRule {

    private boolean lombokImported = false;
    private static final String LOMBOK_PACKAGE = "lombok";
    private static final Set<String> LOMBOK_ANNOTATIONS = new HashSet<>();
    static {
        LOMBOK_ANNOTATIONS.add("Data");
        LOMBOK_ANNOTATIONS.add("Getter");
        LOMBOK_ANNOTATIONS.add("Setter");
        LOMBOK_ANNOTATIONS.add("Value");
        LOMBOK_ANNOTATIONS.add("RequiredArgsConstructor");
        LOMBOK_ANNOTATIONS.add("AllArgsConstructor");
        LOMBOK_ANNOTATIONS.add("Builder");
    }

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        lombokImported = false;
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTImportDeclaration node, Object data) {
        ASTName name = node.getFirstChildOfType(ASTName.class);
        if (!lombokImported && name != null && name.getImage() != null & name.getImage().startsWith(LOMBOK_PACKAGE)) {
            lombokImported = true;
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        boolean classHasLombok = hasLombokAnnotation(node);

        Map<VariableNameDeclaration, List<NameOccurrence>> vars = node.getScope().getDeclarations(
                VariableNameDeclaration.class);
        for (Map.Entry<VariableNameDeclaration, List<NameOccurrence>> entry : vars.entrySet()) {
            VariableNameDeclaration decl = entry.getKey();
            AccessNode accessNodeParent = decl.getAccessNodeParent();
            if (!accessNodeParent.isPrivate() || isOK(decl.getImage()) || classHasLombok || hasLombokAnnotation(accessNodeParent)) {
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

    private boolean hasLombokAnnotation(Node node) {
        boolean result = false;
        Node parent = node.jjtGetParent();
        List<ASTAnnotation> annotations = parent.findChildrenOfType(ASTAnnotation.class);
        for (ASTAnnotation annotation : annotations) {
            ASTName name = annotation.getFirstDescendantOfType(ASTName.class);
            if (name != null) {
                String annotationName = name.getImage();
                if (lombokImported) {
                    if (LOMBOK_ANNOTATIONS.contains(annotationName)) {
                        result = true;
                    }
                } else {
                    if (annotationName.startsWith(LOMBOK_PACKAGE + ".")) {
                        String shortName = annotationName.substring(LOMBOK_PACKAGE.length() + 1);
                        if (LOMBOK_ANNOTATIONS.contains(shortName)) {
                            result = true;
                        }
                    }
                }
            }
        }
        return result;
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
            ASTClassOrInterfaceBody classOrInterfaceBody = outerClass.getFirstChildOfType(ASTClassOrInterfaceBody.class);
            if (usedInOuter(decl, classOrInterfaceBody)) {
                return true;
            }
        }
        return false;
    }

    private boolean usedInOuter(NameDeclaration decl, JavaNode body) {
        List<ASTClassOrInterfaceBodyDeclaration> classOrInterfaceBodyDeclarations = body
                .findChildrenOfType(ASTClassOrInterfaceBodyDeclaration.class);
        List<ASTEnumConstant> enumConstants = body
                .findChildrenOfType(ASTEnumConstant.class);
        List<JavaNode> nodes = new ArrayList<>();
        nodes.addAll(classOrInterfaceBodyDeclarations);
        nodes.addAll(enumConstants);

        for (JavaNode node : nodes) {
            List<ASTPrimarySuffix> primarySuffixes = node.findDescendantsOfType(ASTPrimarySuffix.class);
            for (ASTPrimarySuffix primarySuffix : primarySuffixes) {
                if (decl.getImage().equals(primarySuffix.getImage())) {
                    return true; // No violation
                }
            }

            List<ASTPrimaryPrefix> primaryPrefixes = node.findDescendantsOfType(ASTPrimaryPrefix.class);
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
        return image.equals("serialVersionUID") || image.equals("serialPersistentFields") || image.equals("IDENT");
    }
}
