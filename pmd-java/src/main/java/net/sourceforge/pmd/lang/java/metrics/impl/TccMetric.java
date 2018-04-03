/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.metrics.impl.visitors.TccAttributeAccessCollector;
import net.sourceforge.pmd.lang.metrics.MetricOptions;

/**
 * Tight class cohesion.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class TccMetric extends AbstractJavaClassMetric {


    @Override
    public double computeFor(ASTAnyTypeDeclaration node, MetricOptions options) {
        Map<String, Set<String>> usagesByMethod = new TccAttributeAccessCollector(node).start();

        int numPairs = numMethodsRelatedByAttributeAccess(usagesByMethod);
        int maxPairs = maxMethodPairs(usagesByMethod.size());

        return numPairs / (double) maxPairs;
    }


    /**
     * Gets the number of pairs of methods that use at least one attribute in common.
     *
     * @param usagesByMethod Map of method name to names of local attributes accessed
     *
     * @return The number of pairs
     */
    private int numMethodsRelatedByAttributeAccess(Map<String, Set<String>> usagesByMethod) {
        List<String> methods = new ArrayList<>(usagesByMethod.keySet());
        int methodCount = methods.size();
        int pairs = 0;

        if (methodCount > 1) {
            for (int i = 0; i < methodCount - 1; i++) {
                for (int j = i + 1; j < methodCount; j++) {
                    String firstMethodName = methods.get(i);
                    String secondMethodName = methods.get(j);

                    if (!Collections.disjoint(usagesByMethod.get(firstMethodName),
                                              usagesByMethod.get(secondMethodName))) {
                        pairs++;
                    }
                }
            }
        }
        return pairs;
    }


    /**
     * Calculates the number of possible method pairs of two methods.
     *
     * @param methods Number of methods in the class
     *
     * @return Number of possible method pairs
     */
    private int maxMethodPairs(int methods) {
        return methods * (methods - 1) / 2;
    }

}
