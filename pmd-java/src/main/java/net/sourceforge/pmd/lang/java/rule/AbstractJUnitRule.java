/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule;

import java.util.List;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
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

    protected boolean isJUnit3Class;
    protected boolean isJUnit4Class;
    protected boolean isJUnit5Class;

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        ASTClassOrInterfaceDeclaration topLevelDecl = determineTopLevel(node);
        if (topLevelDecl == null) {
            return data;
        }

        analyzeJUnitClass(topLevelDecl);

        if (!isTestNgClass(node) && (isJUnit3Class || isJUnit4Class || isJUnit5Class)) {
            return super.visit(node, data);
        }

        // Manually visit nested classes.
        // This is done manually here in order to keep the visiting behavior
        // of this base class compatible.
        for (ASTAnyTypeBodyDeclaration nested : topLevelDecl.getDeclarations()) {
            JavaNode declarationNode = nested.getDeclarationNode();
            if (declarationNode instanceof ASTClassOrInterfaceDeclaration) {
                declarationNode.jjtAccept(this, data);
            }
        }

        return data;
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        // this might be a nested class, so we need to determine again whether the nested
        // class is junit or not
        analyzeJUnitClass(node);

        if (!isTestNgClass(node) && (isJUnit3Class || isJUnit4Class || isJUnit5Class)) {
            return super.visit(node, data);
        }

        return data;
    }

    private void analyzeJUnitClass(JavaNode node) {
        isJUnit3Class = false;
        if (node instanceof ASTClassOrInterfaceDeclaration) {
            isJUnit3Class = isJUnit3Class((ASTClassOrInterfaceDeclaration) node);
        }
        isJUnit4Class = isJUnit4Class(node);
        isJUnit5Class = isJUnit5Class(node);

        if (isJUnit4Class && isJUnit5Class) {
            isJUnit4Class &= hasImports(node, JUNIT4_CLASS_NAME);
            isJUnit5Class &= hasImports(node, JUNIT5_CLASS_NAME);
        }
    }

    private ASTClassOrInterfaceDeclaration determineTopLevel(ASTCompilationUnit node) {
        ASTTypeDeclaration typeDecl = node.getFirstChildOfType(ASTTypeDeclaration.class);
        if (typeDecl != null) {
            return typeDecl.getFirstChildOfType(ASTClassOrInterfaceDeclaration.class);
        }
        return null;
    }

    private boolean isTestNgClass(JavaNode node) {
        List<ASTImportDeclaration> imports = node.getRoot().findDescendantsOfType(ASTImportDeclaration.class);
        for (ASTImportDeclaration i : imports) {
            if (i.getImportedName() != null && i.getImportedName().startsWith("org.testng")) {
                return true;
            }
        }
        return false;
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
        return isJUnit5Class && doesNodeContainJUnitAnnotation(method.getParent(), JUNIT5_CLASS_NAME);
    }

    private boolean isJUnit3Method(ASTMethodDeclaration method) {
        return isJUnit3Class && method.isVoid() && method.getName().startsWith("test");
    }

    private boolean isJUnit3Class(ASTClassOrInterfaceDeclaration cid) {
        return TypeTestUtil.isA(JUNIT3_CLASS_NAME, cid);
    }

    private boolean isJUnit4Class(JavaNode node) {
        return doesNodeContainJUnitAnnotation(node, JUNIT4_CLASS_NAME);
    }

    private boolean isJUnit5Class(JavaNode node) {
        return doesNodeContainJUnitAnnotation(node, JUNIT5_CLASS_NAME);
    }

    private boolean doesNodeContainJUnitAnnotation(JavaNode node, String annotationTypeClassName) {
        List<ASTAnnotation> annotations = node.findDescendantsOfType(ASTAnnotation.class);
        for (ASTAnnotation annotation : annotations) {
            Node annotationTypeNode = annotation.getChild(0);
            TypeNode annotationType = (TypeNode) annotationTypeNode;
            if (annotationType.getType() == null) {
                ASTName name = annotationTypeNode.getFirstChildOfType(ASTName.class);
                if (name != null && (name.hasImageEqualTo("Test") || name.hasImageEqualTo(annotationTypeClassName))) {
                    return true;
                }
            } else if (TypeTestUtil.isA(annotationTypeClassName, annotationType)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasImports(JavaNode node, String className) {
        List<ASTImportDeclaration> imports = node.getRoot().findDescendantsOfType(ASTImportDeclaration.class);
        for (ASTImportDeclaration importDeclaration : imports) {
            ASTName name = importDeclaration.getFirstChildOfType(ASTName.class);
            if (name != null && name.hasImageEqualTo(className)) {
                return true;
            }
        }
        return false;
    }
}
