/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule;

import java.util.Collection;
import java.util.Collections;

import net.sourceforge.pmd.properties.StringMultiProperty;

public class AbstractIgnoredAnnotRule extends AbstractJavaRule {

    protected final StringMultiProperty ignoredAnnotationsDescriptor
        = StringMultiProperty.named("ignoredAnnotations")
        .desc("Fully qualified names of the annotation types that should be ignored by this rule")
        .defaultValues(defaultSuppressionAnnotations())
        .build();

    protected Collection<String> defaultSuppressionAnnotations() {
        return Collections.emptyList();
    }

    public AbstractIgnoredAnnotRule() {
        definePropertyDescriptor(ignoredAnnotationsDescriptor);
    }
}
