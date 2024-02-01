/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.modelica.resolver.ModelicaClassType;
import net.sourceforge.pmd.lang.modelica.resolver.ModelicaScope;
import net.sourceforge.pmd.lang.modelica.resolver.internal.ResolutionContext;
import net.sourceforge.pmd.lang.modelica.resolver.internal.Watchdog;

/**
 * Internal API.
 *
 * <p>Acts as a bridge between outer parts of PMD and the restricted access
 * internal API of this package.
 *
 * <p><b>None of this is published API, and compatibility can be broken anytime!</b>
 * Use this only at your own risk.
 *
 * @apiNote Internal API
 */
@InternalApi
public final class InternalApiBridge {
    private InternalApiBridge() {}

    public static void setNodeOwnScope(ModelicaNode node, ModelicaScope scope) {
        ((AbstractModelicaNode) node).setOwnScope(scope);
    }

    public static boolean isQualifiedImport(ModelicaImportClause importClause) {
        return ((AbstractModelicaImportClause) importClause).isQualified();
    }

    public static void resolveImportedSimpleName(ModelicaImportClause importClause, ResolutionContext result, String simpleName) throws Watchdog.CountdownException {
        ((AbstractModelicaImportClause) importClause).resolveSimpleName(result, simpleName);
    }

    public static void populateExtendsAndImports(ModelicaClassSpecifierNode classNode, ModelicaClassType classTypeDeclaration) {
        ((AbstractModelicaClassSpecifierNode) classNode).populateExtendsAndImports(classTypeDeclaration);
    }
}
