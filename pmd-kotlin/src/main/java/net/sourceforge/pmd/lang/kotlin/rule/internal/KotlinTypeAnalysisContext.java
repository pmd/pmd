/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.internal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.annotation.Experimental;

import nl.stokpop.typemapper.model.CallSiteAst;
import nl.stokpop.typemapper.model.DeclarationAst;
import nl.stokpop.typemapper.model.FileAst;
import nl.stokpop.typemapper.model.TypeNameUtilsKt;
import nl.stokpop.typemapper.model.TypedAst;
import nl.stokpop.typemapper.model.TypedAstCallQueriesKt;
import nl.stokpop.typemapper.model.TypedAstHierarchyQueriesKt;
import nl.stokpop.typemapper.model.TypedAstTypeAliasQueriesKt;
import nl.stokpop.typemapper.model.UnresolvedReferenceAst;

/**
 * Holds pre-analyzed Kotlin type information from kotlin-type-mapper, indexed by
 * (absolute file path, line number) for fast lookup during XPath function evaluation.
 *
 * <p>Note: kotlin-type-mapper records the <em>concrete expanded type</em> in all call-site
 * fields -- type alias names are not preserved. Use {@link #resolveTypeAlias(String)} to
 * expand an alias to its concrete type, or use the {@code ...ExpandingAlias} variants of
 * {@link #callsOnReceiver(String)} and {@link #callsReturning(String)}.
 *
 * @since 7.27.0
 * @experimental
 */
@Experimental
public final class KotlinTypeAnalysisContext {

    private static final KotlinTypeAnalysisContext EMPTY = new KotlinTypeAnalysisContext(
            null,
            Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap());

    /** Map from absolute file path -> line -> list of call sites on that line. */
    private final Map<String, Map<Integer, List<CallSiteAst>>> callIndex;

    /** Map from absolute file path -> line -> list of declarations starting on that line. */
    private final Map<String, Map<Integer, List<DeclarationAst>>> declIndex;

    /** Map from absolute file path -> line -> list of unresolved references on that line. */
    private final Map<String, Map<Integer, List<UnresolvedReferenceAst>>> unresolvedIndex;

    /**
     * The full kotlin-type-mapper AST. Null only for {@link #empty()}.
     * Used to delegate hierarchy queries ({@link #isSubtypeOf}) to ktm's built-in logic.
     */
    private final TypedAst typedAst;

    private KotlinTypeAnalysisContext(
            TypedAst typedAst,
            Map<String, Map<Integer, List<CallSiteAst>>> callIndex,
            Map<String, Map<Integer, List<DeclarationAst>>> declIndex,
            Map<String, Map<Integer, List<UnresolvedReferenceAst>>> unresolvedIndex) {
        this.typedAst = typedAst;
        this.callIndex = callIndex;
        this.declIndex = declIndex;
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

        boolean diskBased = ast.hasSourceRoot();
        // Fail fast on key clashes — two files must never map to the same index key.
        Set<String> seenKeys = new HashSet<>();
        for (FileAst file : ast.getFiles()) {
            // Disk: canonical abs path (PMD passes the same paths it gets from the FS).
            // In-memory (fromSources, single-file, tests): relativePath == the key the caller
            // used in the sources map, which is also the key PMD looks up with.
            String key = diskBased
                    ? canonicalize(ast.resolveAbsolutePath(file))
                    : file.getRelativePath();
            if (!seenKeys.add(key)) {
                throw new IllegalStateException(
                        "kotlin-type-mapper index clash: two files resolve to the same key \""
                        + key + "\". This is a bug in the analysis setup.");
            }
            for (CallSiteAst call : file.getCalls()) {
                addToIndex(callIdx, key, call.getLine(), call);
            }
            for (DeclarationAst decl : file.getDeclarations()) {
                addToIndex(declIdx, key, decl.getLine(), decl);
            }
            for (UnresolvedReferenceAst unresolved : file.getUnresolvedReferences()) {
                addToIndex(unresolvedIdx, key, unresolved.getLine(), unresolved);
            }
        }
        return new KotlinTypeAnalysisContext(ast, callIdx, declIdx, unresolvedIdx);
    }

    private static <T> void addToIndex(Map<String, Map<Integer, List<T>>> idx,
            String key, int line, T item) {
        idx.computeIfAbsent(key, k -> new HashMap<>())
                .computeIfAbsent(line, k -> new ArrayList<>())
                .add(item);
    }

    /**
     * Returns call sites recorded at the given file and line.
     * If the exact line has no entries, also checks line +/- 1 to tolerate minor
     * line-number differences between PMD's ANTLR parser and kotlin-type-mapper's PSI.
     *
     * <p>As of ktm 0.6.0, {@link CallSiteAst#getLine()} points to the callee name token
     * (e.g. {@code bar} in {@code foo.bar()}), not the receiver. {@link CallSiteAst#getEndLine()}
     * is also available (0 for single-line calls or ASTs from older JSON).
     */
    public List<CallSiteAst> callSitesAt(String absFilePath, int line) {
        return lookupByLine(callIndex, absFilePath, line);
    }

