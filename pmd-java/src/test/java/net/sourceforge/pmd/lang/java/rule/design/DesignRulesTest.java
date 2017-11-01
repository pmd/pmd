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
        addRule(RULESET, "CollapsibleIfStatements");
        addRule(RULESET, "CouplingBetweenObjects");
        addRule(RULESET, "CyclomaticComplexity");
        addRule(RULESET, "ExcessiveClassLength");
        addRule(RULESET, "ExcessiveImports");
        addRule(RULESET, "ExcessiveMethodLength");
        addRule(RULESET, "ExcessiveParameterList");
        addRule(RULESET, "ExcessivePublicCount");
        addRule(RULESET, "LawOfDemeter");
        addRule(RULESET, "LoosePackageCoupling");
        addRule(RULESET, "ModifiedCyclomaticComplexity");
        addRule(RULESET, "NcssConstructorCount");
        addRule(RULESET, "NcssCount");
        addRule(RULESET, "NcssMethodCount");
        addRule(RULESET, "NcssTypeCount");
        addRule(RULESET, "NPathComplexity");
        addRule(RULESET, "SimplifiedTernary");
        addRule(RULESET, "StdCyclomaticComplexity");
        addRule(RULESET, "TooManyFields");
        addRule(RULESET, "TooManyMethods");
        addRule(RULESET, "UseObjectForClearerAPI");


        
        
        
        
        addRule(RULESET, "AbstractClassWithoutAbstractMethod");
        addRule(RULESET, "AbstractClassWithoutAnyMethod");
        addRule(RULESET, "AccessorClassGeneration");
        addRule(RULESET, "AccessorMethodGeneration");
        addRule(RULESET, "AvoidDeeplyNestedIfStmts");
        addRule(RULESET, "AvoidProtectedFieldInFinalClass");
        addRule(RULESET, "AvoidProtectedMethodInFinalClassNotExtending");
        addRule(RULESET, "AvoidReassigningParameters");
        addRule(RULESET, "AvoidSynchronizedAtMethodLevel");
        addRule(RULESET, "ClassWithOnlyPrivateConstructorsShouldBeFinal");
        addRule(RULESET, "ConfusingTernary");
        addRule(RULESET, "ConstantsInInterface");
        addRule(RULESET, "DefaultLabelNotLastInSwitchStmt");
        addRule(RULESET, "EmptyMethodInAbstractClassShouldBeAbstract");
        addRule(RULESET, "FieldDeclarationsShouldBeAtStartOfClass");
        addRule(RULESET, "FinalFieldCouldBeStatic");
        addRule(RULESET, "GodClass");
        addRule(RULESET, "ImmutableField");
        addRule(RULESET, "LogicInversion");
        addRule(RULESET, "NonThreadSafeSingleton");
        addRule(RULESET, "OptimizableToArrayCall");
        // addRule(RULESET, "PositionalIteratorRule"); This rule does not yet
        // exist
        addRule(RULESET, "PositionLiteralsFirstInComparisons");
        addRule(RULESET, "PositionLiteralsFirstInCaseInsensitiveComparisons");
        addRule(RULESET, "PreserveStackTrace");
        addRule(RULESET, "SimplifyBooleanExpressions");
        addRule(RULESET, "SimplifyBooleanReturns");
        addRule(RULESET, "SimplifyConditional");
        addRule(RULESET, "SingularField");
        addRule(RULESET, "SwitchDensity");
        addRule(RULESET, "SwitchStmtsShouldHaveDefault");
        addRule(RULESET, "TooFewBranchesForASwitchStatement");
        // addRule(RULESET, "TooManyHttpFilter"); This rule does not yet exist
        addRule(RULESET, "UncommentedEmptyConstructor");
        addRule(RULESET, "UncommentedEmptyMethodBody");
        addRule(RULESET, "UnnecessaryLocalBeforeReturn");
        addRule(RULESET, "UnsynchronizedStaticDateFormatter");
        addRule(RULESET, "UseCollectionIsEmpty");
        addRule(RULESET, "UseNotifyAllInsteadOfNotify");
        addRule(RULESET, "UseUtilityClass");
        addRule(RULESET, "UseVarargs");
    }
}
