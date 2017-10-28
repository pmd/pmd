/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

import java.util.Arrays;
import java.util.List;

public class GenericsArrays {

    @SuppressWarnings("unused")
    public void test(String[] params) {
        List<String> var = Arrays.asList(params);
        List<String> var2 = Arrays.<String>asList(params);
        List<String[]> var3 = Arrays.<String[]>asList(params);
    }
}
