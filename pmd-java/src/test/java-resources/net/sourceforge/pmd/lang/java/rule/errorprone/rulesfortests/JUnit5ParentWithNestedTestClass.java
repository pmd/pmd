/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone.rulesfortests;

import org.junit.jupiter.api.Nested;

public class JUnit5ParentWithNestedTestClass {
    @Nested
    class NestedClass {
    }
}
