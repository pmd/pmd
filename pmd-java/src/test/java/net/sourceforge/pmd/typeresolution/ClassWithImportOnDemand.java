/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution;

import java.util.ArrayList;
import java.util.List;

public class ClassWithImportOnDemand {

    public List<?> foo() {
        return new ArrayList<>();
    }
}
