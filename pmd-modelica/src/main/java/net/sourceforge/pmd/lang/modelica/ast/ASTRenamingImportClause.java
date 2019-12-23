/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast;

import net.sourceforge.pmd.lang.modelica.resolver.ModelicaDeclaration;
import net.sourceforge.pmd.lang.modelica.resolver.ModelicaScope;
import net.sourceforge.pmd.lang.modelica.resolver.ResolutionContext;
import net.sourceforge.pmd.lang.modelica.resolver.ResolutionResult;
import net.sourceforge.pmd.lang.modelica.resolver.ResolutionState;

public final class ASTRenamingImportClause extends AbstractModelicaImportClause {
    private ASTName importWhat;
    private String renamedTo;

    ASTRenamingImportClause(int id) {
        super(id);
    }

    ASTRenamingImportClause(ModelicaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(ModelicaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public void jjtClose() {
        super.jjtClose();

        importWhat = getFirstChildOfType(ASTName.class);
        renamedTo = getFirstChildOfType(ASTSimpleName.class).getImage();
    }

    @Override
    protected ResolutionResult<ModelicaDeclaration> getCacheableImportSources(ResolutionState state, ModelicaScope scope) {
        return scope.safeResolveLexically(ModelicaDeclaration.class, state, importWhat.getCompositeName());
    }

    @Override
    protected void fetchImportedClassesFromSource(ResolutionContext result, ModelicaDeclaration source, String simpleName) {
        if (renamedTo.equals(simpleName)) {
            result.addCandidate(source);
        }
    }

    @Override
    public boolean isQualified() {
        return true;
    }
}
