/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

// Abstract class with ONLY hashCode (no equals)
public abstract class AbstractKey implements IKey {
    public Integer hashCode() {
        return 0;
    }
}

