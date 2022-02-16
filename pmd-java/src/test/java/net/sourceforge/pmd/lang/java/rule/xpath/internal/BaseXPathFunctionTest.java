/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.xpath.internal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.function.Consumer;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.Assert;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.FileAnalysisException;
import net.sourceforge.pmd.lang.ast.test.TestUtilsKt;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.java.symboltable.BaseNonParserTest;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.rule.xpath.PmdXPathException;
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion;

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
public class BaseXPathFunctionTest extends BaseNonParserTest {

    private static final String VIOLATION_MESSAGE = "violation";
    private static final String RULE_NAME_PLACEHOLDER = "$rule_name";


    private @NonNull Report executeRule(Rule rule, String code) {
        return java.executeRule(rule, code);
    }

    protected Rule makeXpathRuleFromXPath(String xpath) {
        XPathRule rule = new XPathRule(XPathVersion.DEFAULT, xpath);
        rule.setName("$rule_name");
        rule.setMessage(VIOLATION_MESSAGE);
        rule.setLanguage(LanguageRegistry.getLanguage(JavaLanguageModule.NAME));
        return rule;
    }


    protected void assertFinds(Rule rule, int numViolations, String code) {
        Report report = executeRule(rule, code);
        TestUtilsKt.assertSize(report, numViolations);
    }


    protected void testWithExpectedException(String xpath,
                                             String code,
                                             Consumer<? super PmdXPathException> exceptionSpec) {

        Rule rule = makeXpathRuleFromXPath(xpath);
        FileAnalysisException thrown = Assert.assertThrows(FileAnalysisException.class, () -> executeRule(rule, code));

        assertThat(thrown.getCause(), instanceOf(PmdXPathException.class));

        PmdXPathException cause = (PmdXPathException) thrown.getCause();
        exceptionSpec.accept(cause);
        assertThat(cause.getRuleName(), equalTo(RULE_NAME_PLACEHOLDER));
    }


}
