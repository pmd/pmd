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
    private Map<NameDeclaration, List<NameOccurrence>> nameDeclarations = new LinkedHashMap<NameDeclaration, List<NameOccurrence>>();

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
        return nameDeclarations;
    }

    @Override
    public <T extends NameDeclaration> Map<T, List<NameOccurrence>> getDeclarations(Class<T> clazz) {
        Map<T, List<NameOccurrence>> result = new LinkedHashMap<T, List<NameOccurrence>>();
        for (Map.Entry<NameDeclaration, List<NameOccurrence>> e : nameDeclarations.entrySet()) {
            if (clazz.isAssignableFrom(e.getKey().getClass())) {
                @SuppressWarnings("unchecked") // it's assignable from, so should be ok
                T cast = (T)e.getKey();
                result.put(cast, e.getValue());
            }
        }
        return result;
    }

    @Override
    public boolean contains(NameOccurrence occ) {
        for (NameDeclaration d : nameDeclarations.keySet()) {
            if (d.getImage().equals(occ.getImage())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addDeclaration(NameDeclaration declaration) {
        nameDeclarations.put(declaration, new ArrayList<NameOccurrence>());
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
        for (Map.Entry<NameDeclaration, List<NameOccurrence>> e : nameDeclarations.entrySet()) {
            if (e.getKey().getImage().equals(occurrence.getImage())) {
                result = e.getKey();
                e.getValue().add(occurrence);
            }
        }
        return result;
    }
}
