/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast;

import net.sourceforge.pmd.lang.modelica.resolver.ModelicaDeclaration;
import net.sourceforge.pmd.lang.modelica.resolver.ModelicaScope;
import net.sourceforge.pmd.lang.modelica.resolver.ResolutionContext;
import net.sourceforge.pmd.lang.modelica.resolver.ResolutionResult;
import net.sourceforge.pmd.lang.modelica.resolver.ResolutionState;

public class ASTSingleDefinitionImportClause extends AbstractModelicaImportClause {
    private ASTName importWhat;
    private String importedName;

    ASTSingleDefinitionImportClause(int id) {
        super(id);
    }

    ASTSingleDefinitionImportClause(ModelicaParser p, int id) {
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
        importedName = importWhat.getChild(importWhat.getNumChildren() - 1).getImage();
    }

    @Override
    protected ResolutionResult<ModelicaDeclaration> getCacheableImportSources(ResolutionState state, ModelicaScope scope) {
        return scope.safeResolveLexically(ModelicaDeclaration.class, state, importWhat.getCompositeName());
    }

    @Override
    protected void fetchImportedClassesFromSource(ResolutionContext result, ModelicaDeclaration source, String simpleName) {
        if (importedName.equals(simpleName)) {
            result.addCandidate(source);
        }
    }

    @Override
    public boolean isQualified() {
        return true;
    }
}
