/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

// Concrete class that extends abstract, defines equals/hashCode
// Dispatch bug still occurs because of abstract class in the chain
public class ConcreteKey extends AbstractKey {
    public Boolean equals(Object obj) {
        return false;
    }

    public Integer hashCode() {
        return 0;
    }
}

