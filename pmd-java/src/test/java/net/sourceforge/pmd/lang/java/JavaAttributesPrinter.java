/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ModifierOwner;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;
import net.sourceforge.pmd.lang.test.ast.RelevantAttributePrinter;

/**
 * Special tweak to remove deprecated attributes of {@link ModifierOwner}
 * and deprecated attributes in general.
 *
 * @see BaseJavaTreeDumpTest
 */
public class JavaAttributesPrinter extends RelevantAttributePrinter {

    @Override
    protected boolean ignoreAttribute(@NonNull Node node, @NonNull Attribute attribute) {
        return super.ignoreAttribute(node, attribute)
            // Deprecated attributes are removed from the output
            // This is only for java-grammar, since deprecated getters will
            // be removed, it would be a pain to update all tree dump tests
            // everytime. OTOH failing dump tests would warn us that we removed
            // something that wasn't deprecated
            || attribute.isDeprecated()
            || "MainMethod".equals(attribute.getName()) && node instanceof ASTMethodDeclaration && !isBooleanTrue(attribute.getValue())
            || "Expression".equals(attribute.getName()) && node instanceof ASTExpression;
    }

    private boolean isBooleanTrue(Object o) {
        // for some reason Boolean::new is called somewhere in the reflection layer
        return o instanceof Boolean && (Boolean) o;
    }
}
