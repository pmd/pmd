/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.xpath.internal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.stokpop.typemapper.model.CallSiteAst;
import nl.stokpop.typemapper.model.DeclarationAst;
import nl.stokpop.typemapper.model.FileAst;
import nl.stokpop.typemapper.model.TypeNameUtilsKt;
import nl.stokpop.typemapper.model.TypedAst;
import nl.stokpop.typemapper.model.UnresolvedReferenceAst;

/**
 * Holds pre-analyzed Kotlin type information from kotlin-type-mapper, indexed by
 * (absolute file path, line number) for fast lookup during XPath function evaluation.
 */
public final class KotlinTypeAnalysisContext {

    private static final String KT_EXTENSION = ".kt";

    private static final KotlinTypeAnalysisContext EMPTY = new KotlinTypeAnalysisContext(
            Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(),
            Collections.emptyMap());

    /** Map from absolute file path -> line -> list of call sites on that line. */
    private final Map<String, Map<Integer, List<CallSiteAst>>> callIndex;

    /** Map from absolute file path -> line -> list of declarations starting on that line. */
    private final Map<String, Map<Integer, List<DeclarationAst>>> declIndex;

    /**
     * Direct supertypes per Kotlin FQN (generics stripped), from {@link TypedAst#getTypeHierarchy()}.
     * Key: raw type FQN. Value: list of direct supertype FQNs.
     * Used by {@link #isSubtypeOf(String, String)} for hierarchy traversal.
     */
    private final Map<String, List<String>> typeHierarchy;

    /** Map from absolute file path -> line -> list of unresolved references on that line. */
    private final Map<String, Map<Integer, List<UnresolvedReferenceAst>>> unresolvedIndex;

    private KotlinTypeAnalysisContext(
            Map<String, Map<Integer, List<CallSiteAst>>> callIndex,
            Map<String, Map<Integer, List<DeclarationAst>>> declIndex,
            Map<String, List<String>> typeHierarchy,
            Map<String, Map<Integer, List<UnresolvedReferenceAst>>> unresolvedIndex) {
        this.callIndex = callIndex;
        this.declIndex = declIndex;
        this.typeHierarchy = typeHierarchy;
        this.unresolvedIndex = unresolvedIndex;
    }

    /** Returns a no-op context (all lookups return empty lists). */
    public static KotlinTypeAnalysisContext empty() {
        return EMPTY;
    }

    /** Builds a context from a {@link TypedAst}, indexing all call sites, declarations, and unresolved references. */
    public static KotlinTypeAnalysisContext from(TypedAst ast) {
        Map<String, Map<Integer, List<CallSiteAst>>> callIdx = new HashMap<>();
        Map<String, Map<Integer, List<DeclarationAst>>> declIdx = new HashMap<>();
        Map<String, Map<Integer, List<UnresolvedReferenceAst>>> unresolvedIdx = new HashMap<>();

        String sourceRoot = ast.getSourceRoot();
        for (FileAst file : ast.getFiles()) {
            String absPath = canonicalize(sourceRoot + File.separator + file.getRelativePath());
            // Also index by basename alone as a fallback for when PMD paths differ
            // from the paths kotlin-type-mapper was run on (e.g. temp dir analysis).
            String basename = new File(file.getRelativePath()).getName();

            for (CallSiteAst call : file.getCalls()) {
                addToIndex(callIdx, absPath, basename, call.getLine(), call);
            }
            for (DeclarationAst decl : file.getDeclarations()) {
                addToIndex(declIdx, absPath, basename, decl.getLine(), decl);
            }
            for (UnresolvedReferenceAst unresolved : file.getUnresolvedReferences()) {
                addToIndex(unresolvedIdx, absPath, basename, unresolved.getLine(), unresolved);
            }
        }
        return new KotlinTypeAnalysisContext(callIdx, declIdx, ast.getTypeHierarchy(), unresolvedIdx);
    }

    private static <T> void addToIndex(Map<String, Map<Integer, List<T>>> idx,
            String absPath, String basename, int line, T item) {
        idx.computeIfAbsent(absPath, k -> new HashMap<>())
                .computeIfAbsent(line, k -> new ArrayList<>())
                .add(item);
        idx.computeIfAbsent(basename, k -> new HashMap<>())
                .computeIfAbsent(line, k -> new ArrayList<>())
                .add(item);
    }

    /**
     * Returns call sites recorded at the given file and line.
     * If the exact line has no entries, also checks line +/- 1 to tolerate minor
     * line-number differences between PMD's ANTLR parser and kotlin-type-mapper's PSI.
     */
    public List<CallSiteAst> callSitesAt(String absFilePath, int line) {
        return lookupByLine(callIndex, absFilePath, line);
    }

