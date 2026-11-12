/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import static net.sourceforge.pmd.lang.test.ast.TestUtilsKt.assertSize;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.reporting.Report;

class ASTClassRefExpressionTest extends ApexParserTestBase {

    /**
     * @see <a href="https://github.com/pmd/pmd/issues/7182">[apex] Expose the referenced type name on ClassRefExpression for XPath rules</a>
     */
    @Test
    void referencedTypeIsAvailableInXPath() {
        Report report = apex.executeRuleOnResource(
            apex.newXpathRule("//MethodCallExpression[@MethodName = 'consume']"
                    + "[ClassRefExpression[@Type = 'TargetClass']]"),
            "ClassRefExpressionTypeInfo.cls");

        assertSize(report, 1);
    }
}
