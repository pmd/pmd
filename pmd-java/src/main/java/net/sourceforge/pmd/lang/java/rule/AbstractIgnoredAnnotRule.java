/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule;

import java.util.Collection;
import java.util.Collections;

import net.sourceforge.pmd.lang.java.ast.Annotatable;
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


    /**
     * Checks whether any annotation is present on the node.
     *
     * @param node
     *            the node to check
     * @return <code>true</code> if the annotation has been found, otherwise <code>false</code>
     */
    protected boolean isAnyAnnotationPresent(Annotatable node) {
        return node.isAnyAnnotationPresent(getProperty(ignoredAnnotationsDescriptor));
    }
}
