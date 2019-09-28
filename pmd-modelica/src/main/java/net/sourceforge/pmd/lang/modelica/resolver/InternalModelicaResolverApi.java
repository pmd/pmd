/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.resolver;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.modelica.ast.ModelicaImportClause;
import net.sourceforge.pmd.lang.modelica.ast.Visibility;

@InternalApi
public final class InternalModelicaResolverApi {
    private InternalModelicaResolverApi() {}

    public static void addImportToClass(ModelicaClassType classTypeDeclaration, Visibility visibility, ModelicaImportClause clause) {
        ((ModelicaClassDeclaration) classTypeDeclaration).addImport(visibility, clause);
    }

    public static void addExtendToClass(ModelicaClassType classTypeDeclaration, Visibility visibility, CompositeName extendedClass) {
        ((ModelicaClassDeclaration) classTypeDeclaration).addExtends(visibility, extendedClass);
    }

    public static void resolveFurtherNameComponents(ModelicaDeclaration declaration, ResolutionContext result, CompositeName name) throws Watchdog.CountdownException {
        ((AbstractModelicaDeclaration) declaration).resolveFurtherNameComponents(result, name);
    }
}
