/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.imports;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class ImportsRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "java-imports";

    @Override
    public void setUp() {
        addRule(RULESET, "DontImportJavaLang");
        addRule(RULESET, "DuplicateImports");
        addRule(RULESET, "ImportFromSamePackage");
        addRule(RULESET, "TooManyStaticImports");
        addRule(RULESET, "UnnecessaryFullyQualifiedName");
        addRule(RULESET, "UnusedImports");
    }

    /**
     * This is just for testing DuplicateImports for static imports and
     * disambiguation.
     */
    // Do not delete this method, its needed for a test case
    // see:
    // /pmd-java/src/test/resources/net/sourceforge/pmd/lang/java/rule/imports/xml/DuplicateImports.xml
    // #1306 False positive on duplicate when using static imports
    public static void assertTrue(String message, boolean condition) {
        if (!condition) {
            System.out.println(message);
        }
    }

    // Do not delete these two enums - it is needed for a test case
    // see:
    // /pmd-java/src/test/resources/net/sourceforge/pmd/lang/java/rule/imports/xml/UnnecessaryFullyQualifiedName.xml
    // #1436 UnnecessaryFullyQualifiedName false positive on clashing static
    // imports with enums
    public enum ENUM1 {
        A, B;
    }

    public enum ENUM2 {
        C, D;
    }

    // Do not delete these two classes - it is needed for a test case
    // see: /pmd-java/src/test/resources/net/sourceforge/pmd/lang/java/rule/imports/xml/UnnecessaryFullyQualifiedName.xml
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
