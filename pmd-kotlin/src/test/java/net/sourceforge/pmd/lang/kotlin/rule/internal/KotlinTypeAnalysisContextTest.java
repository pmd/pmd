/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.internal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.kotlin.rule.xpath.internal.KotlinTypeXPathTestHelper;

class KotlinTypeAnalysisContextTest {

    private static final String SNIPPET = ""
            + "import java.util.ArrayList\n"
            + "val list: ArrayList<String> = ArrayList()\n";

    @BeforeEach
    void setUp() {
        KotlinTypeXPathTestHelper.forCode(SNIPPET).injectContext();
    }

    @AfterEach
    void tearDown() {
        KotlinTypeAnalysisContextHolder.clearGlobal();
        KotlinTypeAnalysisContextHolder.clear();
    }

    @Test
    void emptyContextIsNotNull() {
        assertNotNull(KotlinTypeAnalysisContext.empty());
    }

    @Test
    void holderReturnsInjectedContext() {
        KotlinTypeAnalysisContext ctx = KotlinTypeAnalysisContextHolder.get();
        assertNotNull(ctx);
        assertFalse(ctx == KotlinTypeAnalysisContext.empty());
    }

    @Test
    void holderThreadLocalOverridesGlobal() {
        KotlinTypeAnalysisContext local = KotlinTypeAnalysisContext.empty();
        KotlinTypeAnalysisContextHolder.set(local);
        try {
            assertSame(local, KotlinTypeAnalysisContextHolder.get());
        } finally {
            KotlinTypeAnalysisContextHolder.clear();
        }
    }

    @Test
    void isSubtypeOfSameType() {
        KotlinTypeAnalysisContext ctx = KotlinTypeAnalysisContextHolder.get();
        assertTrue(ctx.isSubtypeOf("java.util.ArrayList", "java.util.ArrayList"));
    }

    @Test
    void isSubtypeOfUnrelatedTypes() {
        KotlinTypeAnalysisContext ctx = KotlinTypeAnalysisContextHolder.get();
        assertFalse(ctx.isSubtypeOf("java.util.HashMap", "java.util.ArrayList"));
    }

    @Test
    void isTypeEquivalentJavaKotlinString() {
        KotlinTypeAnalysisContext ctx = KotlinTypeAnalysisContextHolder.get();
        assertTrue(ctx.isTypeEquivalent("java.lang.String", "kotlin.String"));
    }

    @Test
    void emptyContextIsSubtypeOfReturnsFalseForUnrelated() {
        KotlinTypeAnalysisContext empty = KotlinTypeAnalysisContext.empty();
        assertFalse(empty.isSubtypeOf("java.util.List", "java.util.ArrayList"));
    }
}
