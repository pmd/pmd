/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.lang.apex.ast.ASTAnonymousClass;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTUserEnum;
import net.sourceforge.pmd.lang.apex.ast.ASTUserInterface;
import net.sourceforge.pmd.lang.apex.ast.ASTUserTrigger;
import net.sourceforge.pmd.lang.apex.ast.ApexParserTestBase;
import net.sourceforge.pmd.lang.ast.test.TestUtilsKt;

class AbstractApexRuleTest extends ApexParserTestBase {

    @Test
    void shouldVisitTopLevelClass() {
        run("class Foo { }");
    }

    @Test
    void shouldVisitTopLevelInterface() {
        run("interface Foo { }");
    }

    @Test
    void shouldVisitTopLevelTrigger() {
        run("trigger Foo on Account (before insert, before update) { }");
    }

    @Test
    void shouldVisitTopLevelEnum() {
        run("enum Foo { }");
    }

    private void run(String code) {
        TopLevelRule rule = new TopLevelRule();

        Report report = apex.executeRule(rule, code);
        TestUtilsKt.assertSize(report, 1);
    }

    private static class TopLevelRule extends AbstractApexRule {

        @Override
        public String getMessage() {
            return "a message";
        }

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
