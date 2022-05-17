/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.test.schema;

import java.util.List;
import java.util.Objects;

/**
 * @author Clément Fournier
 */
public class TestCollection {

    private List<TestDescriptor> tests;


    public void addTest(TestDescriptor descriptor) {
        tests.add(Objects.requireNonNull(descriptor));
    }

}
