/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import java.util.ArrayDeque;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.InternalApiBridge;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.internal.JavaAstProcessor;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolResolver;


/**
 * Object passing around config for {@link AbstractSymbolTable}.
 *
 * @since 7.0.0
 */
final class SymbolTableHelper {

    private final String thisPackage;
    private final JavaAstProcessor processor;

    // this will be used later
    private final ArrayDeque<JClassSymbol> contextType = new ArrayDeque<>(2);


    SymbolTableHelper(String thisPackage, JavaAstProcessor processor) {
        assert thisPackage != null;
        assert processor != null;
        this.thisPackage = thisPackage;
        this.processor = processor;
    }

    public void earlyDisambig(NodeStream<? extends JavaNode> nodes) {
        InternalApiBridge.disambig(processor, nodes);
    }

    void pushCtxType(JClassSymbol t) {
        assert !t.isArray() && !t.isPrimitive();
        contextType.push(t);
    }

    void popCtxType() {
        contextType.pop();
    }

    /** Prepend the package name, handling empty package. */
    String prependPackageName(String name) {
        return thisPackage.isEmpty() ? name : thisPackage + "." + name;
    }


    public JClassSymbol findSymbolCannotFail(String name) {
        JClassSymbol found = processor.getSymResolver().resolveClassFromCanonicalName(name);
        return found == null ? processor.makeUnresolvedReference(name, 0)
                             : found;
    }

    /** @see SymbolResolver#resolveClassFromCanonicalName(String) */
    @Nullable
    JClassSymbol loadClassOrFail(String fqcn) {
        return processor.getSymResolver().resolveClassFromCanonicalName(fqcn);
    }

    SemanticChecksLogger getLogger() {
        return processor.getLogger();
    }

    public String getThisPackage() {
        return thisPackage;
    }
}
