/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.resolver;

/**
 * A lexical scope corresponding to a Modelica class.
 */
public final class ModelicaClassScope extends AbstractModelicaScope {
    private final ModelicaClassDeclaration classDeclaration;

    ModelicaClassScope(ModelicaClassDeclaration declaration) {
        classDeclaration = declaration;
        classDeclaration.setOwnScope(this);
    }

    public ModelicaClassType getClassDeclaration() {
        return classDeclaration;
    }

    @Override
    public void resolveLexically(ResolutionContext result, CompositeName name) throws Watchdog.CountdownException {
        InternalModelicaResolverApi.resolveFurtherNameComponents(classDeclaration, result, name);
        if (classDeclaration.isEncapsulated()) {
            getRoot().resolveBuiltin(result, name);
        } else {
            ((AbstractModelicaScope) getParent()).resolveLexically(result, name);
        }
    }

    @Override
    public String getRepresentation() {
        return "Class:" + classDeclaration.getSimpleTypeName();
    }

    String getFullyQualifiedClassName() {
        if (getParent() instanceof ModelicaClassScope) {
            return ((ModelicaClassScope) getParent()).getFullyQualifiedClassName() + "." + classDeclaration.getSimpleTypeName();
        } else {
            return ((ModelicaSourceFileScope) getParent()).getFileFQCN();
        }
    }
}
