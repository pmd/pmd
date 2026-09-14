/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.internal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.stokpop.typemapper.analyzer.KotlinTypeMapper;
import nl.stokpop.typemapper.model.CallSiteAst;
import nl.stokpop.typemapper.model.DeclarationAst;
import nl.stokpop.typemapper.model.DeclarationKind;
import nl.stokpop.typemapper.model.FileAst;
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

    @Test
    void fromSkipsFileWhoseAbsolutePathCannotBeCanonicalized() {
        // One file has a relativePath too long for getCanonicalPath() to resolve
        // (throws IOException: File name too long) -- from() must not let this abort
        // building the whole index; it must skip only that file and keep the other.
        DeclarationAst goodDecl = new DeclarationAst(
                DeclarationKind.PROPERTY, "x", "pkg.x", "pkg",
                null, null, Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList(),
                1, 1, 0, 0, Collections.emptyList());
        FileAst goodFile = new FileAst(
                "Good.kt", "pkg", Collections.singletonList(goodDecl),
                Collections.emptyList(), Collections.emptyList(), "", Collections.emptyList());
        String tooLongName = repeat("a", 5000) + ".kt";
        FileAst badFile = new FileAst(
                tooLongName, "pkg", Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList(), "", Collections.emptyList());
        TypedAst ast = new TypedAst(
                "2.0", "test", "/tmp/does-not-need-to-exist",
                Arrays.asList(goodFile, badFile), Collections.emptyMap());

        // from() logs the skip at ERROR level (by design, see production code comment) --
        // capture stderr around the call so the expected log line doesn't show up as build
        // noise, while still asserting it was actually emitted.
        PrintStream originalErr = System.err;
        ByteArrayOutputStream capturedErr = new ByteArrayOutputStream();
        System.setErr(new PrintStream(capturedErr, true));
        KotlinTypeAnalysisContext result;
        try {
            result = assertDoesNotThrow(() -> KotlinTypeAnalysisContext.from(ast));
        } finally {
            System.setErr(originalErr);
        }

        assertEquals(1, result.declarationsAt("/tmp/does-not-need-to-exist/Good.kt", 1).size());
        String logged = capturedErr.toString();
        assertTrue(logged.contains("Skipping type info for"),
                "Expected the skip to be logged at ERROR level, got: " + logged);
    }

    private static String repeat(String s, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(s);
        }
        return sb.toString();
    }

}
