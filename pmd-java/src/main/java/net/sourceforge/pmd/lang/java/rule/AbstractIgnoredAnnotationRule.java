/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule;

import static net.sourceforge.pmd.properties.PropertyFactory.stringListProperty;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.java.ast.Annotatable;
import net.sourceforge.pmd.properties.PropertyDescriptor;

/**
 * @deprecated Internal API
 */
@Deprecated
@InternalApi
public abstract class AbstractIgnoredAnnotationRule extends AbstractJavaRule {

    private final PropertyDescriptor<List<String>> ignoredAnnotationsDescriptor
        = stringListProperty("ignoredAnnotations")
        .desc(defaultIgnoredAnnotationsDescription())
        .defaultValue(defaultSuppressionAnnotations())
        .build();

    protected Collection<String> defaultSuppressionAnnotations() {
        return Collections.emptyList();
    }

    protected String defaultIgnoredAnnotationsDescription() {
        return "Fully qualified names of the annotation types that should be ignored by this rule";
    }

    protected AbstractIgnoredAnnotationRule() {
        definePropertyDescriptor(ignoredAnnotationsDescriptor);
    }


    /**
     * Checks whether any annotation in ignoredAnnotationsDescriptor is present on the node.
     *
     * @param node
     *            the node to check
     * @return <code>true</code> if the annotation has been found, otherwise <code>false</code>
     */
    protected boolean hasIgnoredAnnotation(Annotatable node) {
        return getProperty(ignoredAnnotationsDescriptor).stream().anyMatch(node::isAnnotationPresent);
    }
}
