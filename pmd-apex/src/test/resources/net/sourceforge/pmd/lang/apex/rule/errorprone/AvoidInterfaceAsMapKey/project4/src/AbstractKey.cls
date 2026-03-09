/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

// Abstract class with ONLY equals (no hashCode)
public abstract class AbstractKey implements IKey {
    public Boolean equals(Object obj) {
        return false;
    }
}

