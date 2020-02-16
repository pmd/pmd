/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast;

import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pmd.lang.modelica.resolver.CompositeName;
import net.sourceforge.pmd.lang.modelica.resolver.InternalModelicaResolverApi;
import net.sourceforge.pmd.lang.modelica.resolver.ModelicaDeclaration;
import net.sourceforge.pmd.lang.modelica.resolver.ModelicaScope;
import net.sourceforge.pmd.lang.modelica.resolver.ResolutionContext;
import net.sourceforge.pmd.lang.modelica.resolver.ResolutionResult;
import net.sourceforge.pmd.lang.modelica.resolver.ResolutionState;
import net.sourceforge.pmd.lang.modelica.resolver.Watchdog;

public final class ASTMultipleDefinitionImportClause extends AbstractModelicaImportClause {
    private ASTName importFrom;
    private Set<String> importedNames = new HashSet<>();

    ASTMultipleDefinitionImportClause(int id) {
        super(id);
    }

    ASTMultipleDefinitionImportClause(ModelicaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(ModelicaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public void jjtClose() {
        super.jjtClose();
        importFrom = getFirstChildOfType(ASTName.class);
        ASTImportList importList = getFirstChildOfType(ASTImportList.class);
        for (int i = 0; i < importList.getNumChildren(); ++i) {
            ASTSimpleName namePart = (ASTSimpleName) importList.getChild(i);
            importedNames.add(namePart.getImage());
        }
    }

    @Override
    protected ResolutionResult<ModelicaDeclaration> getCacheableImportSources(ResolutionState state, ModelicaScope scope) {
        return scope.safeResolveLexically(ModelicaDeclaration.class, state, importFrom.getCompositeName());
    }

    @Override
    protected void fetchImportedClassesFromSource(ResolutionContext result, ModelicaDeclaration source, String simpleName) throws Watchdog.CountdownException {
        if (importedNames.contains(simpleName)) {
            InternalModelicaResolverApi.resolveFurtherNameComponents(source, result, CompositeName.create(simpleName));
        }
    }

    @Override
    public boolean isQualified() {
        return true;
    }
}
