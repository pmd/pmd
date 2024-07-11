/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.ast;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.mutable.MutableInt;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTExecutableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.ast.InternalApiBridge;
import net.sourceforge.pmd.lang.java.ast.JavaVisitorBase;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterOwnerSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolResolver;


/**
 * Populates symbols on declaration nodes. Cannot be reused.
 */
final class AstSymbolMakerVisitor extends JavaVisitorBase<AstSymFactory, Void> {

    private static final String NO_CANONICAL_NAME = "<impossible/name>";

    // these map simple name to count of local classes with that name in the given class
    private final Deque<Map<String, Integer>> currentLocalIndices = new ArrayDeque<>();
    // these are counts of anon classes in the enclosing class
    private final Deque<MutableInt> anonymousCounters = new ArrayDeque<>();
    // these are binary names, eg may contain pack.Foo, pack.Foo$Nested, pack.Foo$Nested$1Local
    private final Deque<String> enclosingBinaryNames = new ArrayDeque<>();
    // these are canonical names. Contains NO_CANONICAL_NAME if the enclosing decl has no canonical name
    private final Deque<String> enclosingCanonicalNames = new ArrayDeque<>();
    // these are symbols, NOT 1-to-1 with the type name stack because may contain method/ctor symbols
    private final Deque<JTypeParameterOwnerSymbol> enclosingSymbols = new ArrayDeque<>();

    /** Package name of the current file. */
    private final String packageName;

    private final Map<String, JClassSymbol> byCanonicalName = new HashMap<>();
    private final Map<String, JClassSymbol> byBinaryName = new HashMap<>();

    AstSymbolMakerVisitor(ASTCompilationUnit node) {
        // update the package list
        packageName = node.getPackageName();
    }

    public SymbolResolver makeKnownSymbolResolver() {
        return new MapSymResolver(byCanonicalName, byBinaryName);
    }

    @Override
    public Void visit(ASTVariableId node, AstSymFactory data) {

        if (isTrueLocalVar(node)) {
            data.setLocalVarSymbol(node);
        } else {
            // in the other cases, building the method/ctor/class symbols already set the symbols
            assert node.getSymbol() != null : "Symbol was null for " + node;
        }

        return super.visit(node, data);
    }

    private boolean isTrueLocalVar(ASTVariableId node) {
        return !(node.isField()
            || node.isEnumConstant()
            || node.isRecordComponent()
            || node.getParent() instanceof ASTFormalParameter);
    }

    @Override
    public Void visit(ASTCompilationUnit node, AstSymFactory data) {
        if (node.isImplicitlyDeclaredClass()) {
            JClassSymbol sym = data.setClassSymbol(node);
            enclosingSymbols.push(sym);
            visitChildren(node, data);
            enclosingSymbols.pop();
            return null;
        }
        return super.visit(node, data);
    }

    @Override
    public Void visitTypeDecl(ASTTypeDeclaration node, AstSymFactory data) {
        String binaryName = makeBinaryName(node);
        @Nullable String canonicalName = makeCanonicalName(node, binaryName);
        InternalApiBridge.setQname(node, binaryName, canonicalName);
        JClassSymbol sym = data.setClassSymbol(enclosingSymbols.peek(), node);

        byBinaryName.put(binaryName, sym);
        if (canonicalName != null) {
            byCanonicalName.put(canonicalName, sym);
        }

        enclosingBinaryNames.push(binaryName);
        enclosingCanonicalNames.push(canonicalName == null ? NO_CANONICAL_NAME : canonicalName);
        enclosingSymbols.push(sym);
        anonymousCounters.push(new MutableInt(0));
        currentLocalIndices.push(new HashMap<>());

        visitChildren(node, data);

        currentLocalIndices.pop();
        anonymousCounters.pop();
        enclosingSymbols.pop();
        enclosingBinaryNames.pop();
        enclosingCanonicalNames.pop();

        return null;
    }

    @NonNull
    private String makeBinaryName(ASTTypeDeclaration node) {
        String simpleName = node.getSimpleName();
        if (node.isLocal()) {
            simpleName = getNextIndexFromHistogram(currentLocalIndices.getFirst(), node.getSimpleName(), 1)
                + simpleName;
        } else if (node.isAnonymous()) {
            simpleName = "" + anonymousCounters.getFirst().incrementAndGet();
        }

        String enclosing = enclosingBinaryNames.peek();
        return enclosing != null ? enclosing + "$" + simpleName
                                 : packageName.isEmpty() ? simpleName
                                                         : packageName + "." + simpleName;
    }

    @Nullable
    private String makeCanonicalName(ASTTypeDeclaration node, String binaryName) {
        if (node.isAnonymous() || node.isLocal()) {
            return null;
        }

        if (enclosingCanonicalNames.isEmpty()) {
            // toplevel
            return binaryName;
        }

        String enclCanon = enclosingCanonicalNames.getFirst();
        return NO_CANONICAL_NAME.equals(enclCanon)
               ? null  // enclosing has no canonical name, so this one doesn't either
               : enclCanon + '.' + node.getSimpleName();

    }

    @Override
    public Void visitMethodOrCtor(ASTExecutableDeclaration node, AstSymFactory data) {
        enclosingSymbols.push(node.getSymbol());
        visitChildren(node, data);
        enclosingSymbols.pop();
        return null;
    }


    /**
     * Gets the next available index based on a key and a histogram (map of keys to int counters).
     * If the key doesn't exist, we add a new entry with the startIndex.
     *
     * <p>Used for lambda and anonymous class counters
     *
     * @param histogram  The histogram map
     * @param key        The key to access
     * @param startIndex First index given out when the key doesn't exist
     *
     * @return The next free index
     */
    private static <T> int getNextIndexFromHistogram(Map<T, Integer> histogram, T key, int startIndex) {
        Integer count = histogram.get(key);
        if (count == null) {
            histogram.put(key, startIndex);
            return startIndex;
        } else {
            histogram.put(key, count + 1);
            return count + 1;
        }
    }

}