    /**
     * Returns all call sites on any line in [{@code beginLine}, {@code endLine}] for the given file.
     * Used for multi-line expressions where the method call may be on a different line
     * than the start of the expression (e.g. chained calls split across lines).
     *
     * <p>As of ktm 0.6.0, {@link CallSiteAst#getEndLine()} carries the closing-token line
     * and could be used for overlap checks; the current range-iteration strategy already covers
     * all practical cases without it.
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
        // +/-1 fallback: tolerates minor line-number differences between PMD's ANTLR parser
        // and ktm's PSI (e.g. annotations on a separate line from the declaration keyword).
        // ktm 0.6.0 improved line semantics so this fallback may become unnecessary once
        // test coverage across all Kotlin call and declaration patterns is broader.
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
        return index.get(absFilePath);
    }

    /**
     * Returns the type hierarchy map (Kotlin FQN -> direct supertype FQNs).
     * Primarily for testing; empty when no aux classpath was provided or for the empty context.
     */
    public Map<String, List<String>> getTypeHierarchy() {
        return typedAst != null ? typedAst.getTypeHierarchy() : Collections.emptyMap();
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
     * {@code expectedType}. Delegates to {@code TypedAst.isSubtypeOfUpward} from
     * kotlin-type-mapper (ktm 0.6.0+), which walks supertypes of {@code actualType} upward
     * through the type hierarchy -- O(ancestors) vs the downward BFS O(all subtypes).
     * Also short-circuits immediately for {@code java.lang.Object} / {@code kotlin.Any}.
     *
     * <p>Falls back to name-equivalence only when this is the empty context (no {@link TypedAst}).
     */
    public boolean isSubtypeOf(String expectedType, String actualType) {
        if (typedAst == null) {
            return TypeNameUtilsKt.typeNamesEquivalent(expectedType, actualType);
        }
        return TypedAstHierarchyQueriesKt.isSubtypeOfUpward(typedAst, expectedType, actualType);
    }

    /**
     * Resolves a typealias FQN to its concrete expanded type string, or {@code null}
     * if {@code fqn} is not a known typealias in the analyzed AST.
     * Delegates to {@link TypedAstTypeAliasQueriesKt#resolveTypeAlias(TypedAst, String)}.
     *
     * <p>Only finds aliases defined in the analyzed source files; library aliases resolve to
     * {@code null}. Returns {@code null} for the empty context.
     */
    public String resolveTypeAlias(String fqn) {
        if (typedAst == null) {
            return null;
        }
        return TypedAstTypeAliasQueriesKt.resolveTypeAlias(typedAst, fqn);
    }

    /**
     * Returns all call sites where the dispatch or extension receiver type matches {@code fqn}.
     * Delegates to {@code TypedAstCallQueriesKt.callsOnReceiver} from kotlin-type-mapper.
     * Returns empty list for the empty context.
     */
    public List<CallSiteAst> callsOnReceiver(String fqn) {
        if (typedAst == null) {
            return Collections.emptyList();
        }
        return TypedAstCallQueriesKt.callsOnReceiver(typedAst, fqn);
    }

    /**
     * Returns all call sites where the return type matches {@code fqn}.
     * Delegates to {@code TypedAstCallQueriesKt.callsReturning} from kotlin-type-mapper.
     * Returns empty list for the empty context.
     */
    public List<CallSiteAst> callsReturning(String fqn) {
        if (typedAst == null) {
            return Collections.emptyList();
        }
        return TypedAstCallQueriesKt.callsReturning(typedAst, fqn);
    }

    /**
     * Like {@link #callsOnReceiver(String)}, but if {@code fqn} is a known typealias it is
     * first expanded to its concrete type, then the call search uses that concrete type.
     * Falls back to the raw {@code fqn} if the alias is not found (library aliases, empty context).
     */
    public List<CallSiteAst> callsOnReceiverExpandingAlias(String fqn) {
        if (typedAst == null) {
            return Collections.emptyList();
        }
        String resolved = TypedAstTypeAliasQueriesKt.resolveTypeAlias(typedAst, fqn);
        return TypedAstCallQueriesKt.callsOnReceiver(typedAst, resolved != null ? resolved : fqn);
    }

    /**
     * Like {@link #callsReturning(String)}, but if {@code fqn} is a known typealias it is
     * first expanded to its concrete type, then the call search uses that concrete type.
     * Falls back to the raw {@code fqn} if the alias is not found (library aliases, empty context).
     */
    public List<CallSiteAst> callsReturningExpandingAlias(String fqn) {
        if (typedAst == null) {
            return Collections.emptyList();
        }
        String resolved = TypedAstTypeAliasQueriesKt.resolveTypeAlias(typedAst, fqn);
        return TypedAstCallQueriesKt.callsReturning(typedAst, resolved != null ? resolved : fqn);
    }

    private static String canonicalize(String path) {
        try {
            return new File(path).getCanonicalPath();
        } catch (IOException e) {
            return new File(path).getAbsolutePath();
        }
    }
}
