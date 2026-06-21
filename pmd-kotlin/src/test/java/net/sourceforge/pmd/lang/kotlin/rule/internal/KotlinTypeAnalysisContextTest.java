/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

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

    // --- issue #10: dispatchReceiverType / extensionReceiverType, callsOnReceiver / callsReturning ---

    @Test
    void callSiteDispatchReceiverTypeIsSetForMethodCall() {
        // list.add("hello") is a regular method call; ktm must record non-null dispatchReceiverType.
        String code = "import java.util.ArrayList\n"
                + "fun f() {\n"
                + "    val list = ArrayList<String>()\n"
                + "    list.add(\"hello\")\n"
                + "}\n";
        KotlinTypeXPathTestHelper.forCode(code).injectContext();
        KotlinTypeAnalysisContext ctx = KotlinTypeAnalysisContextHolder.get();
        List<CallSiteAst> calls = ctx.callSitesAt("snippet.kt", 4);
        assertFalse(calls.isEmpty(), "Expected call site for list.add at line 4");
        String recv = calls.get(0).getDispatchReceiverType();
        assertNotNull(recv, "dispatchReceiverType must be non-null for a regular method call");
        assertTrue(recv.startsWith("java.util.ArrayList"), "Expected ArrayList receiver, got: " + recv);
    }

    @Test
    void callsOnReceiverFindsMethodCallsOnMatchingType() {
        // Verifies KotlinTypeAnalysisContext.callsOnReceiver() delegation (issue #10).
        String code = "import java.util.ArrayList\n"
                + "fun f() {\n"
                + "    val list = ArrayList<String>()\n"
                + "    list.add(\"hello\")\n"
                + "}\n";
        KotlinTypeXPathTestHelper.forCode(code).injectContext();
        KotlinTypeAnalysisContext ctx = KotlinTypeAnalysisContextHolder.get();
        List<CallSiteAst> calls = ctx.callsOnReceiver("java.util.ArrayList");
        assertFalse(calls.isEmpty(), "Expected calls on java.util.ArrayList receiver");
    }

    @Test
    void callsReturningFindsCallsWithMatchingReturnType() {
        // Verifies KotlinTypeAnalysisContext.callsReturning() delegation (issue #10).
        String code = "fun give(): String = \"x\"\n"
                + "val x: String = give()\n";
        KotlinTypeXPathTestHelper.forCode(code).injectContext();
        KotlinTypeAnalysisContext ctx = KotlinTypeAnalysisContextHolder.get();
        List<CallSiteAst> calls = ctx.callsReturning("kotlin.String");
        assertFalse(calls.isEmpty(), "Expected calls returning kotlin.String");
    }

    // --- type alias query delegation ---

    @Test
    void resolveTypeAliasReturnsConcreteType() {
        String code = "typealias MyStr = String\n"
                + "fun give(): MyStr = \"x\"\n";
        KotlinTypeXPathTestHelper.forCode(code).injectContext();
        KotlinTypeAnalysisContext ctx = KotlinTypeAnalysisContextHolder.get();
        assertEquals("kotlin.String", ctx.resolveTypeAlias("MyStr"));
    }

    @Test
    void callsReturningExpandingAliasFindsCallsThroughAlias() {
        // callsReturning("MyStr") returns empty because ktm records the expanded type.
        // callsReturningExpandingAlias("MyStr") expands first and finds the call.
        String code = "typealias MyStr = String\n"
                + "fun give(): MyStr = \"x\"\n"
                + "val x = give()\n";
        KotlinTypeXPathTestHelper.forCode(code).injectContext();
        KotlinTypeAnalysisContext ctx = KotlinTypeAnalysisContextHolder.get();
        assertTrue(ctx.callsReturning("MyStr").isEmpty(),
                "callsReturning by alias name must return empty (ktm stores expanded type)");
        assertFalse(ctx.callsReturningExpandingAlias("MyStr").isEmpty(),
                "callsReturningExpandingAlias must find calls by expanding the alias");
    }

    @Test
    void callsOnReceiverExpandingAliasFindsCallsThroughAlias() {
        // callsOnReceiver("MyList") returns empty; callsOnReceiverExpandingAlias expands first.
        String code = "typealias MyList = java.util.ArrayList<String>\n"
                + "fun f() {\n"
                + "    val list = java.util.ArrayList<String>()\n"
                + "    list.add(\"hello\")\n"
                + "}\n";
        KotlinTypeXPathTestHelper.forCode(code).injectContext();
        KotlinTypeAnalysisContext ctx = KotlinTypeAnalysisContextHolder.get();
        assertTrue(ctx.callsOnReceiver("MyList").isEmpty(),
                "callsOnReceiver by alias name must return empty (ktm stores expanded type)");
        assertFalse(ctx.callsOnReceiverExpandingAlias("MyList").isEmpty(),
                "callsOnReceiverExpandingAlias must find calls by expanding the alias");
    }
}
