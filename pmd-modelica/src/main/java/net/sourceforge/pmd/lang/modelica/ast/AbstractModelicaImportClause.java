/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast;

import net.sourceforge.pmd.lang.modelica.resolver.ModelicaDeclaration;
import net.sourceforge.pmd.lang.modelica.resolver.ModelicaScope;
import net.sourceforge.pmd.lang.modelica.resolver.ResolutionContext;
import net.sourceforge.pmd.lang.modelica.resolver.ResolutionResult;
import net.sourceforge.pmd.lang.modelica.resolver.ResolutionState;
import net.sourceforge.pmd.lang.modelica.resolver.Watchdog;

/**
 * Common internal machinery for various import clauses to describe themselves to resolver.
 */
abstract class AbstractModelicaImportClause extends AbstractModelicaNode implements ModelicaImportClause {
    private ResolutionResult<ModelicaDeclaration> importSourcesCache;

    AbstractModelicaImportClause(int id) {
        super(id);
    }

    AbstractModelicaImportClause(ModelicaParser parser, int id) {
        super(parser, id);
    }

    /**
     * Some import clauses are considered "qualified", some "unqualified", the former being processed first
     * while looking up some name. See "5.3.1 Simple Name Lookup" from MLS 3.4.
     *
     * @return Whether this kind of import is considered "qualified"
     */
    abstract boolean isQualified();

    /**
     * A template method to be used by {@link #resolveSimpleName}. Usually used to fetch the lexically referenced
     * class in the corresponding import statement.
     */
    protected abstract ResolutionResult<ModelicaDeclaration> getCacheableImportSources(ResolutionState state, ModelicaScope scope);

    /**
     * A template method to be used by {@link #resolveSimpleName}. Usually used to try to fetch declarations for
     * <code>simpleName</code> from particular <i>source</i> returned by {@link #getCacheableImportSources}.
     */
    protected abstract void fetchImportedClassesFromSource(ResolutionContext result, ModelicaDeclaration source, String simpleName) throws Watchdog.CountdownException;

    /**
     * Tries to resolve the specified name via this import clause.
     *
     * @param result     Resolution context
     * @param simpleName Name to resolve
     * @throws Watchdog.CountdownException if too many resolution steps were performed
     */
    final void resolveSimpleName(ResolutionContext result, String simpleName) throws Watchdog.CountdownException {
        // No need to re-resolve if already resolved successfully
        if (importSourcesCache == null || importSourcesCache.wasTimedOut()) {
            importSourcesCache = getCacheableImportSources(result.getState(), getMostSpecificScope().getParent());
        }
        for (ModelicaDeclaration source : importSourcesCache.getBestCandidates()) {
            fetchImportedClassesFromSource(result, source, simpleName);
        }
        result.markHidingPoint();
        for (ModelicaDeclaration source : importSourcesCache.getHiddenCandidates()) {
            fetchImportedClassesFromSource(result, source, simpleName);
        }
    }
}
