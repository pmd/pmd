/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

import java.util.Objects;

public class MethodCallExpressionTypes {
    public void bar() {
        Objects.toString(null);
    }
}
