/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.rules;

public class ImportWrapper {
    private int line;
    private String name;

    public ImportWrapper(String name, int line) {
        this.name = name;
        this.line = line;
    }

    public boolean equals(Object other) {
        ImportWrapper i = (ImportWrapper) other;
        return i.getName().equals(getName());
    }

    public int hashCode() {
        return getName().hashCode();
    }

    public String getName() {
        return name;
    }

    public int getLine() {
        return line;
    }
}

