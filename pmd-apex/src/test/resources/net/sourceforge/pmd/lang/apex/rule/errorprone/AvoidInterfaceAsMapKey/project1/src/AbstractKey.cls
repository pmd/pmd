/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

public abstract class AbstractKey implements IKey {
    public Boolean equals(Object obj) {
        return false;
    }

    public Integer hashCode() {
        return 0;
    }
}

