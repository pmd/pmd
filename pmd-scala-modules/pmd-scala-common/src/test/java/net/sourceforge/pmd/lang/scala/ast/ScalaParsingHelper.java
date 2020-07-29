/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RulesetsFactoryUtils;
import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.scala.ScalaLanguageModule;
import net.sourceforge.pmd.processor.PmdRunnable;
import net.sourceforge.pmd.util.datasource.DataSource;

public final class ScalaParsingHelper extends BaseParsingHelper<ScalaParsingHelper, ASTSource> {

    public static final ScalaParsingHelper DEFAULT = new ScalaParsingHelper(Params.getDefaultProcess());

    private ScalaParsingHelper(Params params) {
        super(ScalaLanguageModule.NAME, ASTSource.class, params);
    }

    @Override
    protected ScalaParsingHelper clone(Params params) {
        return new ScalaParsingHelper(params);
    }


    public Report getReportForTestString(Rule rule, String testSourceCode) {
        RuleSet rules = RulesetsFactoryUtils.defaultFactory().createSingleRuleRuleSet(rule);
        return new PmdRunnable(
            DataSource.forString(testSourceCode, "test.scala"),
            RuleContext.throwingExceptions(),
            listOf(rules),
            new PMDConfiguration()
        ).call();
    }

    public Report getReportForResource(Rule rule, String resourcePath) {
        return getReportForTestString(rule, readResource(resourcePath));
    }
}
