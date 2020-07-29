/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sourceforge.pmd.Report.ReportBuilderListener;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.apex.ast.ASTAnonymousClass;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTUserEnum;
import net.sourceforge.pmd.lang.apex.ast.ASTUserInterface;
import net.sourceforge.pmd.lang.apex.ast.ASTUserTrigger;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.ast.ApexParserTestBase;

import apex.jorje.semantic.ast.compilation.Compilation;

public class AbstractApexRuleTest extends ApexParserTestBase {

    @Test
    public void shouldVisitTopLevelClass() throws Exception {
        run("class Foo { }");
    }

    @Test
    public void shouldVisitTopLevelInterface() throws Exception {
        run("interface Foo { }");
    }

    @Test
    public void shouldVisitTopLevelTrigger() throws Exception {
        run("trigger Foo on Account (before insert, before update) { }");
    }

    @Test
    public void shouldVisitTopLevelEnum() throws Exception {
        run("enum Foo { }");
    }

    private void run(String code) throws Exception {
        ApexNode<Compilation> node = parse(code);

        ReportBuilderListener reportBuilder = new ReportBuilderListener();
        try (RuleContext ctx = new RuleContext(reportBuilder)) {
            TopLevelRule rule = new TopLevelRule();
            rule.setMessage("Message");
            rule.apply(node, ctx);
        }
        assertEquals(1, reportBuilder.getReport().getViolations().size());
    }

    private static class TopLevelRule extends AbstractApexRule {
        @Override
        public Object visit(ASTUserClass node, Object data) {
            addViolation(data, node);
            return data;
        }

        @Override
        public Object visit(ASTUserInterface node, Object data) {
            addViolation(data, node);
            return data;
        }

        @Override
        public Object visit(ASTUserTrigger node, Object data) {
            addViolation(data, node);
            return data;
        }

        @Override
        public Object visit(ASTUserEnum node, Object data) {
            addViolation(data, node);
            return data;
        }

        @Override
        public Object visit(ASTAnonymousClass node, Object data) {
            addViolation(data, node);
            return data;
        }
    }
}
