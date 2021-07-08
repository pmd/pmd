/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.internal;

import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.JavaVisitorBase;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.metrics.JavaMetrics.ClassFanOutOption;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.metrics.MetricOptions;


/**
 * Visitor for the ClassFanOut metric.
 *
 * @author Andreas Pabst
 */
public final class ClassFanOutVisitor extends JavaVisitorBase<Set<JClassSymbol>, Void> {

    private static final ClassFanOutVisitor INCLUDE_JLANG = new ClassFanOutVisitor(true);
    private static final ClassFanOutVisitor EXCLUDE_JLANG = new ClassFanOutVisitor(false);

    private final boolean includeJavaLang;

    private ClassFanOutVisitor(boolean includeJavaLang) {
        this.includeJavaLang = includeJavaLang;
    }

    public static ClassFanOutVisitor getInstance(MetricOptions options) {
        if (options.getOptions().contains(ClassFanOutOption.INCLUDE_JAVA_LANG)) {
            return INCLUDE_JLANG;
        } else {
            return EXCLUDE_JLANG;
        }
    }

    @Override
    public Void visitExpression(ASTExpression node, Set<JClassSymbol> data) {
        check(node, data);
        return visitChildren(node, data);
    }

    @Override
    public Void visit(ASTClassOrInterfaceType node, Set<JClassSymbol> data) {
        check(node, data);
        return visitChildren(node, data);
    }

    private void check(TypeNode node, Set<JClassSymbol> classes) {
        JTypeMirror typeMirror = node.getTypeMirror();
        if (!(typeMirror instanceof JClassType)) {
            return;
        }
        JClassSymbol symbol = ((JClassType) typeMirror).getSymbol();
        if (shouldBeIncluded(symbol)) {
            classes.add(symbol);
        }
    }

    private boolean shouldBeIncluded(JClassSymbol classToCheck) {
        return includeJavaLang || !JClassSymbol.PRIMITIVE_PACKAGE.equals(classToCheck.getPackageName());
    }
}
