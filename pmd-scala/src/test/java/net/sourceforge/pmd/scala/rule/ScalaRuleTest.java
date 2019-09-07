/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scala.rule;

import java.io.File;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.scala.ScalaLanguageModule;
import net.sourceforge.pmd.lang.scala.ast.ASTTermApply;
import net.sourceforge.pmd.lang.scala.ast.ASTTermName;
import net.sourceforge.pmd.lang.scala.ast.ScalaNode;
import net.sourceforge.pmd.lang.scala.rule.ScalaRule;

public class ScalaRuleTest {
    private static final String SCALA_TEST = "/parserFiles/helloworld.scala";

    @Test
    public void testRuleVisits() throws Exception {
        LanguageVersionHandler scalaVersionHandler = LanguageRegistry.getLanguage(ScalaLanguageModule.NAME)
                .getDefaultVersion().getLanguageVersionHandler();
        Parser parser = scalaVersionHandler.getParser(scalaVersionHandler.getDefaultParserOptions());
        ScalaNode<?> root = (ScalaNode<?>) parser.parse(null,
                new StringReader(IOUtils.toString(getClass().getResourceAsStream(SCALA_TEST), "UTF-8")));
        final AtomicInteger visited = new AtomicInteger();
        ScalaRule rule = new ScalaRule() {

            @Override
            public RuleContext visit(ScalaNode<?> node, RuleContext data) {
                visited.incrementAndGet();
                return super.visit(node, data);
            }
        };
        rule.apply(Arrays.asList(root), null);
        Assert.assertEquals(12, visited.get());
    }

    @Test
    public void testDummyRule() throws Exception {
        ScalaRule rule = new ScalaRule() {
            @Override
            public RuleContext visit(ASTTermApply node, RuleContext data) {
                ASTTermName child = node.getFirstChildOfType(ASTTermName.class);
                if (child != null && child.hasImageEqualTo("println")) {
                    addViolation(data, node);
                }
                return data;
            }
        };
        Report report = getReportForTestString(rule,
                IOUtils.toString(getClass().getResourceAsStream(SCALA_TEST), "UTF-8"));

        int vioCount = 0;
        Iterator<RuleViolation> rvIter = report.iterator();
        while (rvIter.hasNext()) {
            rvIter.next();
            vioCount++;
        }

        Assert.assertEquals(1, vioCount);
    }

    private static Report getReportForTestString(Rule r, String test) throws PMDException {
        PMD p = new PMD();
        RuleContext ctx = new RuleContext();
        Report report = new Report();
        ctx.setReport(report);
        ctx.setSourceCodeFile(new File("test.scala"));
        RuleSet rules = new RuleSetFactory().createSingleRuleRuleSet(r);
        p.getSourceCodeProcessor().processSourceCode(new StringReader(test), new RuleSets(rules), ctx);
        return report;
    }

}
