/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import net.sourceforge.pmd.testframework.PmdRuleTst;

class UnnecessaryFullyQualifiedNameTest extends PmdRuleTst {
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

    // Do not delete these classes - it is needed for a test case
    // see: /pmd-java/src/test/resources/net/sourceforge/pmd/lang/java/rule/codestyle/xml/UnnecessaryFullyQualifiedName.xml
    // #1546 part 1 UnnecessaryFullyQualifiedName doesn't take into consideration conflict resolution
    // #1546 part 2 UnnecessaryFullyQualifiedName doesn't take into consideration conflict resolution
    public static class PhonyMockito {

        public static final int TWO = 2;

        public static <T> T mock(Class<T> clazz) {
            return null;
        }
    }

    static class Container {

        public static class PhonyMockito {

            public static <T> T mock(Class<T> clazz) {
                return null;
            }
        }
    }

    public static class MockitoInherited extends PhonyMockito {
        // static method PhonyMockito::mock is static imported
        // if MockitoInherited is imported on demand
    }

}
