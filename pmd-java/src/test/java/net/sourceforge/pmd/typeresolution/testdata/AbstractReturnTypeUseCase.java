/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

import java.util.List;

import net.sourceforge.pmd.typeresolution.testdata.dummytypes.AbstractReturnType;

public class AbstractReturnTypeUseCase {

    public void foo() {
        AbstractReturnType sample = new AbstractReturnType();
        List<String> list = sample.getList();
        System.out.println(list.size());
    }
}
