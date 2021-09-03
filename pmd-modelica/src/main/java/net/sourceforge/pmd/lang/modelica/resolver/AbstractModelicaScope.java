/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.resolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Internal base class for Modelica lexical scopes, see {@link ModelicaScope} for the public API.
 */
abstract class AbstractModelicaScope implements ModelicaScope {
    private AbstractModelicaScope parent;
    private final List<ModelicaDeclaration> declarations = new ArrayList<>();
    private final Map<String, ArrayList<ModelicaDeclaration>> declarationsByName = new HashMap<>();

    void setParent(AbstractModelicaScope scope) {
        parent = scope;
    }

    @Override
    public ModelicaScope getParent() {
        return parent;
    }

    void addDeclaration(ModelicaDeclaration declaration) {
        String name = declaration.getSimpleDeclarationName();
        declarations.add(declaration);
        if (!declarationsByName.containsKey(name)) {
            declarationsByName.put(name, new ArrayList<ModelicaDeclaration>());
        }
        declarationsByName.get(name).add(declaration);
    }

    @Override
    public List<ModelicaDeclaration> getContainedDeclarations() {
        return Collections.unmodifiableList(declarations);
    }

    List<ModelicaDeclaration> getDirectlyDeclared(String simpleName) {
        List<ModelicaDeclaration> result = declarationsByName.get(simpleName);
        if (result != null) {
            return result;
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Resolves a name as if it is written inside this lexical scope in a file.
     */
    abstract void resolveLexically(ResolutionContext result, CompositeName name) throws Watchdog.CountdownException;

    @Override
    public <T extends ResolvableEntity> ResolutionResult<T> safeResolveLexically(Class<T> clazz, ResolutionState state, CompositeName name) {
        ResolutionContext result = state.createContext();
        try {
            resolveLexically(result, name);
        } catch (Watchdog.CountdownException e) {
            result.markTtlExceeded();
        }
        return result.get(clazz);
    }

    protected abstract String getRepresentation();

    // For testing purposes
    String getNestingRepresentation() {
        ModelicaScope parentScope = getParent();
        String prefix = "";
        if (parentScope != null) {
            prefix = ((AbstractModelicaScope) parentScope).getNestingRepresentation();
        }
        return prefix + "#" + getRepresentation();
    }

    @Override
    public RootScope getRoot() {
        return getParent().getRoot();
    }

    @Override
    public String toString() {
        return getRepresentation();
    }
}
