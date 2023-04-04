/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import net.sourceforge.pmd.properties.PropertyBuilder.GenericPropertyBuilder;
import net.sourceforge.pmd.properties.PropertyFactory;

/**
 * Just to share names/ other stuff.
 *
 * @author Clément Fournier
 */
public final class CommonPropertyDescriptors {

    private CommonPropertyDescriptors() {

    }

    /**
     * The "minimum" property that previously was on StatisticalRule.
     */
    public static GenericPropertyBuilder<Integer> reportLevelProperty() {
        return PropertyFactory.intProperty("minimum");
    }

}
