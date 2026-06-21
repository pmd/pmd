/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.kotlin.rule.xpath.internal.KotlinTypeXPathTestHelper;

import nl.stokpop.typemapper.model.CallSiteAst;

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

    @Test
    void emptyContextIsSubtypeOfEquivalentNamesReturnsTrue() {
        // Empty context (null typedAst) must still resolve Java<->Kotlin name equivalence.
        assertTrue(KotlinTypeAnalysisContext.empty().isSubtypeOf("java.lang.String", "kotlin.String"));
    }

    @Test
    void activeContextIsSubtypeOfEquivalentNamesReturnsTrue() {
        // Non-empty context (real typedAst) must also resolve Java<->Kotlin equivalence
        // via ktm delegation.
        KotlinTypeAnalysisContext ctx = KotlinTypeAnalysisContextHolder.get();
        assertTrue(ctx.isSubtypeOf("java.lang.String", "kotlin.String"));
    }

    @Test
    void inMemoryAnalysisIndexesByBasename() {
        KotlinTypeAnalysisContext ctx = KotlinTypeAnalysisContextHolder.get();
        // In-memory analysis (fromSources) has no real source root;
        // declarations must be findable by basename alone.
        // Exactly 1 declaration at line 2 (val list) — confirms no duplication.
        assertEquals(1, ctx.declarationsAt("snippet.kt", 2).size());
    }

    @Test
    void unknownAbsPathFallsBackToBasenameList() {
        KotlinTypeAnalysisContext ctx = KotlinTypeAnalysisContextHolder.get();
        // An unknown abs path (not in index) falls back to the basename list;
        // both must return the same List object — no duplication.
        assertSame(
                ctx.declarationsAt("snippet.kt", 2),
                ctx.declarationsAt("/any/path/snippet.kt", 2)
        );
    }

    @Test
    void callSiteEndLineFieldIsReadable() {
        // Compilation check: CallSiteAst.endLine is available in ktm 0.6.0 (issue #9).
        // endLine defaults to 0 when the call spans a single line or for ASTs from older JSON.
        KotlinTypeAnalysisContext ctx = KotlinTypeAnalysisContextHolder.get();
        for (CallSiteAst call : ctx.callSitesAt("snippet.kt", 2)) {
            assertTrue(call.getEndLine() >= 0);
            assertTrue(call.getEndColumn() >= 0);
        }
    }
}
