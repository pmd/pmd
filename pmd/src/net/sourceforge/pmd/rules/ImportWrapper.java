/*
 * User: tom
 * Date: Aug 23, 2002
 * Time: 8:43:14 PM
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

