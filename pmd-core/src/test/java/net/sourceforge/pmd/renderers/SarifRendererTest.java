package net.sourceforge.pmd.renderers;

import static org.junit.Assert.assertEquals;

import net.sourceforge.pmd.*;
import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SarifRendererTest extends AbstractRendererTest{
    @Override
    public Renderer getRenderer() {
        return new SarifRenderer();
    }

    @Override
    public String getExpected() {
        return readFile("expected.sarif.json");
    }

    @Override
    public String getExpectedEmpty() {
        return readFile("empty.sarif.json");
    }

    @Override
    public String getExpectedMultiple() {
        return readFile("expected-multiple.sarif.json");
    }

    @Override
    @Test
    public void testRendererMultiple() throws Exception {
        Report rep = reportTwoViolations();
        String actual = ReportTest.render(getRenderer(), rep);
        assertEquals(filter(getExpectedMultiple()), filter(actual));
    }

    private Report reportTwoViolations() {
        Report report = new Report();
        RuleViolation informationalRuleViolation = newRuleViolation(1, "Foo");
        informationalRuleViolation.getRule().setPriority(RulePriority.LOW);
        report.addRuleViolation(informationalRuleViolation);
        RuleViolation severeRuleViolation = newRuleViolation(2, "Boo");
        severeRuleViolation.getRule().setPriority(RulePriority.HIGH);
        report.addRuleViolation(severeRuleViolation);
        return report;
    }

    private RuleViolation newRuleViolation(int endColumn, String ruleName) {
        DummyNode node = createNode(endColumn);
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFile(new File(getSourceCodeFilename()));
        AbstractRule fooRule = new FooRule();
        fooRule.setName(ruleName);
        return new ParametricRuleViolation<Node>(fooRule, ctx, node, "blah");
    }

    private String readFile(String name) {
        try (InputStream in = SarifRendererTest.class.getResourceAsStream("sarif/" + name)) {
            String asd = IOUtils.toString(in, StandardCharsets.UTF_8);
            return asd;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
