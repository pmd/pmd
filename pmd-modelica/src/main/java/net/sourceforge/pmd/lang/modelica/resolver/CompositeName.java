/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.resolver;

/**
 * An immutable composite name representation for use in "pattern matching style".
 *
 * Supports lightweight splitting into first element and "tail" (everything else).
 */
public final class CompositeName {
    private static final CompositeName NIL = new CompositeName(null, null);
    static final String ROOT_PSEUDO_NAME = "";
    public static final String NAME_COMPONENT_SEPARATOR = ".";

    private String head;
    private CompositeName tail;

    private CompositeName(String head, CompositeName tail) {
        this.head = head;
        this.tail = tail;
    }

    private static CompositeName create(String[] components, int startIndex, int total, boolean isAbsolute) {
        if (isAbsolute) {
            return new CompositeName(ROOT_PSEUDO_NAME, create(components, startIndex, total, false));
        } else if (startIndex == total) {
            return NIL;
        } else {
            return new CompositeName(components[startIndex], create(components, startIndex + 1, total, false));
        }
    }

    public static CompositeName create(boolean isAbsolute, String[] components) {
        return create(components, 0, components.length, isAbsolute);
    }

    public static CompositeName create(boolean isAbsolute, String[] componenets, int prefixLength) {
        return create(componenets, 0, prefixLength, isAbsolute);
    }

    public static CompositeName create(String simpleName) {
        return new CompositeName(simpleName, NIL);
    }

    public boolean isEmpty() {
        return head == null;
    }

    public String getHead() {
        return head;
    }

    public CompositeName getTail() {
        return tail;
    }

    private CompositeName matchPrefix(String[] prefix, int currentIndex) {
        if (currentIndex == prefix.length) {
            return this;
        } else if (prefix[currentIndex].equals(head) && tail != null) {
            return tail.matchPrefix(prefix, currentIndex + 1);
        } else {
            return null;
        }
    }

    /**
     * Tries to match the <code>prefix</code> argument with the first elements of this name
     *
     * @return the remaining elements on success or null on failure
     */
    public CompositeName matchPrefix(String[] prefix) {
        return matchPrefix(prefix, 0);
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "Nil";
        } else {
            return head + "::" + tail.toString();
        }
    }
}
