/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.resolver;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.modelica.ast.ASTStoredDefinition;
import net.sourceforge.pmd.lang.modelica.ast.ModelicaImportClause;
import net.sourceforge.pmd.lang.modelica.ast.Visibility;
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

    public static void addImportToClass(ModelicaClassType classTypeDeclaration, Visibility visibility, ModelicaImportClause clause) {
        ((ModelicaClassDeclaration) classTypeDeclaration).addImport(visibility, clause);
    }

    public static void addExtendToClass(ModelicaClassType classTypeDeclaration, Visibility visibility, CompositeName extendedClass) {
        ((ModelicaClassDeclaration) classTypeDeclaration).addExtends(visibility, extendedClass);
    }

    public static void resolveFurtherNameComponents(ModelicaDeclaration declaration, ResolutionContext result, CompositeName name) throws Watchdog.CountdownException {
        ((AbstractModelicaDeclaration) declaration).resolveFurtherNameComponents(result, name);
    }

    public static final class ModelicaSymbolFacade {
        private ModelicaSymbolFacade() {}

        public static void process(ASTStoredDefinition node) {
            ScopeAndDeclarationFinder sc = new ScopeAndDeclarationFinder();
            node.acceptVisitor(sc, null);
        }
    }
}
