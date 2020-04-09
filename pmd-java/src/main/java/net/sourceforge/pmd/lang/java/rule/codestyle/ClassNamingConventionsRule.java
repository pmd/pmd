/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotationTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeBodyDeclaration.DeclarationKind;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration.TypeKind;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
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

        addRuleChainVisit(ASTClassOrInterfaceDeclaration.class);
        addRuleChainVisit(ASTEnumDeclaration.class);
        addRuleChainVisit(ASTAnnotationTypeDeclaration.class);
    }


    // This could probably be moved to ClassOrInterfaceDeclaration
    // to share the implementation and be used from XPath
    private boolean isUtilityClass(ASTAnyTypeDeclaration node) {
        if (node.getTypeKind() != TypeKind.CLASS) {
            return false;
        }

        ASTClassOrInterfaceDeclaration classNode = (ASTClassOrInterfaceDeclaration) node;

        // A class with a superclass or interfaces should not be considered
        if (classNode.getSuperClassTypeNode() != null
                || !classNode.getSuperInterfacesTypeNodes().isEmpty()) {
            return false;
        }

        // A class without declarations shouldn't be reported
        boolean hasAny = false;

        for (ASTAnyTypeBodyDeclaration decl : classNode.getDeclarations()) {
            switch (decl.getKind()) {
            case FIELD:
            case METHOD:
                hasAny = isNonPrivate(decl) && !isMainMethod(decl);
                if (!((AccessNode) decl.getDeclarationNode()).isStatic()) {
                    return false;
                }
                break;

            case INITIALIZER:
                if (!((ASTInitializer) decl.getDeclarationNode()).isStatic()) {
                    return false;
                }
                break;

            default:
                break;
            }
        }

        return hasAny;
    }

    private boolean isNonPrivate(ASTAnyTypeBodyDeclaration decl) {
        return !((AccessNode) decl.getDeclarationNode()).isPrivate();
    }


    private boolean isMainMethod(ASTAnyTypeBodyDeclaration bodyDeclaration) {
        if (DeclarationKind.METHOD != bodyDeclaration.getKind()) {
            return false;
        }

        ASTMethodDeclaration decl = (ASTMethodDeclaration) bodyDeclaration.getDeclarationNode();

        return decl.isStatic()
                && "main".equals(decl.getName())
                && decl.getResultType().isVoid()
                && decl.getFormalParameters().size() == 1
                && String[].class.equals(decl.getFormalParameters().iterator().next().getType());
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
    String kindDisplayName(ASTAnyTypeDeclaration node, PropertyDescriptor<Pattern> descriptor) {
        return isUtilityClass(node) ? "utility class" : PrettyPrintingUtil.kindName(node);
    }
}
