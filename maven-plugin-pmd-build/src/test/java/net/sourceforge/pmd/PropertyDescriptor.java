/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

/*
 * Note: This class is here in pmd-build to test the RuntimeRulePropertiesAnalyzer
 */

public interface PropertyDescriptor {

    String name();
    String description();
    Object defaultValue();
}
