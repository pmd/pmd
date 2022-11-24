/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBody;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

/**
 * @deprecated Internal API
 */
@Deprecated
@InternalApi
public abstract class AbstractJUnitRule extends AbstractJavaRule {

    protected static final String JUNIT3_CLASS_NAME = "junit.framework.TestCase";
    protected static final String JUNIT4_CLASS_NAME = "org.junit.Test";
    protected static final String JUNIT5_CLASS_NAME = "org.junit.jupiter.api.Test";
    private static final String JUNIT5_NESTED = "org.junit.jupiter.api.Nested";
    private static final Set<String> JUNIT5_TEST_ANNOTATIONS = new HashSet<>(Arrays.asList(
            JUNIT5_CLASS_NAME,
            "org.junit.jupiter.api.RepeatedTest",
            "org.junit.jupiter.api.TestFactory",
            "org.junit.jupiter.api.TestTemplate",
            "org.junit.jupiter.params.ParameterizedTest"));

    private static final String TESTNG_ANNOTATION = "org.testng.annotations.Test";

    protected boolean isJUnit3Class;
    protected boolean isJUnit4Class;
    protected boolean isJUnit5Class;

    private boolean isTestNgClass;

    @Override
    public void start(RuleContext ctx) {
        super.start(ctx);
        isTestNgClass = false;
    }

