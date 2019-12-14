/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.java.ParserTstUtil;

@Ignore("those tests depend on type resolution")
public class Java12Test {
    private static String loadSource(String name) {
        try {
            return IOUtils.toString(Java12Test.class.getResourceAsStream("jdkversiontests/java12/" + name),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test(expected = ParseException.class)
    public void testMultipleCaseLabelsJava11() {
        ParserTstUtil.parseAndTypeResolveJava("11", loadSource("MultipleCaseLabels.java"));
    }

    @Test(expected = ParseException.class)
    public void testSwitchRulesJava11() {
        ParserTstUtil.parseAndTypeResolveJava("11", loadSource("SwitchRules.java"));
    }


    @Test(expected = ParseException.class)
    public void testSwitchExpressionsJava11() {
        ParserTstUtil.parseAndTypeResolveJava("11", loadSource("SwitchExpressions.java"));
    }

}
