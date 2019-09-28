/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.modelica.resolver.ModelicaClassType;
import net.sourceforge.pmd.lang.modelica.resolver.ModelicaScope;
import net.sourceforge.pmd.lang.modelica.resolver.ResolutionContext;
import net.sourceforge.pmd.lang.modelica.resolver.Watchdog;

@InternalApi
public final class InternalModelicaNodeApi {
    private InternalModelicaNodeApi() {}

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
