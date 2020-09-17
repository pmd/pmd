/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

import java.util.List;

public class LambdaBug2783 {
    // https://github.com/pmd/pmd/issues/2783

    public Spec<String> test() {
        // cast, block body (the failing case)
        Spec<String> result = (Spec<String>) (a, b) -> {
            return a.toArray(String[]::new);
        };
        // no cast, block body
        result = (a, b) -> {
            return a.toArray(String[]::new);
        };
        // cast, expression body
        result = (Spec<String>) (a, b) -> a.toArray(String[]::new);

        // return position?
        return (Spec<String>) (a, b) -> {
            return a.toArray(String[]::new);
        };
    }

    interface Spec<T> {
        String[] process(List<T> var1, List<?> var2);
    }
}
