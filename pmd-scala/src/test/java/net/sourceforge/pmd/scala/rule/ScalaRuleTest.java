/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scala.rule;

import java.io.StringReader;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.scala.ScalaLanguageModule;
import net.sourceforge.pmd.lang.scala.ast.ScalaNode;
import net.sourceforge.pmd.lang.scala.rule.ScalaRule;

public class ScalaRuleTest {
    private static final String SCALA_TEST = "/parserFiles/helloworld.scala";

    @Test
    public void testRuleVisits() throws Exception {
        LanguageVersionHandler scalaVersionHandler = LanguageRegistry.getLanguage(ScalaLanguageModule.NAME)
                .getDefaultVersion().getLanguageVersionHandler();
        Parser parser = scalaVersionHandler.getParser(scalaVersionHandler.getDefaultParserOptions());
        ScalaNode root = (ScalaNode) parser.parse(null,
                new StringReader(IOUtils.toString(getClass().getResourceAsStream(SCALA_TEST), "UTF-8")));

        ScalaTestRule rule = new ScalaTestRule();
        rule.apply(Arrays.asList(root), null);
        Assert.assertEquals(12, rule.visited);
    }

    class ScalaTestRule extends ScalaRule {
        int visited = 0;

        @Override
        public Object visit(ScalaNode node, Object data) {
            visited++;
            return super.visit(node, data);
        }
    }
}
