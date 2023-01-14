/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public class MethodCallExpressionTypes {
    public void objectsToString() {
        Objects.toString(null);
    }

    public void arraysAsList() {
        Collection<String> l = Arrays.asList("a");
    }
}
