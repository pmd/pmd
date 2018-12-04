/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import net.sourceforge.pmd.testframework.PmdRuleTst;

public class UnnecessaryFullyQualifiedNameTest extends PmdRuleTst {
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
