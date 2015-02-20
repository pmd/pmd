/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.symboltable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class for any {@link Scope}.
 * Provides useful default implementations.
 */
public abstract class AbstractScope implements Scope {

    private Scope parent;
    /** Stores the name declaration already sorted by class. */
    private Map<Class<? extends NameDeclaration>, Map<NameDeclaration, List<NameOccurrence>>> nameDeclarations =
            new LinkedHashMap<Class<? extends NameDeclaration>, Map<NameDeclaration,List<NameOccurrence>>>();

    @Override
    public Scope getParent() {
        return parent;
    }

    @Override
    public void setParent(Scope parent) {
        this.parent = parent;
    }

    @Override
    public Map<NameDeclaration, List<NameOccurrence>> getDeclarations() {
        Map<NameDeclaration, List<NameOccurrence>> result = new LinkedHashMap<NameDeclaration, List<NameOccurrence>>();
        for (Map<NameDeclaration, List<NameOccurrence>> e : nameDeclarations.values()) {
            result.putAll(e);
        }
        return result;
    }

    @Override
    public <T extends NameDeclaration> Map<T, List<NameOccurrence>> getDeclarations(Class<T> clazz) {
        @SuppressWarnings("unchecked")
        Map<T, List<NameOccurrence>> result = (Map<T, List<NameOccurrence>>)nameDeclarations.get(clazz);
        if (result == null) {
            result = new LinkedHashMap<T, List<NameOccurrence>>();
        }
        return result;
    }

    @Override
    public boolean contains(NameOccurrence occ) {
        for (NameDeclaration d : getDeclarations().keySet()) {
            if (d.getImage().equals(occ.getImage())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addDeclaration(NameDeclaration declaration) {
        Map<NameDeclaration, List<NameOccurrence>> declarationsPerClass = nameDeclarations.get(declaration.getClass());
        if (declarationsPerClass == null) {
            declarationsPerClass = new LinkedHashMap<NameDeclaration, List<NameOccurrence>>();
            nameDeclarations.put(declaration.getClass(), declarationsPerClass);
        }
        declarationsPerClass.put(declaration, new ArrayList<NameOccurrence>());
    }

    @Override
    public <T extends Scope> T getEnclosingScope(Class<T> clazz) {
        T result = null;
        Scope current = this;
        while (result == null && current != null) {
            if (clazz.isAssignableFrom(current.getClass())) {
                @SuppressWarnings("unchecked")
                T cast = (T)current;
                result = cast;
            }
            current = current.getParent();
        }
        return result;
    }

    @Override
    public NameDeclaration addNameOccurrence(NameOccurrence occurrence) {
        NameDeclaration result = null;
        for (Map.Entry<NameDeclaration, List<NameOccurrence>> e : getDeclarations().entrySet()) {
            if (e.getKey().getImage().equals(occurrence.getImage())) {
                result = e.getKey();
                e.getValue().add(occurrence);
            }
        }
        return result;
    }
}
