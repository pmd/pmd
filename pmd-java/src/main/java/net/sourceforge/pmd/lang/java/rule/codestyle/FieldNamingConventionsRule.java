/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static net.sourceforge.pmd.lang.java.ast.JModifier.FINAL;
import static net.sourceforge.pmd.lang.java.ast.JModifier.STATIC;
import static net.sourceforge.pmd.lang.java.ast.ModifierOwner.Visibility.V_PUBLIC;
import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.java.ast.ASTEnumConstant;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;


/**
 * Configurable naming conventions for field declarations.
 *
 * @author Clément Fournier
 * @since 6.7.0
 */
public class FieldNamingConventionsRule extends AbstractNamingConventionRule<ASTVariableId> {
    // TODO we need a more powerful scheme to match some fields, e.g. include modifiers/type
    // We could define a new property, but specifying property values as a single string doesn't scale
    private static final PropertyDescriptor<List<String>> EXCLUDED_NAMES =
            PropertyFactory.stringListProperty("exclusions")
                           .desc("Names of fields to whitelist.")
                           .defaultValues("serialVersionUID", "serialPersistentFields")
                           .build();

    private static final Set<String> MAKE_FIELD_STATIC_CLASS_ANNOT =
        setOf(
            "lombok.experimental.UtilityClass"
        );


    private final PropertyDescriptor<Pattern> publicConstantFieldRegex = defaultProp("public constant").defaultValue("[A-Z][A-Z_0-9]*").build();
    private final PropertyDescriptor<Pattern> constantFieldRegex = defaultProp("constant").desc("Regex which applies to non-public static final field names").defaultValue("[A-Z][A-Z_0-9]*").build();
    private final PropertyDescriptor<Pattern> enumConstantRegex = defaultProp("enum constant").defaultValue("[A-Z][A-Z_0-9]*").build();
    private final PropertyDescriptor<Pattern> finalFieldRegex = defaultProp("final field").build();
    private final PropertyDescriptor<Pattern> staticFieldRegex = defaultProp("static field").build();
    private final PropertyDescriptor<Pattern> defaultFieldRegex = defaultProp("defaultField", "field").build();


    public FieldNamingConventionsRule() {
        super(ASTFieldDeclaration.class, ASTEnumConstant.class);
        definePropertyDescriptor(publicConstantFieldRegex);
        definePropertyDescriptor(constantFieldRegex);
        definePropertyDescriptor(enumConstantRegex);
        definePropertyDescriptor(finalFieldRegex);
        definePropertyDescriptor(staticFieldRegex);
        definePropertyDescriptor(defaultFieldRegex);
        definePropertyDescriptor(EXCLUDED_NAMES);
    }

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        for (ASTVariableId id : node) {
            if (getProperty(EXCLUDED_NAMES).contains(id.getName())) {
                continue;
            }
            ASTTypeDeclaration enclosingType = node.getEnclosingType();
            boolean isFinal = node.hasModifiers(FINAL);
            boolean isStatic = node.hasModifiers(STATIC) || JavaAstUtils.hasAnyAnnotation(enclosingType, MAKE_FIELD_STATIC_CLASS_ANNOT);
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
            asCtx(data).addViolation(node, "enum constant",
                                     node.getImage(),
                                     getProperty(enumConstantRegex).toString());
        }

        return data;
    }


    @Override
    String defaultConvention() {
        return CAMEL_CASE;
    }

    @Override
    String nameExtractor(ASTVariableId node) {
        return node.getName();
    }

    @Override
    String kindDisplayName(ASTVariableId node, PropertyDescriptor<Pattern> descriptor) {

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
