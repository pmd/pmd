/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.IPositionProvider;

public class ImportWrapper {
    private IPositionProvider pos;
    private String name;
    private String fullname;

    public ImportWrapper(String fullname, String name, IPositionProvider pos) {
        this.fullname = fullname;
        this.name = name;
        this.pos = pos;
    }

    public boolean equals(Object other) {
        ImportWrapper i = (ImportWrapper) other;
        return i.getName().equals(name);
    }

    public int hashCode() {
        return getName().hashCode();
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullname;
    }

    public IPositionProvider getPositionProvider() {
        return pos;
    }
}

