/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl;

import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTExecutableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.metrics.JavaMetrics;
import net.sourceforge.pmd.lang.java.metrics.JavaMetrics.NcssOption;
import net.sourceforge.pmd.lang.metrics.MetricOption;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

/**
 * @author Clément Fournier
 */
public class NcssTestRule extends JavaIntMetricTestRule {

    static final PropertyDescriptor<Boolean> REPORT_CLASSES =
        PropertyFactory.booleanProperty("reportClasses")
                       .desc("...")
                       .defaultValue(false).build();

    public NcssTestRule() {
        super(JavaMetrics.NCSS);
        definePropertyDescriptor(REPORT_CLASSES);
    }

    @Override
    protected boolean reportOn(Node node) {
        return super.reportOn(node)
            && (node instanceof ASTExecutableDeclaration
            || getProperty(REPORT_CLASSES) && node instanceof ASTTypeDeclaration);
    }

    @Override
    protected Map<String, MetricOption> optionMappings() {
        Map<String, MetricOption> mappings = super.optionMappings();
        mappings.put(NcssOption.COUNT_IMPORTS.valueName(), NcssOption.COUNT_IMPORTS);
        return mappings;
    }
}
