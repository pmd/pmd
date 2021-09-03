/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.resolver;

import net.sourceforge.pmd.lang.modelica.ast.ASTStoredDefinition;

/**
 * A scope corresponding to some specific Modelica source code file.
 */
public final class ModelicaSourceFileScope extends AbstractModelicaScope {
    private final String fileFQCN;
    private final String[] packageComponents;

    ModelicaSourceFileScope(ASTStoredDefinition node) {
        fileFQCN = node.getImage();
        if (fileFQCN.isEmpty()) {
            packageComponents = new String[0];
        } else {
            packageComponents = fileFQCN.split("\\.");
        }
    }

    /**
     * Resolve <code>name</code> as if not accounting for <code>fileFQCN</code> (as if it would be empty).
     */
    void lookupLocally(ResolutionContext result, CompositeName name) throws Watchdog.CountdownException {
        if (name.isEmpty()) {
            // TODO technically, here we should return an implicit package...
            return;
        }
        result.watchdogTick();

        String firstName = name.getHead();
        CompositeName furtherNames = name.getTail();

        for (ModelicaDeclaration decl: getDirectlyDeclared(firstName)) {
            ResolutionContext tmpContext = result.getState().createContext();
            ((ModelicaClassDeclaration) decl).lookupInInstanceScope(result, furtherNames);
            // According to "5.2 Enclosing classes" from MLS 3.4, the order of definitions inside the unnamed
            // enclosing class is unspecified, so handle name hiding with care.
            result.accumulate(tmpContext.get(ResolvableEntity.class));
        }
    }

    /**
     * Resolve <code>name</code> accounting for <code>fileFQCN</code>. To be called from {@link RootScope}.
     */
    void lookupGlobally(ResolutionContext result, CompositeName name) throws Watchdog.CountdownException {
        result.watchdogTick();
        CompositeName remainingNameComponents = name.matchPrefix(packageComponents);
        if (remainingNameComponents != null) {
            lookupLocally(result, remainingNameComponents);
        }
    }

    @Override
    public void resolveLexically(ResolutionContext result, CompositeName name) throws Watchdog.CountdownException {
        if (!isInDefaultPackage()) {
            // otherwise we would get it from the RootScope anyway
            lookupLocally(result, name);
        }
        ((AbstractModelicaScope) getParent()).resolveLexically(result, name);
    }

    @Override
    public String getRepresentation() {
        return "FILE";
    }

    public boolean isInDefaultPackage() {
        return fileFQCN.isEmpty();
    }

    public String getFileFQCN() {
        return fileFQCN;
    }
}