    @Override
    public Object visit(ASTImportDeclaration node, Object data) {
        if (node.getImportedName() != null && node.getImportedName().startsWith("org.testng")) {
            isTestNgClass = true;
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        boolean oldJUnit3Class = isJUnit3Class;
        boolean oldJUnit4Class = isJUnit4Class;
        boolean oldJUnit5Class = isJUnit5Class;

        try {
            analyzeJUnitClass(node);
            super.visit(node, data);
        } finally {
            isJUnit3Class = oldJUnit3Class;
            isJUnit4Class = oldJUnit4Class;
            isJUnit5Class = oldJUnit5Class;
        }

        return data;
    }

    @Override
    public Object visit(ASTClassOrInterfaceBodyDeclaration node, Object data) {
        // don't visit methods, that are not test methods
        if (node.getFirstChildOfType(ASTMethodDeclaration.class) != null && !isJUnitTestClass()) {
            return data;
        }

        // visit anything else - test methods, nested classes
        return super.visit(node, data);
    }

    protected boolean isJUnitTestClass() {
        return !isTestNgClass && (isJUnit3Class || isJUnit4Class || isJUnit5Class);
    }


    protected void analyzeJUnitClass(ASTClassOrInterfaceDeclaration node) {
        isJUnit3Class = isJUnit3Class(node);
        isJUnit4Class = isJUnit4Class(node);
        isJUnit5Class = isJUnit5Class(node);

        if (isJUnit4Class && isJUnit5Class) {
            isJUnit4Class &= hasImports(node, JUNIT4_CLASS_NAME);
            isJUnit5Class &= hasImports(node, JUNIT5_TEST_ANNOTATIONS);
        }
    }

    public static boolean isTestClass(ASTClassOrInterfaceBody node) {
        return !isAbstractClass(node) && node.getParent() instanceof ASTClassOrInterfaceDeclaration
                && (isTestClassJUnit3(node) || isTestClassJUnit4(node) || isTestClassJUnit5(node) || isTestClassTestNg(node));
    }

    private static boolean isAbstractClass(ASTClassOrInterfaceBody node) {
        if (node.getParent() instanceof ASTClassOrInterfaceDeclaration) {
            ASTClassOrInterfaceDeclaration decl = (ASTClassOrInterfaceDeclaration) node.getParent();
            return decl.isAbstract();
        }
        return false;
    }

    public static boolean isTestClassJUnit3(ASTClassOrInterfaceBody node) {
        Node parent = node.getParent();
        if (parent instanceof TypeNode) {
            TypeNode type = (TypeNode) parent;
            return isJUnit3Class(type);
        }
        return false;
    }

    public static boolean isTestClassJUnit4(ASTClassOrInterfaceBody node) {
        Node parent = node.getParent();
        if (parent instanceof TypeNode) {
            TypeNode type = (TypeNode) parent;
            return isJUnit4Class(type) && hasImports(type, JUNIT4_CLASS_NAME);
        }
        return false;
    }

    public static boolean isTestClassJUnit5(ASTClassOrInterfaceBody node) {
        Node parent = node.getParent();
        if (parent instanceof TypeNode) {
            TypeNode type = (TypeNode) parent;
            return isJUnit5Class(type) && hasImports(type, JUNIT5_TEST_ANNOTATIONS);
        }
        return false;
    }

    private static boolean isTestClassTestNg(ASTClassOrInterfaceBody node) {
        Node parent = node.getParent();
        if (parent instanceof TypeNode) {
            TypeNode type = (TypeNode) parent;
            return doesNodeContainJUnitAnnotation(type, TESTNG_ANNOTATION);
        }
        return false;
    }

    public static boolean isTestMethod(ASTMethodDeclaration method) {
        if (method.isAbstract() || method.isNative() || method.isStatic()) {
            return false; // skip various inapplicable method variations
        }

        ASTClassOrInterfaceBody type = method.getFirstParentOfType(ASTClassOrInterfaceBody.class);
        return isTestClassJUnit3(type) && method.isPublic() && method.isVoid() && method.getName().startsWith("test")
                || isTestClassJUnit4(type) && method.isPublic() && doesNodeContainJUnitAnnotation(method.getParent(), JUNIT4_CLASS_NAME)
                || isTestClassJUnit5(type) && doesNodeContainJUnitAnnotation(method.getParent(), JUNIT5_TEST_ANNOTATIONS)
                || doesNodeContainJUnitAnnotation(method.getParent(), TESTNG_ANNOTATION);
    }

    public static boolean isJUnit5NestedClass(ASTClassOrInterfaceBody innerClassDecl) {
        return doesNodeContainJUnitAnnotation((JavaNode) innerClassDecl.getNthParent(2), JUNIT5_NESTED);
    }

    public boolean isJUnitMethod(ASTMethodDeclaration method, Object data) {
        if (method.isAbstract() || method.isNative() || method.isStatic()) {
            return false; // skip various inapplicable method variations
        }

        if (!isJUnit5Class && !method.isPublic()) {
            // junit5 class doesn't require test methods to be public anymore
            return false;
        }

        boolean result = false;
        result |= isJUnit3Method(method);
        result |= isJUnit4Method(method);
        result |= isJUnit5Method(method);
        return result;
    }

    private boolean isJUnit4Method(ASTMethodDeclaration method) {
        return isJUnit4Class && doesNodeContainJUnitAnnotation(method.getParent(), JUNIT4_CLASS_NAME);
    }

    private boolean isJUnit5Method(ASTMethodDeclaration method) {
        return isJUnit5Class && doesNodeContainJUnitAnnotation(method.getParent(), JUNIT5_TEST_ANNOTATIONS);
    }

    private boolean isJUnit3Method(ASTMethodDeclaration method) {
        return isJUnit3Class && method.isVoid() && method.getName().startsWith("test");
    }

    private static boolean isJUnit3Class(TypeNode cid) {
        return TypeTestUtil.isA(JUNIT3_CLASS_NAME, cid);
    }

    private static boolean isJUnit4Class(JavaNode node) {
        return doesNodeContainJUnitAnnotation(node, JUNIT4_CLASS_NAME);
    }

    private static boolean isJUnit5Class(JavaNode node) {
        return doesNodeContainJUnitAnnotation(node, JUNIT5_TEST_ANNOTATIONS);
    }

    private static boolean doesNodeContainJUnitAnnotation(JavaNode node, String annotationTypeClassName) {
        return doesNodeContainJUnitAnnotation(node, Collections.singleton(annotationTypeClassName));
    }

    private static boolean doesNodeContainJUnitAnnotation(JavaNode node, Set<String> annotationTypeClassNames) {
        List<ASTAnnotation> annotations = node.findDescendantsOfType(ASTAnnotation.class);
        for (ASTAnnotation annotation : annotations) {
            Node annotationTypeNode = annotation.getChild(0);
            TypeNode annotationType = (TypeNode) annotationTypeNode;
            if (annotationType.getType() == null) {
                ASTName name = annotationTypeNode.getFirstChildOfType(ASTName.class);
                if (name != null) {
                    for (String annotationTypeName : annotationTypeClassNames) {
                        if (areNamesEqual(name, annotationTypeName)) {
                            return true;
                        }
                    }
                }
            } else {
                for (String annotationTypeClassName : annotationTypeClassNames) {
                    if (TypeTestUtil.isA(annotationTypeClassName, annotationType)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean hasImports(JavaNode node, String className) {
        return hasImports(node, Collections.singleton(className));
    }

    private static boolean hasImports(JavaNode node, Set<String> classNames) {
        List<ASTImportDeclaration> imports = node.getRoot().findDescendantsOfType(ASTImportDeclaration.class);
        for (ASTImportDeclaration importDeclaration : imports) {
            ASTName name = importDeclaration.getFirstChildOfType(ASTName.class);
            // imports are always fully qualified
            if (name != null && classNames.contains(name.getImage())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Compares the name against the given fully qualified name. If a direct comparison doesn't
     * work, try to compare as a simple name.
     *
     * <p>Note: This method is only used, if the auxclasspath is incomplete.</p>
     */
    private static boolean areNamesEqual(ASTName node, String fullyQualifiedAnnotationName) {
        String simpleName = node.getImage();
        if (simpleName.equals(fullyQualifiedAnnotationName)) {
            return true;
        }

        String simpleAnnotationName = fullyQualifiedAnnotationName;
        int lastDot = fullyQualifiedAnnotationName.lastIndexOf('.');
        if (lastDot != -1) {
            simpleAnnotationName = fullyQualifiedAnnotationName.substring(lastDot + 1);
        }
        // when comparing by simple name, double check whether we have a import to avoid false positives
        return simpleName.equals(simpleAnnotationName) && hasImports(node, fullyQualifiedAnnotationName);
    }
}