    /**
     * Returns all call sites on any line in [{@code beginLine}, {@code endLine}] for the given file.
     * Uses the same basename/extension fallback as {@link #callSitesAt}.
     * Used for multi-line expressions where the method call may be on a different line
     * than the start of the expression (e.g. chained calls split across lines).
     */
    public List<CallSiteAst> callSitesInRange(String absFilePath, int beginLine, int endLine) {
        if (beginLine == endLine) {
            return lookupByLine(callIndex, absFilePath, beginLine);
        }
        Map<Integer, List<CallSiteAst>> byLine = resolveByLineMap(callIndex, absFilePath);
        if (byLine == null) {
            return Collections.emptyList();
        }
        // Multi-line: collect all call sites across the entire range
        List<CallSiteAst> result = new ArrayList<>();
        for (int line = beginLine; line <= endLine; line++) {
            List<CallSiteAst> sites = byLine.get(line);
            if (sites != null) {
                result.addAll(sites);
            }
        }
        return result;
    }

    /**
     * Returns declarations recorded at the given file and line.
     * Also checks line +/- 1 as a fallback.
     */
    public List<DeclarationAst> declarationsAt(String absFilePath, int line) {
        return lookupByLine(declIndex, absFilePath, line);
    }

    /**
     * Returns unresolved references recorded at the given file and line.
     * Also checks line +/- 1 as a fallback.
     */
    public List<UnresolvedReferenceAst> unresolvedReferencesAt(String absFilePath, int line) {
        return lookupByLine(unresolvedIndex, absFilePath, line);
    }

    private static <T> List<T> lookupByLine(
            Map<String, Map<Integer, List<T>>> index, String absFilePath, int line) {
        Map<Integer, List<T>> byLine = resolveByLineMap(index, absFilePath);
        if (byLine == null) {
            return Collections.emptyList();
        }
        List<T> exact = byLine.get(line);
        if (exact != null && !exact.isEmpty()) {
            return exact;
        }
        List<T> result = new ArrayList<>();
        List<T> prev = byLine.get(line - 1);
        List<T> next = byLine.get(line + 1);
        if (prev != null) {
            result.addAll(prev);
        }
        if (next != null) {
            result.addAll(next);
        }
        return result;
    }

    private static <T> Map<Integer, List<T>> resolveByLineMap(
            Map<String, Map<Integer, List<T>>> index, String absFilePath) {
        Map<Integer, List<T>> byLine = index.get(absFilePath);
        if (byLine != null) {
            return byLine;
        }
        String basename = new File(absFilePath).getName();
        byLine = index.get(basename);
        if (byLine == null && !basename.endsWith(KT_EXTENSION)) {
            // PmdRuleTst uses synthetic ids without .kt; temp files are written with .kt appended
            byLine = index.get(basename + KT_EXTENSION);
        }
        return byLine;
    }

    /**
     * Returns the type hierarchy map (Kotlin FQN -> direct supertype FQNs).
     * Primarily for testing; empty when no aux classpath was provided.
     */
    public Map<String, List<String>> getTypeHierarchy() {
        return typeHierarchy;
    }

    /**
     * Returns true if {@code expected} and {@code actual} refer to the same type,
     * accounting for Java<->Kotlin name mapping (e.g. java.lang.String <-> kotlin.String).
     */
    public boolean isTypeEquivalent(String expected, String actual) {
        return TypeNameUtilsKt.typeNamesEquivalent(expected, actual);
    }

    /**
     * Returns true if {@code actualType} is the same as, or a (transitive) subtype of,
     * {@code expectedType}. Uses the type hierarchy built by kotlin-type-mapper via reflection.
     *
     * <p>Both Java FQCNs and Kotlin FQNs are accepted for {@code expectedType}. Java<->Kotlin
     * name equivalence is applied when comparing each type against {@code expectedType}.
     *
     * <p>Falls back to {@link #isTypeEquivalent} when no hierarchy data is available
     * (i.e. the aux classpath was not set or the type could not be loaded).
     */
    public boolean isSubtypeOf(String expectedType, String actualType) {
        // First: exact/mapped equivalence check.
        if (isTypeEquivalent(expectedType, actualType)) {
            return true;
        }
        if (typeHierarchy.isEmpty()) {
            return false;
        }
        // BFS over transitive supertypes of actualType.
        String rawActual = substringBeforeAngle(actualType);
        Set<String> visited = new HashSet<>();
        Deque<String> queue = new ArrayDeque<>();
        queue.add(rawActual);
        while (!queue.isEmpty()) {
            String current = queue.poll();
            if (!visited.add(current)) {
                continue;
            }
            List<String> supers = typeHierarchy.get(current);
            if (supers == null) {
                continue;
            }
            for (String superType : supers) {
                if (isTypeEquivalent(expectedType, superType)) {
                    return true;
                }
                queue.add(substringBeforeAngle(superType));
            }
        }
        return false;
    }

    private static String substringBeforeAngle(String s) {
        // Strip trailing '?' (nullable marker) before extracting the raw type name,
        // so that bare nullable types like "java.util.List?" are handled correctly.
        String stripped = s.endsWith("?") ? s.substring(0, s.length() - 1) : s;
        int idx = stripped.indexOf('<');
        return idx >= 0 ? stripped.substring(0, idx) : stripped;
    }

    private static String canonicalize(String path) {
        try {
            return new File(path).getCanonicalPath();
        } catch (IOException e) {
            return new File(path).getAbsolutePath();
        }
    }
}
