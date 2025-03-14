/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

public class LocalVars {
    
    public void aMethod() {
        String sealed = null;

        sealed = this.getClass().getName();

        // error: sealed or non-sealed local classes are not allowed
        // sealed class LocalSealedClass {}
    }
}
