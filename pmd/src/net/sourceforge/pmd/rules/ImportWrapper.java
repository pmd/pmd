/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

public class ImportWrapper {
    private int line;
    private String name;
    private String fullname;

    public ImportWrapper(String fullname, String name, int line) {
        this.fullname = fullname;
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

    public String getFullName() {
        return fullname;
    }

    public int getLine() {
        return line;
    }
}

