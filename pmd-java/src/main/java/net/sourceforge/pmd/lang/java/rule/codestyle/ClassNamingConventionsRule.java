/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotationTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTRecordDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.reporting.RuleContext;


/**
 * Configurable naming conventions for type declarations.
 */
public class ClassNamingConventionsRule extends AbstractNamingConventionRule<ASTTypeDeclaration> {

    private final PropertyDescriptor<Pattern> classRegex = defaultProp("class", "concrete class").build();
    private final PropertyDescriptor<Pattern> abstractClassRegex = defaultProp("abstract class").build();
    private final PropertyDescriptor<Pattern> interfaceRegex = defaultProp("interface").build();
    private final PropertyDescriptor<Pattern> enumerationRegex = defaultProp("enum").build();
    private final PropertyDescriptor<Pattern> annotationRegex = defaultProp("annotation").build();
    private final PropertyDescriptor<Pattern> utilityClassRegex = defaultProp("utility class").build();
    private final PropertyDescriptor<Pattern> testClassRegex = defaultProp("test class")
            .desc("Regex which applies to test class names. Since PMD 6.52.0.")
            .defaultValue("^(Test|IT).*$|^[A-Z][a-zA-Z0-9]*(Test|Tests|TestCase|IT|ITCase)$").build();


    public ClassNamingConventionsRule() {
        super(ASTClassDeclaration.class,
              ASTEnumDeclaration.class,
              ASTAnnotationTypeDeclaration.class,
              ASTRecordDeclaration.class);
        definePropertyDescriptor(classRegex);
        definePropertyDescriptor(abstractClassRegex);
        definePropertyDescriptor(interfaceRegex);
        definePropertyDescriptor(enumerationRegex);
        definePropertyDescriptor(annotationRegex);
        definePropertyDescriptor(utilityClassRegex);
        definePropertyDescriptor(testClassRegex);
    }


    private boolean isTestClass(ASTClassDeclaration node) {
        return !node.isNested() && TestFrameworksUtil.isTestClass(node);
    }

    @Override
    public Object visit(ASTClassDeclaration node, Object data) {
        RuleContext ctx = (RuleContext) data;

        if (isTestClass(node)) {
            checkMatches(node, testClassRegex, ctx);
        } else if (JavaRuleUtil.isUtilityClass(node)) {
            checkMatches(node, utilityClassRegex, ctx);
        } else if (node.isInterface()) {
            checkMatches(node, interfaceRegex, ctx);
        } else {
            // at this point, node must be a class and cannot be an interface anymore
            if (node.isAbstract()) {
                checkMatches(node, abstractClassRegex, ctx);
            } else {
                checkMatches(node, classRegex, ctx);
            }
        }

        return null;
    }


    @Override
    public Object visit(ASTEnumDeclaration node, Object data) {
        RuleContext ctx = (RuleContext) data;
        checkMatches(node, enumerationRegex, ctx);
        return null;
    }

    @Override
    public Object visit(ASTRecordDeclaration node, Object data) {
        RuleContext ctx = (RuleContext) data;
        checkMatches(node, classRegex, ctx); // property?
        return null;
    }

    @Override
    public Object visit(ASTAnnotationTypeDeclaration node, Object data) {
        RuleContext ctx = (RuleContext) data;
        checkMatches(node, annotationRegex, ctx);
        return null;
    }


    @Override
    String defaultConvention() {
        return PASCAL_CASE;
    }

    @Override
    String nameExtractor(ASTTypeDeclaration node) {
        return node.getSimpleName();
    }


    @Override
    String kindDisplayName(ASTTypeDeclaration node, PropertyDescriptor<Pattern> descriptor) {
        return JavaRuleUtil.isUtilityClass(node) ? "utility class" : PrettyPrintingUtil.getPrintableNodeKind(node);
    }
}
