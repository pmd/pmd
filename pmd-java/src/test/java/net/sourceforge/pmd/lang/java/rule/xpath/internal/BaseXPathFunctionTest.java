/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.xpath.internal;

import java.util.function.Consumer;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.Assert;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.test.TestUtilsKt;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.java.symboltable.BaseNonParserTest;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public class BaseXPathFunctionTest extends BaseNonParserTest {

    private static final String VIOLATION_MESSAGE = "violation";


    private @NonNull Report executeRule(Rule rule, String code) {
        return java.executeRule(rule, code);
    }

    protected Rule makeXpathRuleFromXPath(String xpath) {
        XPathRule rule = new XPathRule(XPathVersion.DEFAULT, xpath);
        rule.setMessage(VIOLATION_MESSAGE);
        rule.setLanguage(LanguageRegistry.getLanguage(JavaLanguageModule.NAME));
        return rule;
    }

    protected void assertReportSize(Rule rule, String code, int numViolations) {
        Report report = executeRule(rule, code);
        TestUtilsKt.assertSize(report, numViolations);
    }


    protected <E extends Throwable> void testWithExpectedException(String xpath,
                                                                   String code,
                                                                   Class<? extends E> exceptionClass,
                                                                   Consumer<? super E> exceptionSpec) {

        Rule rule = makeXpathRuleFromXPath(xpath);

        E thrown = Assert.assertThrows(exceptionClass, () -> executeRule(rule, code));

        exceptionSpec.accept(thrown);
    }


    protected <E extends Throwable> void testWithExpectedException(String xpath,
                                                                   String code,
                                                                   Class<? extends E> exceptionClass,
                                                                   String expectMessage) {

        testWithExpectedException(xpath, code, exceptionClass, thrown -> Assert.assertEquals("Wrong message", expectMessage, thrown.getMessage()));
    }


}
