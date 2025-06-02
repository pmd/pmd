/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast;

import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pmd.lang.modelica.resolver.CompositeName;
import net.sourceforge.pmd.lang.modelica.resolver.InternalApiBridge;
import net.sourceforge.pmd.lang.modelica.resolver.ModelicaDeclaration;
import net.sourceforge.pmd.lang.modelica.resolver.ModelicaScope;
import net.sourceforge.pmd.lang.modelica.resolver.ResolutionResult;
import net.sourceforge.pmd.lang.modelica.resolver.internal.ResolutionContext;
import net.sourceforge.pmd.lang.modelica.resolver.internal.ResolutionState;
import net.sourceforge.pmd.lang.modelica.resolver.internal.Watchdog;

public final class ASTMultipleDefinitionImportClause extends AbstractModelicaImportClause {
    private ASTName importFrom;
    private Set<String> importedNames = new HashSet<>();

    ASTMultipleDefinitionImportClause(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptModelicaVisitor(ModelicaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    @Override
    public void jjtClose() {
        super.jjtClose();
        importFrom = firstChild(ASTName.class);
        ASTImportList importList = firstChild(ASTImportList.class);
        for (int i = 0; i < importList.getNumChildren(); ++i) {
            ASTSimpleName namePart = (ASTSimpleName) importList.getChild(i);
            importedNames.add(namePart.getName());
        }
    }

    @Override
    protected ResolutionResult<ModelicaDeclaration> getCacheableImportSources(ResolutionState state, ModelicaScope scope) {
        return scope.safeResolveLexically(ModelicaDeclaration.class, state, importFrom.getCompositeName());
    }

    @Override
    protected void fetchImportedClassesFromSource(ResolutionContext result, ModelicaDeclaration source, String simpleName) throws Watchdog.CountdownException {
        if (importedNames.contains(simpleName)) {
            InternalApiBridge.resolveFurtherNameComponents(source, result, CompositeName.create(simpleName));
        }
    }

    @Override
    public boolean isQualified() {
        return true;
    }
}
