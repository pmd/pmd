/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.test.schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Collection of individual rule tests. Each test contains a copy of the
 * rule.
 *
 * @author Clément Fournier
 */
public class RuleTestCollection {

    private final List<RuleTestDescriptor> tests = new ArrayList<>();
    private String absoluteUriToTestXmlFile;

    public void addTest(RuleTestDescriptor descriptor) {
        tests.add(Objects.requireNonNull(descriptor));
    }


    public List<RuleTestDescriptor> getTests() {
        return Collections.unmodifiableList(tests);
    }

    /**
     * Returns the last test of the collection which is focused.
     */
    public RuleTestDescriptor getFocusedTestOrNull() {
        RuleTestDescriptor focused = null;
        for (RuleTestDescriptor test : tests) {
            if (test.isFocused()) {
                focused = test;
            }
        }
        return focused;
    }

    public String getAbsoluteUriToTestXmlFile() {
        return absoluteUriToTestXmlFile;
    }

    public void setAbsoluteUriToTestXmlFile(String absoluteUriToTestXmlFile) {
        this.absoluteUriToTestXmlFile = absoluteUriToTestXmlFile;
    }
}
