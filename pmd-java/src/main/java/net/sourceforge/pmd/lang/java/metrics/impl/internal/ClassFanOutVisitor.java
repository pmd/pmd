/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl.internal;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.mutable.MutableInt;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.metrics.impl.ClassFanOutMetric.ClassFanOutOption;
import net.sourceforge.pmd.lang.metrics.MetricOptions;


/**
 * Visitor for the ClassFanOut metric.
 *
 * @author Andreas Pabst
 */
public class ClassFanOutVisitor extends JavaParserVisitorAdapter {

    private static final String JAVA_LANG_PACKAGE_NAME = "java.lang";
    protected Set<Class<?>> classes = new HashSet<>();
    private final boolean includeJavaLang;

    @SuppressWarnings("PMD.UnusedFormalParameter")
    public ClassFanOutVisitor(MetricOptions options, JavaNode topNode) {
        includeJavaLang = options.getOptions().contains(ClassFanOutOption.INCLUDE_JAVA_LANG);
        // topNode is unused, but we'll need it if we want to discount lambdas
        // if we add it later, we break binary compatibility
    }

    @Override
    public Object visit(ASTClassOrInterfaceType node, Object data) {
        check(node, (MutableInt) data);
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTName node, Object data) {
        check(node, (MutableInt) data);
        return super.visit(node, data);
    }

    private void check(TypeNode node, MutableInt counter) {
        if (!classes.contains(node.getType()) && shouldBeIncluded(node.getType())) {
            classes.add(node.getType());
            counter.increment();
        }
    }

    private boolean shouldBeIncluded(Class<?> classToCheck) {
        if (includeJavaLang || classToCheck == null) {
            // include all packages
            return true;
        } else {
            // exclude the java.lang package
            Package packageToCheck = classToCheck.getPackage();
            if (packageToCheck == null) {
                return true;
            }

            return !JAVA_LANG_PACKAGE_NAME.equals(packageToCheck.getName());
        }
    }
}
