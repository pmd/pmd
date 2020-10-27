/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static net.sourceforge.pmd.lang.java.ast.AccessNode.Visibility.V_PUBLIC;
import static net.sourceforge.pmd.lang.java.ast.JModifier.FINAL;
import static net.sourceforge.pmd.lang.java.ast.JModifier.STATIC;

import java.util.List;
import java.util.regex.Pattern;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.ast.ASTEnumConstant;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;
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
    }

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTFieldDeclaration.class, ASTEnumConstant.class);
    }

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        for (ASTVariableDeclaratorId id : node) {
            if (getProperty(EXCLUDED_NAMES).contains(id.getVariableName())) {
                continue;
            }

            boolean isFinal = node.hasModifiers(FINAL);
            boolean isStatic = node.hasModifiers(STATIC);
            if (isFinal && isStatic) {
                checkMatches(id, node.getVisibility() == V_PUBLIC ? publicConstantFieldRegex : constantFieldRegex, data);
            } else if (isFinal) {
                checkMatches(id, finalFieldRegex, data);
            } else if (isStatic) {
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
    String nameExtractor(ASTVariableDeclaratorId node) {
        return node.getName();
    }

    @Override
    String kindDisplayName(ASTVariableDeclaratorId node, PropertyDescriptor<Pattern> descriptor) {

        boolean isFinal = node.hasModifiers(FINAL);
        boolean isStatic = node.hasModifiers(STATIC);

        if (isFinal && isStatic) {
            return node.getVisibility() == V_PUBLIC ? "public constant" : "constant";
        } else if (isFinal) {
            return "final field";
        } else if (isStatic) {
            return "static field";
        } else {
            return "field";
        }
    }

}
