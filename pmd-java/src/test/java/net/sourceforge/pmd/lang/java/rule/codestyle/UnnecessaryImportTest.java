/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.test.PmdRuleTst;

class UnnecessaryImportTest extends PmdRuleTst {

    @ParameterizedTest
    @ValueSource(strings = {"@see ", "{@link ", "// @link substring=\"a\" target=\"", "["})
    void craftedJavadocReferenceShouldNotStackOverflow(String prefix) {
        String leadingCode = "public class Foo {\n"
                + "    /**\n"
                + "     * ";
        String trailingCode = "\n"
                + "     */\n"
                + "    public void test() {}\n"
                + "\n"
                + "}\n";

        StringBuilder pattern = new StringBuilder(5000);
        pattern.append(leadingCode);
        pattern.append(prefix);
        for (int i = 0; i < 4000; i++) {
            pattern.append("a.");
        }
        pattern.append("Type#method(ParamType1, ParamType2)");
        pattern.append(trailingCode);

        ASTCompilationUnit compilationUnit = JavaParsingHelper.DEFAULT.parse(pattern.toString());
        UnnecessaryImportRule rule = new UnnecessaryImportRule();
        rule.visitComments(compilationUnit);
    }
}
