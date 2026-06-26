/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.stokpop.typemapper.analyzer.KotlinTypeMapper;
import nl.stokpop.typemapper.model.CallSiteAst;
import nl.stokpop.typemapper.model.TypedAst;

class KotlinTypeAnalysisContextTest {

    private static final String SNIPPET = ""
            + "import java.util.ArrayList\n"
            + "val list: ArrayList<String> = ArrayList()\n";

    private KotlinTypeAnalysisContext ctx;

    @BeforeEach
    void setUp() {
        TypedAst ast = KotlinTypeMapper.fromSources(
                Collections.singletonMap("snippet.kt", SNIPPET),
                Collections.<File>emptyList());
        ctx = KotlinTypeAnalysisContext.from(ast);
    }

    @Test
    void emptyContextIsNotNull() {
        assertNotNull(KotlinTypeAnalysisContext.empty());
    }

    @Test
    void isSubtypeOfSameType() {
        assertTrue(ctx.isSubtypeOf("java.util.ArrayList", "java.util.ArrayList"));
    }

    @Test
    void isSubtypeOfUnrelatedTypes() {
        assertFalse(ctx.isSubtypeOf("java.util.HashMap", "java.util.ArrayList"));
    }

    @Test
    void isTypeEquivalentJavaKotlinString() {
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
        assertTrue(ctx.isSubtypeOf("java.lang.String", "kotlin.String"));
    }

    @Test
    void inMemoryAnalysisIndexesByRelativePath() {
        // In-memory analysis (fromSources) indexes by the relativePath key passed to fromSources.
        // Exactly 1 declaration at line 2 (val list) — confirms no duplication.
        assertEquals(1, ctx.declarationsAt("snippet.kt", 2).size());
    }

    @Test
    void unknownAbsPathReturnsEmpty() {
        // A path not present in the index returns empty — no silent wrong-file hit.
        assertEquals(0, ctx.declarationsAt("/any/path/snippet.kt", 2).size());
    }

    @Test
    void callSiteEndLineFieldIsReadable() {
        // Compilation check: CallSiteAst.endLine is available in ktm 0.6.0 (issue #9).
        // endLine defaults to 0 when the call spans a single line or for ASTs from older JSON.
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
        TypedAst ast = KotlinTypeMapper.fromSources(
                Collections.singletonMap("snippet.kt", code), Collections.<File>emptyList());
        KotlinTypeAnalysisContext ctx = KotlinTypeAnalysisContext.from(ast);
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
        TypedAst ast = KotlinTypeMapper.fromSources(
                Collections.singletonMap("snippet.kt", code), Collections.<File>emptyList());
        KotlinTypeAnalysisContext ctx = KotlinTypeAnalysisContext.from(ast);
        List<CallSiteAst> calls = ctx.callsOnReceiver("java.util.ArrayList");
        assertFalse(calls.isEmpty(), "Expected calls on java.util.ArrayList receiver");
    }

    @Test
    void callsReturningFindsCallsWithMatchingReturnType() {
        // Verifies KotlinTypeAnalysisContext.callsReturning() delegation (issue #10).
        String code = "fun give(): String = \"x\"\n"
                + "val x: String = give()\n";
        TypedAst ast = KotlinTypeMapper.fromSources(
                Collections.singletonMap("snippet.kt", code), Collections.<File>emptyList());
        KotlinTypeAnalysisContext ctx = KotlinTypeAnalysisContext.from(ast);
        List<CallSiteAst> calls = ctx.callsReturning("kotlin.String");
        assertFalse(calls.isEmpty(), "Expected calls returning kotlin.String");
    }

    // --- type alias query delegation ---

    @Test
    void resolveTypeAliasReturnsConcreteType() {
        String code = "typealias MyStr = String\n"
                + "fun give(): MyStr = \"x\"\n";
        TypedAst ast = KotlinTypeMapper.fromSources(
                Collections.singletonMap("snippet.kt", code), Collections.<File>emptyList());
        KotlinTypeAnalysisContext ctx = KotlinTypeAnalysisContext.from(ast);
        assertEquals("kotlin.String", ctx.resolveTypeAlias("MyStr"));
    }

    @Test
    void callsReturningExpandingAliasFindsCallsThroughAlias() {
        // callsReturning("MyStr") returns empty because ktm records the expanded type.
        // callsReturningExpandingAlias("MyStr") expands first and finds the call.
        String code = "typealias MyStr = String\n"
                + "fun give(): MyStr = \"x\"\n"
                + "val x = give()\n";
        TypedAst ast = KotlinTypeMapper.fromSources(
                Collections.singletonMap("snippet.kt", code), Collections.<File>emptyList());
        KotlinTypeAnalysisContext ctx = KotlinTypeAnalysisContext.from(ast);
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
        TypedAst ast = KotlinTypeMapper.fromSources(
                Collections.singletonMap("snippet.kt", code), Collections.<File>emptyList());
        KotlinTypeAnalysisContext ctx = KotlinTypeAnalysisContext.from(ast);
        assertTrue(ctx.callsOnReceiver("MyList").isEmpty(),
                "callsOnReceiver by alias name must return empty (ktm stores expanded type)");
        assertFalse(ctx.callsOnReceiverExpandingAlias("MyList").isEmpty(),
                "callsOnReceiverExpandingAlias must find calls by expanding the alias");
    }
}
