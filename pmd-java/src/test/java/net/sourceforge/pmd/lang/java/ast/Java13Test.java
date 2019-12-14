/*
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
public class Java13Test {
    private static String loadSource(String name) {
        try {
            return IOUtils.toString(Java13Test.class.getResourceAsStream("jdkversiontests/java13/" + name),
                                    StandardCharsets.UTF_8)
                .replaceAll("\\R", "\n"); // normalize line separators to \n
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Test(expected = ParseException.class)
    public void testSwitchExpressionsBeforeJava13() {
        ParserTstUtil.parseAndTypeResolveJava("12", loadSource("SwitchExpressions.java"));
    }


}
