/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotationTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration.TypeKind;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTInitializer;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.RegexProperty;
import net.sourceforge.pmd.properties.RegexProperty.RegexPBuilder;
import net.sourceforge.pmd.util.StringUtil;


/**
 * Configurable naming conventions for type declarations.
 */
public class ClassNamingConventionsRule extends AbstractJavaRule {

    private static final RegexProperty CLASS_REGEX = defaultProp("class").desc("Regex which applies to concrete class names").build();
    private static final RegexProperty ABSTRACT_CLASS_REGEX = defaultProp("abstract class").build();
    private static final RegexProperty INTERFACE_REGEX = defaultProp("interface").build();
    private static final RegexProperty ENUMERATION_REGEX = defaultProp("enum").build();
    private static final RegexProperty ANNOTATION_REGEX = defaultProp("annotation").build();
    private static final RegexProperty UTILITY_CLASS_REGEX = defaultProp("utility class").defaultValue("[A-Z][a-zA-Z0-9]+Utils?").build();


    public ClassNamingConventionsRule() {
        definePropertyDescriptor(CLASS_REGEX);
        definePropertyDescriptor(ABSTRACT_CLASS_REGEX);
        definePropertyDescriptor(INTERFACE_REGEX);
        definePropertyDescriptor(ENUMERATION_REGEX);
        definePropertyDescriptor(ANNOTATION_REGEX);
        definePropertyDescriptor(UTILITY_CLASS_REGEX);

        addRuleChainVisit(ASTClassOrInterfaceDeclaration.class);
        addRuleChainVisit(ASTEnumDeclaration.class);
        addRuleChainVisit(ASTAnnotationTypeDeclaration.class);
    }


    private void checkMatches(ASTAnyTypeDeclaration node, PropertyDescriptor<Pattern> regex, Object data) {
        if (!getProperty(regex).matcher(node.getImage()).matches()) {
            addViolation(data, node, new Object[]{
                    isUtilityClass(node) ? "utility class" : node.getTypeKind().getPrintableName(),
                    node.getImage(),
                    getProperty(regex).toString(),
            });
        }
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
                hasAny = true;
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


    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {

        if (node.isAbstract()) {
            checkMatches(node, ABSTRACT_CLASS_REGEX, data);
        } else if (isUtilityClass(node)) {
            checkMatches(node, UTILITY_CLASS_REGEX, data);
        } else if (node.isInterface()) {
            checkMatches(node, INTERFACE_REGEX, data);
        } else {
            checkMatches(node, CLASS_REGEX, data);
        }

        return data;
    }


    @Override
    public Object visit(ASTEnumDeclaration node, Object data) {
        checkMatches(node, ENUMERATION_REGEX, data);
        return data;
    }


    @Override
    public Object visit(ASTAnnotationTypeDeclaration node, Object data) {
        checkMatches(node, ANNOTATION_REGEX, data);
        return data;
    }


    private static RegexPBuilder defaultProp(String name) {
        return RegexProperty.named(StringUtil.toCamelCase(name) + "Pattern")
                            .desc("Regex which applies to " + name.trim() + " names")
                            .defaultValue("[A-Z][a-zA-Z0-9]+");

    }
}
