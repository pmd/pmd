/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

/**
 * Rule tests for the code style category
 */
public class CodeStyleRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "category/java/codestyle.xml";

    @Override
    public void setUp() {
        addRule(RULESET, "AtLeastOneConstructor");
        addRule(RULESET, "AvoidFinalLocalVariable");
        addRule(RULESET, "AvoidPrefixingMethodParameters");
        addRule(RULESET, "AvoidProtectedFieldInFinalClass");
        addRule(RULESET, "AvoidProtectedMethodInFinalClassNotExtending");
        addRule(RULESET, "AvoidUsingNativeCode");
        addRule(RULESET, "CallSuperInConstructor");
        addRule(RULESET, "CommentDefaultAccessModifier");
        addRule(RULESET, "ConfusingTernary");
        addRule(RULESET, "DefaultPackage");
        addRule(RULESET, "DontImportJavaLang");
        addRule(RULESET, "DuplicateImports");
        addRule(RULESET, "EmptyMethodInAbstractClassShouldBeAbstract");
        addRule(RULESET, "ExtendsObject");
        addRule(RULESET, "FieldDeclarationsShouldBeAtStartOfClass");
        addRule(RULESET, "ForLoopsMustUseBraces");
        addRule(RULESET, "ForLoopShouldBeWhileLoop");
        addRule(RULESET, "IfElseStmtsMustUseBraces");
        addRule(RULESET, "IfStmtsMustUseBraces");
        addRule(RULESET, "OnlyOneReturn");
        addRule(RULESET, "TooManyStaticImports");
        addRule(RULESET, "UnnecessaryConstructor");
        addRule(RULESET, "UnnecessaryFullyQualifiedName");
        addRule(RULESET, "UnnecessaryLocalBeforeReturn");
        addRule(RULESET, "WhileLoopsMustUseBraces");
    }

    /**
     * This is just for testing DuplicateImports for static imports and
     * disambiguation.
     */
    // Do not delete this method, its needed for a test case
    // see:
    // /pmd-java/src/test/resources/net/sourceforge/pmd/lang/java/rule/codestyle/xml/DuplicateImports.xml
    // #1306 False positive on duplicate when using static imports
    public static void assertTrue(String message, boolean condition) {
        if (!condition) {
            System.out.println(message);
        }
    }

    // Do not delete these two enums - it is needed for a test case
    // see:
    // /pmd-java/src/test/resources/net/sourceforge/pmd/lang/java/rule/codestyle/xml/UnnecessaryFullyQualifiedName.xml
    // #1436 UnnecessaryFullyQualifiedName false positive on clashing static
    // imports with enums
    public enum ENUM1 {
        A, B;
    }

    public enum ENUM2 {
        C, D;
    }

    // Do not delete these two classes - it is needed for a test case
    // see: /pmd-java/src/test/resources/net/sourceforge/pmd/lang/java/rule/codestyle/xml/UnnecessaryFullyQualifiedName.xml
    // #1546 part 1 UnnecessaryFullyQualifiedName doesn't take into consideration conflict resolution
    // #1546 part 2 UnnecessaryFullyQualifiedName doesn't take into consideration conflict resolution
    public static class PhonyMockito {
        public static <T> T mock(Class<T> clazz) {
            return null;
        }
    }

    public static class PhonyPowerMockito {
        public static <T> T mock(Class<T> clazz) {
            return null;
        }
    }
}
