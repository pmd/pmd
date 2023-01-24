/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import static net.sourceforge.pmd.properties.PropertyFactory.stringListProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import net.sourceforge.pmd.PMDVersion;
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
import net.sourceforge.pmd.lang.java.ast.AbstractJavaNode;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.java.ast.Annotatable;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.JavaNameOccurrence;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

public class UnusedPrivateFieldRule extends AbstractJavaRule {

    private static final Logger LOG = Logger.getLogger(UnusedPrivateFieldRule.class.getName());

    private static final PropertyDescriptor<List<String>> IGNORED_ANNOTATIONS_DESCRIPTOR
            = stringListProperty("ignoredAnnotations")
            .desc("deprecated! Fully qualified names of the annotation types that should be ignored by this rule. "
                + "This property has been deprecated since PMD 6.50.0 and will be completely ignored.")
            .defaultValue(new ArrayList<String>())
            .build();

    private static boolean warnedAboutDeprecatedIgnoredAnnotationsProperty = false;
    private static final PropertyDescriptor<List<String>> IGNORED_FIELD_NAMES =
                PropertyFactory.stringListProperty("ignoredFieldNames")
                    .defaultValues("serialVersionUID", "serialPersistentFields")
                    .desc("Field Names that are ignored from the unused check")
                    .build();

    private static final PropertyDescriptor<List<String>> REPORT_FOR_ANNOTATIONS_DESCRIPTOR
            = stringListProperty("reportForAnnotations")
            .desc("Fully qualified names of the annotation types that should be reported anyway. If an unused field "
                    + "has any of these annotations, then it is reported. If it has any other annotation, then "
                    + "it is still considered to used and is not reported.")
            .defaultValue(new ArrayList<String>())
            .build();

    public UnusedPrivateFieldRule() {
        definePropertyDescriptor(IGNORED_ANNOTATIONS_DESCRIPTOR);
        definePropertyDescriptor(IGNORED_FIELD_NAMES);
        definePropertyDescriptor(REPORT_FOR_ANNOTATIONS_DESCRIPTOR);
    }

    @Override
    public String dysfunctionReason() {
        List<PropertyDescriptor<?>> overriddenPropertyDescriptors = getOverriddenPropertyDescriptors();
        if (!warnedAboutDeprecatedIgnoredAnnotationsProperty && overriddenPropertyDescriptors.contains(IGNORED_ANNOTATIONS_DESCRIPTOR)) {
            LOG.warning("The property '" + IGNORED_ANNOTATIONS_DESCRIPTOR.name() + "' for rule '"
                    + this.getName() + "' is deprecated. The value is being ignored and the property will "
                    + "be removed in PMD " + PMDVersion.getNextMajorRelease());
            warnedAboutDeprecatedIgnoredAnnotationsProperty = true;
        }
        return super.dysfunctionReason();
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (hasAnyAnnotation(node)) {
            return super.visit(node, data);
        }

        Map<VariableNameDeclaration, List<NameOccurrence>> vars = node.getScope()
                                                                      .getDeclarations(VariableNameDeclaration.class);
        for (Map.Entry<VariableNameDeclaration, List<NameOccurrence>> entry : vars.entrySet()) {
            VariableNameDeclaration decl = entry.getKey();
            AccessNode accessNodeParent = decl.getAccessNodeParent();
            if (!accessNodeParent.isPrivate()
                || isOK(decl.getImage())
                || hasAnyAnnotation((Annotatable) accessNodeParent)) {
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

    private boolean hasAnyAnnotation(Annotatable node) {
        List<ASTAnnotation> annotations = node.getDeclaredAnnotations();
        for (String reportAnnotation : getProperty(REPORT_FOR_ANNOTATIONS_DESCRIPTOR)) {
            for (ASTAnnotation annotation : annotations) {
                if (TypeTestUtil.isA(reportAnnotation, annotation)) {
                    return false;
                }
            }
        }
        return !node.getDeclaredAnnotations().isEmpty();
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
        return getProperty(IGNORED_FIELD_NAMES).contains(image);
    }
}
