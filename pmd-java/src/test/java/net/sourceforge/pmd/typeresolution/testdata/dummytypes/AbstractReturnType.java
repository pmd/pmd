/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata.dummytypes;

import java.util.ArrayList;
import java.util.List;

public class AbstractReturnType {

    public List<String> getList() {
        return new ArrayList<>();
    }
}
