/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.resolver;

import java.util.ArrayList;
import java.util.List;

/**
 * A pseudo lexical scope corresponding to "unnamed enclosing class" for top-level entities.
 * See "5.2 Enclosing Classes" from MLS 3.4.
 *
 * Unlike in MLS, this class aggregates <b>source file scopes</b>, not the top-level entities themselves.
 */
public final class RootScope extends AbstractModelicaScope {
    private final List<ModelicaSourceFileScope> sourceFiles = new ArrayList<>();

    void addSourceFile(ModelicaSourceFileScope sourceFile) {
        sourceFiles.add(sourceFile);
    }

    void resolveBuiltin(ResolutionContext result, CompositeName name) {
        if (!name.isEmpty() && name.getTail().isEmpty()) {
            String simpleName = name.getHead();
            for (ModelicaBuiltinType.BaseType tpe: ModelicaBuiltinType.BaseType.values()) {
                if (tpe.getName().equals(simpleName)) {
                    result.addCandidate(new ModelicaBuiltinType(tpe));
                }
            }
        }
    }

    @Override
    public void resolveLexically(ResolutionContext result, CompositeName name) throws Watchdog.CountdownException {
        CompositeName nameToLookup = CompositeName.ROOT_PSEUDO_NAME.equals(name.getHead()) ? name.getTail() : name;
        resolveBuiltin(result, name);
        for (ModelicaSourceFileScope sourceFile: sourceFiles) {
            ResolutionContext tmpContext = result.getState().createContext();
            sourceFile.lookupGlobally(tmpContext, nameToLookup);
            // According to "5.2 Enclosing classes" from MLS 3.4, the order of definitions inside the unnamed
            // enclosing class is unspecified, so handle name hiding with care.
            result.accumulate(tmpContext.get(ResolvableEntity.class));
        }
    }

    @Override
    public RootScope getRoot() {
        return this;
    }

    @Override
    public String toString() {
        return "<implicit root scope>";
    }

    @Override
    public String getRepresentation() {
        return "ROOT";
    }
}
