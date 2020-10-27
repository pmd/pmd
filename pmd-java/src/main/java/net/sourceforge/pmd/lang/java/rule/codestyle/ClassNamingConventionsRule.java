/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.regex.Pattern;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotationTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTRecordDeclaration;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.java.ast.AccessNode.Visibility;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;
import net.sourceforge.pmd.properties.PropertyDescriptor;


/**
 * Configurable naming conventions for type declarations.
 */
public class ClassNamingConventionsRule extends AbstractNamingConventionRule<ASTAnyTypeDeclaration> {

    private final PropertyDescriptor<Pattern> classRegex = defaultProp("class", "concrete class").build();
    private final PropertyDescriptor<Pattern> abstractClassRegex = defaultProp("abstract class").build();
    private final PropertyDescriptor<Pattern> interfaceRegex = defaultProp("interface").build();
    private final PropertyDescriptor<Pattern> enumerationRegex = defaultProp("enum").build();
    private final PropertyDescriptor<Pattern> annotationRegex = defaultProp("annotation").build();
    private final PropertyDescriptor<Pattern> utilityClassRegex = defaultProp("utility class").defaultValue("[A-Z][a-zA-Z0-9]+(Utils?|Helper|Constants)").build();


    public ClassNamingConventionsRule() {
        definePropertyDescriptor(classRegex);
        definePropertyDescriptor(abstractClassRegex);
        definePropertyDescriptor(interfaceRegex);
        definePropertyDescriptor(enumerationRegex);
        definePropertyDescriptor(annotationRegex);
        definePropertyDescriptor(utilityClassRegex);
    }

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTClassOrInterfaceDeclaration.class,
                                           ASTEnumDeclaration.class,
                                           ASTAnnotationTypeDeclaration.class,
                                           ASTRecordDeclaration.class);
    }

    // This could probably be moved to ClassOrInterfaceDeclaration
    // to share the implementation and be used from XPath
    private boolean isUtilityClass(ASTAnyTypeDeclaration node) {
        if (node.isInterface() || node.isEnum()) {
            return false;
        }

        ASTClassOrInterfaceDeclaration classNode = (ASTClassOrInterfaceDeclaration) node;

        // A class with a superclass or interfaces should not be considered
        if (classNode.getSuperClassTypeNode() != null
                || !classNode.getSuperInterfaceTypeNodes().isEmpty()) {
            return false;
        }

        // A class without declarations shouldn't be reported
        boolean hasAny = false;

        for (ASTBodyDeclaration declNode : classNode.getDeclarations()) {
            if (declNode instanceof ASTFieldDeclaration
                || declNode instanceof ASTMethodDeclaration) {

                hasAny = isNonPrivate(declNode) && !isMainMethod(declNode);
                if (!((AccessNode) declNode).hasModifiers(JModifier.STATIC)) {
                    return false;
                }

            } else if (declNode instanceof ASTInitializer) {
                if (!((ASTInitializer) declNode).isStatic()) {
                    return false;
                }
            }
        }

        return hasAny;
    }

    private boolean isNonPrivate(ASTBodyDeclaration decl) {
        return ((AccessNode) decl).getVisibility() != Visibility.V_PRIVATE;
    }

    private boolean isMainMethod(ASTBodyDeclaration bodyDeclaration) {
        if (!(bodyDeclaration instanceof ASTMethodDeclaration)) {
            return false;
        }

        ASTMethodDeclaration decl = (ASTMethodDeclaration) bodyDeclaration;


        return decl.hasModifiers(JModifier.PUBLIC, JModifier.STATIC)
                && "main".equals(decl.getName())
                && decl.isVoid()
                && decl.getArity() == 1
                && TypeTestUtil.isExactlyA(String[].class, decl.getFormalParameters().get(0));
    }


    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {

        if (node.isAbstract()) {
            checkMatches(node, abstractClassRegex, data);
        } else if (isUtilityClass(node)) {
            checkMatches(node, utilityClassRegex, data);
        } else if (node.isInterface()) {
            checkMatches(node, interfaceRegex, data);
        } else {
            checkMatches(node, classRegex, data);
        }

        return data;
    }


    @Override
    public Object visit(ASTEnumDeclaration node, Object data) {
        checkMatches(node, enumerationRegex, data);
        return data;
    }


    @Override
    public Object visit(ASTAnnotationTypeDeclaration node, Object data) {
        checkMatches(node, annotationRegex, data);
        return data;
    }


    @Override
    String defaultConvention() {
        return PASCAL_CASE;
    }

    @Override
    String nameExtractor(ASTAnyTypeDeclaration node) {
        return node.getSimpleName();
    }


    @Override
    String kindDisplayName(ASTAnyTypeDeclaration node, PropertyDescriptor<Pattern> descriptor) {
        return isUtilityClass(node) ? "utility class" : PrettyPrintingUtil.kindName(node);
    }
}
