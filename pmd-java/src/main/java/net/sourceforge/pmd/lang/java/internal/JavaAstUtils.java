/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.internal;

import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;

/**
 *
 */
public final class JavaAstUtils {
    private static final Pattern FIELD_NAME_PATTERN = Pattern.compile("(?:m_|_)?(\\w+)");

    private JavaAstUtils() {
        // utility class
    }


    public static boolean isGetterOrSetter(ASTMethodDeclaration node) {

        // fields names mapped to their types
        Map<String, String> fieldNames =
            node.getEnclosingType()
                .getDeclarations()
                .filterIs(ASTFieldDeclaration.class)
                .flatMap(ASTFieldDeclaration::getVarIds)
                .collect(Collectors.toMap(
                    f -> {
                        Matcher matcher = FIELD_NAME_PATTERN.matcher(f.getVariableName());
                        return matcher.find() ? matcher.group(1) : f.getVariableName();
                    },
                    f -> f.getTypeNode().getTypeImage()
                ));

        return isGetter(node, fieldNames) || isSetter(node, fieldNames);
    }


    /** Attempts to determine if the method is a getter. */
    private static boolean isGetter(ASTMethodDeclaration node, Map<String, String> fieldNames) {

        if (node.getArity() != 0 || node.isVoid()) {
            return false;
        }

        if (node.getName().startsWith("get")) {
            return containsIgnoreCase(fieldNames.keySet(), node.getName().substring(3));
        } else if (node.getName().startsWith("is")) {
            return containsIgnoreCase(fieldNames.keySet(), node.getName().substring(2));
        }


        return fieldNames.containsKey(node.getName());
    }


    /** Attempts to determine if the method is a setter. */
    private static boolean isSetter(ASTMethodDeclaration node, Map<String, String> fieldNames) {

        if (node.getArity() != 1 || !node.isVoid()) {
            return false;
        }

        if (node.getName().startsWith("set")) {
            return containsIgnoreCase(fieldNames.keySet(), node.getName().substring(3));
        }

        return fieldNames.containsKey(node.getName());
    }


    private static boolean containsIgnoreCase(Set<String> set, String str) {
        for (String s : set) {
            if (str.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }


}
