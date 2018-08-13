/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.java.ast.ASTEnumConstant;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.RegexProperty;


/**
 * Configurable naming conventions for field declarations.
 *
 * @author Clément Fournier
 * @since 6.7.0
 */
public class FieldNamingConventionsRule extends AbstractNamingConventionRule<ASTVariableDeclaratorId> {
    private final RegexProperty constantFieldRegex = defaultProp("constant").defaultValue("[A-Z][A-Z_0-9]*").build();
    private final RegexProperty enumConstantRegex = defaultProp("enum constant").defaultValue("[A-Z][A-Z_0-9]*").build();
    private final RegexProperty finalFieldRegex = defaultProp("final field").build();
    private final RegexProperty staticFieldRegex = defaultProp("static field").build();
    private final RegexProperty defaultFieldRegex = defaultProp("defaultField", "field").build();


    public FieldNamingConventionsRule() {
        definePropertyDescriptor(constantFieldRegex);
        definePropertyDescriptor(enumConstantRegex);
        definePropertyDescriptor(finalFieldRegex);
        definePropertyDescriptor(staticFieldRegex);
        definePropertyDescriptor(defaultFieldRegex);

        addRuleChainVisit(ASTFieldDeclaration.class);
        addRuleChainVisit(ASTEnumConstant.class);
    }


    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {

        for (ASTVariableDeclaratorId id : node) {
            if (node.isFinal() && node.isStatic()) {
                checkMatches(id, constantFieldRegex, data);
            } else if (node.isFinal()) {
                checkMatches(id, finalFieldRegex, data);
            } else if (node.isStatic()) {
                checkMatches(id, staticFieldRegex, data);
            } else {
                checkMatches(id, defaultFieldRegex, data);
            }
        }
        return data;
    }


    @Override
    public Object visit(ASTEnumConstant node, Object data) {
        // This inlines checkMatches because there's no variable declarator id

        if (!getProperty(enumConstantRegex).matcher(node.getImage()).matches()) {
            addViolation(data, node, new Object[]{
                "enum constant",
                node.getImage(),
                getProperty(enumConstantRegex).toString(),
            });
        }

        return data;
    }


    @Override
    String defaultConvention() {
        return CAMEL_CASE;
    }


    @Override
    String kindDisplayName(ASTVariableDeclaratorId node, PropertyDescriptor<Pattern> descriptor) {
        ASTFieldDeclaration field = (ASTFieldDeclaration) node.getNthParent(2);

        if (field.isFinal()) {
            return field.isStatic() ? "constant" : "final field";
        } else if (field.isStatic()) {
            return "static field";
        } else {
            return "field";
        }
    }

}
