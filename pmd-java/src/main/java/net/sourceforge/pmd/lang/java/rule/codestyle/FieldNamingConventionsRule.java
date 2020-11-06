/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.List;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.java.ast.ASTEnumConstant;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;


/**
 * Configurable naming conventions for field declarations.
 *
 * @author Clément Fournier
 * @since 6.7.0
 */
public class FieldNamingConventionsRule extends AbstractNamingConventionRule<ASTVariableDeclaratorId> {
    // TODO we need a more powerful scheme to match some fields, e.g. include modifiers/type
    // We could define a new property, but specifying property values as a single string doesn't scale
    private static final PropertyDescriptor<List<String>> EXCLUDED_NAMES =
            PropertyFactory.stringListProperty("exclusions")
                           .desc("Names of fields to whitelist.")
                           .defaultValues("serialVersionUID", "serialPersistentFields")
                           .build();


    private final PropertyDescriptor<Pattern> publicConstantFieldRegex = defaultProp("public constant").defaultValue("[A-Z][A-Z_0-9]*").build();
    private final PropertyDescriptor<Pattern> constantFieldRegex = defaultProp("constant").desc("Regex which applies to non-public static final field names").defaultValue("[A-Z][A-Z_0-9]*").build();
    private final PropertyDescriptor<Pattern> enumConstantRegex = defaultProp("enum constant").defaultValue("[A-Z][A-Z_0-9]*").build();
    private final PropertyDescriptor<Pattern> finalFieldRegex = defaultProp("final field").build();
    private final PropertyDescriptor<Pattern> staticFieldRegex = defaultProp("static field").build();
    private final PropertyDescriptor<Pattern> defaultFieldRegex = defaultProp("defaultField", "field").build();


    public FieldNamingConventionsRule() {
        definePropertyDescriptor(publicConstantFieldRegex);
        definePropertyDescriptor(constantFieldRegex);
        definePropertyDescriptor(enumConstantRegex);
        definePropertyDescriptor(finalFieldRegex);
        definePropertyDescriptor(staticFieldRegex);
        definePropertyDescriptor(defaultFieldRegex);
        definePropertyDescriptor(EXCLUDED_NAMES);

        addRuleChainVisit(ASTFieldDeclaration.class);
        addRuleChainVisit(ASTEnumConstant.class);
    }


    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        for (ASTVariableDeclaratorId id : node) {
            if (getProperty(EXCLUDED_NAMES).contains(id.getVariableName())) {
                continue;
            }

            if (node.isFinal() && node.isStatic()) {
                checkMatches(id, node.isPublic() ? publicConstantFieldRegex : constantFieldRegex, data);
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

        if (field.isFinal() && field.isStatic()) {
            return field.isPublic() ? "public constant" : "constant";
        } else if (field.isFinal()) {
            return "final field";
        } else if (field.isStatic()) {
            return "static field";
        } else {
            return "field";
        }
    }

}
