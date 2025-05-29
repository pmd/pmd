/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.internal.AbstractIgnoredAnnotationRule;

import java.util.Collection;
import java.util.Set;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static net.sourceforge.pmd.util.CollectionUtil.setOf;

/**
 * This rule detects private methods, that are not used and can therefore be
 * deleted.
 */
public class UsePatternVariableRule extends AbstractIgnoredAnnotationRule {

    @Override
    protected Collection<String> defaultSuppressionAnnotations() {
        return listOf(
            "java.lang.Deprecated",
            "jakarta.annotation.PostConstruct",
            "jakarta.annotation.PreDestroy",
            "lombok.EqualsAndHashCode.Include"
        );
    }

    @Override
    public Object visit(ASTCompilationUnit file, Object param) {

        return null;
    }

}
