/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;

/**
 * KEEP PRIVATE
 * @author Clément Fournier
 */
final class AstImplUtil {

    private static final List<String> UNUSED_RULES
        = Arrays.asList("UnusedPrivateField", "UnusedLocalVariable", "UnusedPrivateMethod", "UnusedFormalParameter");

    private static final List<String> SERIAL_RULES = Arrays.asList("BeanMembersShouldSerialize", "MissingSerialVersionUID");

    private AstImplUtil() {

    }

    private static boolean isSuppressWarnings(ASTAnnotation astAnnotation) {
        return TypeHelper.isA(astAnnotation, "java.lang.SuppressWarnings");
    }

    public static boolean suppresses(ASTAnnotation annotation, Rule rule) {
        // if (SuppressWarnings.class.equals(getType())) { // typeres is not always on
        if (isSuppressWarnings(annotation)) {
            for (ASTLiteral element : annotation.findDescendantsOfType(ASTLiteral.class)) {
                if (element.hasImageEqualTo("\"PMD\"") || element.hasImageEqualTo(
                    "\"PMD." + rule.getName() + "\"")
                    // Check for standard annotations values
                    || element.hasImageEqualTo("\"all\"")
                    || element.hasImageEqualTo("\"serial\"") && SERIAL_RULES.contains(rule.getName())
                    || element.hasImageEqualTo("\"unused\"") && UNUSED_RULES.contains(rule.getName())
                    || element.hasImageEqualTo("\"all\"")) {
                    return true;
                }
            }
        }

        return false;
    }
}
