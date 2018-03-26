/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.java.metrics.MetricsHook;
import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

/**
 * Tests all the rules, that are in the design category.
 */
public class DesignRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "category/java/design.xml";

    @Override
    protected Rule reinitializeRule(Rule rule) {
        MetricsHook.reset();
        return rule;
    }

    @Override
    public void setUp() {
        addRule(RULESET, "AbstractClassWithoutAnyMethod");
        addRule(RULESET, "AvoidCatchingGenericException");
        addRule(RULESET, "AvoidDeeplyNestedIfStmts");
        addRule(RULESET, "AvoidRethrowingException");
        addRule(RULESET, "AvoidThrowingNewInstanceOfSameException");
        addRule(RULESET, "AvoidThrowingNullPointerException");
        addRule(RULESET, "AvoidThrowingRawExceptionTypes");
        addRule(RULESET, "ClassWithOnlyPrivateConstructorsShouldBeFinal");
        addRule(RULESET, "CollapsibleIfStatements");
        addRule(RULESET, "CouplingBetweenObjects");
        addRule(RULESET, "CyclomaticComplexity");
        addRule(RULESET, "DataClass");
        addRule(RULESET, "DoNotExtendJavaLangError");
        addRule(RULESET, "ExceptionAsFlowControl");
        addRule(RULESET, "ExcessiveClassLength");
        addRule(RULESET, "ExcessiveImports");
        addRule(RULESET, "ExcessiveMethodLength");
        addRule(RULESET, "ExcessiveParameterList");
        addRule(RULESET, "ExcessivePublicCount");
        addRule(RULESET, "FinalFieldCouldBeStatic");
        addRule(RULESET, "GodClass");
        addRule(RULESET, "ImmutableField");
        addRule(RULESET, "LawOfDemeter");
        addRule(RULESET, "LogicInversion");
        addRule(RULESET, "LoosePackageCoupling");
        addRule(RULESET, "ModifiedCyclomaticComplexity");
        addRule(RULESET, "NcssConstructorCount");
        addRule(RULESET, "NcssCount");
        addRule(RULESET, "NcssMethodCount");
        addRule(RULESET, "NcssTypeCount");
        addRule(RULESET, "NPathComplexity");
        addRule(RULESET, "SignatureDeclareThrowsException");
        addRule(RULESET, "SimplifiedTernary");
        addRule(RULESET, "SimplifyBooleanAssertion");
        addRule(RULESET, "SimplifyBooleanExpressions");
        addRule(RULESET, "SimplifyBooleanReturns");
        addRule(RULESET, "SimplifyConditional");
        addRule(RULESET, "SingularField");
        addRule(RULESET, "StdCyclomaticComplexity");
        addRule(RULESET, "SwitchDensity");
        addRule(RULESET, "TooManyFields");
        addRule(RULESET, "TooManyMethods");
        addRule(RULESET, "UselessOverridingMethod");
        addRule(RULESET, "UseObjectForClearerAPI");
        addRule(RULESET, "UseUtilityClass");

        // addRule(RULESET, "PositionalIteratorRule"); This rule does not yet
        // exist
        // addRule(RULESET, "TooManyHttpFilter"); This rule does not yet exist
    }
}
